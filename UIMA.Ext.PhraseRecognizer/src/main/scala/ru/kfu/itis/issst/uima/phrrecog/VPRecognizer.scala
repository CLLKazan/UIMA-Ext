/**
 *
 */
package ru.kfu.itis.issst.uima.phrrecog

import org.uimafit.component.JCasAnnotator_ImplBase
import org.apache.uima.jcas.JCas
import org.opencorpora.cas.Word
import org.apache.uima.cas.text.AnnotationFS
import com.google.common.collect.ComparisonChain
import scala.collection.mutable.ListBuffer
import org.apache.uima.jcas.tcas.Annotation
import org.uimafit.util.FSCollectionFactory
import org.opencorpora.cas.Wordform
import parsing.WordUtils._
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.{ MorphConstants => M }
import ru.kfu.cll.uima.segmentation.fstype.Sentence
import org.apache.uima.cas.Type
import org.uimafit.util.CasUtil
import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.JavaConversions.iterableAsScalaIterable
import VPRecognizer._
import scala.collection.mutable.HashSet
import scala.collection.mutable.Buffer
import org.apache.uima.jcas.cas.FSArray
import grizzled.slf4j.Logging
import ru.kfu.itis.issst.uima.phrrecog.cas.VerbPhrase

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class VPRecognizer extends JCasAnnotator_ImplBase with Logging {

  private var wordType: Type = _

  override def process(jCas: JCas) {
    wordType = jCas.getCasType(Word.typeIndexID)
    jCas.getAnnotationIndex(Sentence.typeIndexID).foreach(processSpan(jCas, _))
  }

  private def processSpan(jCas: JCas, spanAnno: Annotation) {
    val words = CasUtil.selectCovered(wordType, spanAnno).asInstanceOf[java.util.List[Word]]
    val wordforms = words.map(w =>
      if (w.getWordforms() == null) IndexedSeq.empty
      else FSCollectionFactory.create(w.getWordforms, classOf[Wordform]).toIndexedSeq)
    val verbalWfs =
      for (
        (wwfs, wIndex) <- wordforms.zipWithIndex;
        wf <- wwfs;
        if (VerbalPoSes.contains(wf.getPos()))
      ) yield (wIndex, wf)
    val attached = HashSet.empty[Wordform]
    for ((vwIndex, vwf) <- verbalWfs.reverse; if !attached.contains(vwf)) {
      val phrWfs = vwf.getPos match {
        case M.VERB => handleFiniteVerb(wordforms, vwIndex, vwf)
        case M.PRTS => handleShortPerfective(wordforms, vwIndex, vwf)
        case M.PRTF => List(vwf) // TODO handleFullPerfective(words, wIndex, wfIndex)
        case M.GRND => List(vwf) // TODO handleGerund(words, wIndex, wfIndex)
        case M.INFN => handleInfinitive(wordforms, vwIndex, vwf)
        case unknownPos => throw new UnsupportedOperationException("Unknown verbal word pos: %s"
          .format(unknownPos))
      }
      if (phrWfs != null && !phrWfs.isEmpty) {
        attached ++= phrWfs
        createPhraseAnnotation(jCas, phrWfs)
      }
    }
  }

  // returns phrase wordforms; first wf of iter is a head of phrase
  private def handleFiniteVerb(
    wordforms: Buffer[IndexedSeq[Wordform]],
    verbIndex: Int,
    verb: Wordform): Iterable[Wordform] = List(verb)

  private def handleShortPerfective(
    wordforms: Buffer[IndexedSeq[Wordform]],
    spIndex: Int,
    sp: Wordform): Iterable[Wordform] =
    if (spIndex > 0) {
      val toBeIdx = spIndex - 1
      wordforms(toBeIdx).find(hasLemma("есть", M.VERB) _) match {
        case Some(toBeWf) => toBeWf :: sp :: Nil
        case None => sp :: Nil
      }
    } else sp :: Nil

  private def handleInfinitive(
    wordforms: Buffer[IndexedSeq[Wordform]],
    infIndex: Int,
    inf: Wordform): Iterable[Wordform] = {
    val infW = inf.getWord
    debug("Handling infinitive: (%s,%s) %s".format(infW.getBegin, infW.getEnd, infW.getCoveredText))
    var head: Wordform = null
    // search to the left
    if (infIndex > 0) {
      findWf(wordforms.view(0, infIndex).reverse, isVerb) match {
        case Some(wf) => head = wf
        case None =>
      }
    }
    if (head != null) head :: inf :: Nil
    else inf :: Nil
  }

  private def findWf(wordforms: Seq[IndexedSeq[Wordform]], pred: Wordform => Boolean): Option[Wordform] = {
    var result: Option[Wordform] = None
    wordforms.find(wfs => {
      wfs.find(pred(_)) match {
        case opt @ Some(_) => {
          result = opt
          true
        }
        case None => false
      }
    })
    result
  }

  private def isVerb(wf: Wordform): Boolean = M.VERB == wf.getPos()

  private def hasLemma(lemmaString: String, pos: String)(wf: Wordform): Boolean =
    pos == wf.getPos() && lemmaString == wf.getLemma()

  private def createPhraseAnnotation(jCas: JCas, phraseWordforms: Iterable[Wordform]) {
    val iter = phraseWordforms.iterator
    val head = iter.next()
    val depsFsArray = new FSArray(jCas, phraseWordforms.size - 1)
    var depsI = 0
    while (iter.hasNext) {
      depsFsArray.set(depsI, iter.next())
      depsI += 1
    }
    val phrase = new VerbPhrase(jCas)
    phrase.setBegin(head.getWord.getBegin)
    phrase.setEnd(head.getWord.getEnd)
    phrase.setHead(head)
    phrase.setDependentWords(depsFsArray)
    phrase.addToIndexes()
  }

  private def getVerbalWfs(w: Word): TraversableOnce[Wordform] =
    if (w.getWordforms() == null) {
      List()
    } else {
      val resultList = ListBuffer.empty[Wordform]
      for (wf <- FSCollectionFactory.create(w.getWordforms(), classOf[Wordform]))
        if (VerbalPoSes.contains(wf.getPos()))
          resultList += wf
      resultList
    }
}

object VPRecognizer {
  val VerbalPoSes = Set(M.VERB, M.INFN, M.PRTF, M.PRTS, M.GRND)
}

// TODO move to some utility package 
class AnnotationOffsetOrdering[A <: AnnotationFS] extends Ordering[A] {
  override def compare(first: A, second: A) =
    ComparisonChain.start()
      .compare(first.getBegin(), second.getBegin())
      .compare(second.getEnd(), first.getEnd())
      .compare(first.getType().getName(), second.getType().getName())
      .result()
}