package ru.kfu.itis.issst.uima.morph.lemmatizer

import scala.collection.JavaConversions._
import org.apache.uima.jcas.JCas
import org.uimafit.util.JCasUtil.select
import org.opencorpora.cas.Wordform

/**
 * Created by fsqcds on 07/05/14.
 */
class Lemmatizer extends org.uimafit.component.JCasAnnotator_ImplBase {
  def process(aJCAS : JCas) {
    select(aJCAS, classOf[Wordform]).foreach((wf: Wordform) =>
      println(wf.getGrammems)
    )
  }
}
