package ru.kfu.itis.issst.uima.morph.lemmatizer

import scala.collection.JavaConversions._
import org.apache.uima.jcas.JCas
import org.uimafit.util.JCasUtil.select
import org.opencorpora.cas.{Word,Wordform}
import org.apache.uima.cas.FeatureStructure
import org.uimafit.descriptor.ExternalResource
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionaryHolder
import ru.ksu.niimm.cll.uima.morph.opencorpora.MorphologyAnnotator

/**
 * Created by fsqcds on 07/05/14.
 */
class Lemmatizer() extends org.uimafit.component.JCasAnnotator_ImplBase {
  @ExternalResource(key = MorphologyAnnotator.RESOURCE_KEY_DICTIONARY)
  private var dictHolder: MorphDictionaryHolder = null

  def process(aJCAS : JCas) {
    select(aJCAS, classOf[Word]).foreach((word: Word) => {
      val wordText = word.getCoveredText
      println(dictHolder.getDictionary.getEntries(wordText))
      println(wordText)
      word.getWordforms.toArray.foreach((wordform: FeatureStructure) =>
        println(wordform.asInstanceOf[Wordform].getGrammems)
      )
    })
  }
}
