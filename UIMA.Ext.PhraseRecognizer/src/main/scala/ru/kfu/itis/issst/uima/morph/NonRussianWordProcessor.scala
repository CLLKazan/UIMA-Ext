/**
 *
 */
package ru.kfu.itis.issst.uima.morph

import org.apache.uima.jcas.JCas
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.{ MorphConstants => M }
import scala.collection.JavaConversions.iterableAsScalaIterable
import ru.kfu.cll.uima.tokenizer.fstype.W
import org.apache.uima.cas.text.AnnotationIndex
import org.uimafit.util.CasUtil
import org.opencorpora.cas.Word

/**
 * @author Rinat Gareev
 *
 */
class NonRussianWordProcessor extends JCasAnnotator_ImplBase {

  def process(jCas: JCas) {
    val wordType = jCas.getCasType(Word.typeIndexID)
    for (w <- jCas.getAnnotationIndex(W.typeIndexID).asInstanceOf[AnnotationIndex[W]])
      if (CasUtil.selectCovered(wordType, w).isEmpty())
        makeWordFor(jCas, w)
  }

  private def makeWordFor(jCas: JCas, w: W) {
    val word = new Word(jCas)
    word.setBegin(w.getBegin)
    word.setEnd(w.getEnd)
    word.setToken(w)
    word.addToIndexes()
  }

}