/**
 *
 */
package ru.kfu.itis.issst.uima.morph
import org.uimafit.component.JCasAnnotator_ImplBase
import org.apache.uima.jcas.JCas
import ru.kfu.cll.uima.tokenizer.fstype.NUM
import scala.collection.JavaConversions.iterableAsScalaIterable
import org.apache.uima.cas.text.AnnotationIndex
import org.opencorpora.cas.Word

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class NumProcessor extends JCasAnnotator_ImplBase {

  override def process(jCas: JCas) {
    for (num <- jCas.getAnnotationIndex(NUM.typeIndexID).asInstanceOf[AnnotationIndex[NUM]])
      makeWordFrom(jCas, num)
  }

  private def makeWordFrom(jCas: JCas, num: NUM) {
    val word = new Word(jCas)
    word.setBegin(num.getBegin)
    word.setEnd(num.getEnd)
    word.setToken(num)
    word.addToIndexes()
  }

}