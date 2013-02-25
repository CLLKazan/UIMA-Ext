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
import ru.kfu.itis.issst.uima.phrrecog.cas.Phrase

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
          makeNPAnnotation(np)
          parseFrom(rest)
        }
        case Failure(_, _) =>
          // start from next anno
          parseFrom(reader.rest)
      }

  private def makeNPAnnotation(np: NP) {
    val head = np.noun
    val jCas = head.getCAS().getJCas()

    val phrase = new Phrase(jCas)
    phrase.setBegin(head.getBegin())
    phrase.setEnd(head.getEnd())

    phrase.setHead(head)

    val depsFsArray = new FSArray(jCas, np.deps.size)
    FSCollectionFactory.fillArrayFS(depsFsArray, np.deps)
    phrase.setDependents(depsFsArray)
    phrase.addToIndexes()
  }
}