/**
 *
 */
package ru.kfu.itis.issst.uima.phrrecog
import org.apache.uima.jcas.JCas
import scala.collection.JavaConversions._
import ru.kfu.cll.uima.segmentation.fstype.Sentence
import org.apache.uima.cas.text.AnnotationFS
import org.uimafit.util.CasUtil
import input.AnnotationSpan
import org.uimafit.component.CasAnnotator_ImplBase
import org.apache.uima.cas.TypeSystem
import org.apache.uima.cas.Type
import org.opencorpora.cas.Word
import ru.kfu.itis.issst.uima.phrrecog.parsing.NPParsers
import org.apache.uima.cas.CAS
import ru.kfu.itis.issst.uima.phrrecog.parsing.NP
import scala.util.parsing.input.Reader
import org.uimafit.util.FSCollectionFactory
import org.apache.uima.jcas.cas.FSArray
import ru.kfu.itis.issst.uima.phrrecog.cas.NounPhrase
import scala.collection.immutable.TreeSet
import org.opencorpora.cas.Wordform

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class NPRecognizer extends CasAnnotator_ImplBase with NPParsers {
  private var wordType: Type = _

  override def typeSystemInit(ts: TypeSystem) {
    wordType = ts.getType(classOf[Word].getName)
  }

  override def process(cas: CAS): Unit =
    process(cas.getJCas())

  private def process(jCas: JCas) =
    jCas.getAnnotationIndex(Sentence.typeIndexID).foreach(processSpan(_))

  private def processSpan(span: AnnotationFS) {
    val spanWords = CasUtil.selectCovered(span.getCAS(), wordType, span)
      .asInstanceOf[java.util.List[Word]].toList
    parseFrom(new AnnotationSpan(spanWords).reader)
  }

  private def parseFrom(reader: Reader[Word]): Unit =
    if (!reader.atEnd)
      np(reader) match {
        case Success(np, rest) => {
          addToCas(np)
          parseFrom(rest)
        }
        case Failure(_, _) =>
          // start from next anno
          parseFrom(reader.rest)
      }

  private def addToCas(np: NP) {
    val npAnno = createNPAnnotation(np)
    npAnno.addToIndexes()
  }

  private def createNPAnnotation(np: NP): NounPhrase = {
    val head = np.noun
    val jCas = head.getCAS().getJCas()

    val phrase = new NounPhrase(jCas)
    phrase.setBegin(head.getWord.getBegin)
    phrase.setEnd(head.getWord.getEnd)

    phrase.setHead(head)
    np.prepOpt match {
      case Some(prep) => phrase.setPreposition(prep)
      case None =>
    }
    np.particleOpt match {
      case Some(particle) => phrase.setParticle(particle)
      case None =>
    }

    val depWordsFsArray = new FSArray(jCas, np.depWords.size)
    FSCollectionFactory.fillArrayFS(depWordsFsArray, TreeSet.empty[Wordform](wfOffsetComp) ++ np.depWords)
    phrase.setDependentWords(depWordsFsArray)

    // TODO low priority: add a sanity check to avoid infinite recursion
    val depAnnoQ = np.depNPs.map(createNPAnnotation(_))
    val depNPsFsArray = new FSArray(jCas, depAnnoQ.size)
    FSCollectionFactory.fillArrayFS(depNPsFsArray, depAnnoQ)
    phrase.setDependentPhrases(depNPsFsArray)

    phrase
  }
}