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
import org.apache.uima.jcas.cas.FSArray
import org.opencorpora.cas.Wordform
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.{ MorphConstants => M }
import org.apache.uima.jcas.cas.StringArray

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

    // make wordforms
    val numrWf = new Wordform(jCas)
    numrWf.setPos(M.NUMR)
    numrWf.setWord(word)

    val adjWf = new Wordform(jCas)
    adjWf.setPos(M.ADJF)
    val adjWfGrammems = new StringArray(jCas, 1)
    adjWfGrammems.set(0, M.Anum)
    adjWf.setGrammems(adjWfGrammems)
    adjWf.setWord(word)

    val wfArr = new FSArray(jCas, 2)
    wfArr.set(0, numrWf)
    wfArr.set(1, adjWf)
    word.setWordforms(wfArr)

    word.addToIndexes()
  }

}