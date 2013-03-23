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

  private type WordformPointer = (Int, Int)

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
        (wf, wfIndex) <- wwfs.zipWithIndex;
        if (VerbalPoSes.contains(wf.getPos()))
      ) yield (wIndex, wfIndex)

    val attached = HashSet.empty[WordformPointer]
    for (vwCoord <- verbalWfs.reverse; if !attached.contains(vwCoord)) {
      val phrWfs = wordforms(vwCoord._1)(vwCoord._2).getPos() match {
        case M.VERB => handleFiniteVerb(wordforms, vwCoord)
        case M.PRTS => handleShortPerfective(wordforms, vwCoord)
        case M.PRTF => List(vwCoord) // TODO handleFullPerfective(words, wIndex, wfIndex)
        case M.GRND => List(vwCoord) // TODO handleGerund(words, wIndex, wfIndex)
        case M.INFN => handleInfinitive(wordforms, vwCoord)
        case unknownPos => throw new UnsupportedOperationException("Unknown verbal word pos: %s"
          .format(unknownPos))
      }
      if (phrWfs != null && !phrWfs.isEmpty) {
        attached ++= phrWfs
        createPhraseAnnotation(jCas, for ((wIndex, wf) <- phrWfs) yield words(wIndex))
      }
    }
  }

  // returns phrase words; first wf of iter is a head of phrase
  private def handleFiniteVerb(wordforms: Buffer[IndexedSeq[Wordform]],
    verbCoord: WordformPointer): Iterable[WordformPointer] =
    List(verbCoord)

  private def handleShortPerfective(wordforms: Buffer[IndexedSeq[Wordform]],
    spCoord: WordformPointer): Iterable[WordformPointer] = {
    val (spWordIndex, spWfIndex) = spCoord
    if (spWordIndex > 0) {
      val toBeWordIdx = spWordIndex - 1
      val toBeWfIdx = wordforms(spWordIndex - 1).indexWhere(hasLemma("есть", M.VERB))
      if (toBeWfIdx >= 0)
        (toBeWordIdx, toBeWfIdx) :: spCoord :: Nil
      else List(spCoord)
    } else
      List(spCoord)
  }

  private def handleInfinitive(wordforms: Buffer[IndexedSeq[Wordform]],
    infCoord: WordformPointer): Iterable[WordformPointer] = {
    debug("Handling infinitive: %s".format(wordforms(infCoord._1)(infCoord._2)))
    val (infWordIndex, infWfIndex) = infCoord
    var head: WordformPointer = null
    // search to the left
    if (infWordIndex > 0) {
      val (vIndex, vWfIndex) = findIndexOfWf(wordforms.view(0, infWordIndex).reverse, isVerb)
      if (vIndex >= 0)
        // ajdust index of reversed subview
        head = (infWordIndex - 1 - vIndex, vWfIndex)
    }
    if (head != null)
      List(head, infCoord)
    else List(infCoord)
  }

  private def findIndexOfWf(wordforms: Seq[IndexedSeq[Wordform]], pred: Wordform => Boolean): (Int, Int) = {
    var wfIndex = -1
    val wordIndex = wordforms.findIndexOf(wfs => {
      wfIndex = wfs.indexWhere(pred(_))
      wfIndex >= 0
    })
    (wordIndex, wfIndex)
  }

  private def isVerb(wf: Wordform): Boolean = {
    // trace("Checking whether wf is verb: %s".format(wf))
    M.VERB == wf.getPos()
  }

  private def hasLemma(lemmaString: String, pos: String)(wf: Wordform): Boolean =
    pos == wf.getPos() && lemmaString == wf.getLemma()

  private def createPhraseAnnotation(jCas: JCas, phraseWords: Iterable[Word]) {
    val iter = phraseWords.iterator
    val head = iter.next()
    val depsFsArray = new FSArray(jCas, phraseWords.size - 1)
    var depsI = 0
    while (iter.hasNext) {
      depsFsArray.set(depsI, iter.next())
      depsI += 1
    }
    val phrase = new VerbPhrase(jCas)
    phrase.setBegin(head.getBegin)
    phrase.setEnd(head.getEnd)
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