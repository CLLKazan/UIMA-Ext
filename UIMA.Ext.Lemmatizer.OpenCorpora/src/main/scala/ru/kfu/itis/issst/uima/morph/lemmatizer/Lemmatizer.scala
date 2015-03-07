package ru.kfu.itis.issst.uima.morph.lemmatizer

import org.apache.uima.fit.component.JCasAnnotator_ImplBase

import scala.collection.JavaConversions._
import org.apache.uima.jcas.JCas
import org.apache.uima.fit.util.JCasUtil.select
import org.opencorpora.cas.{ Word, Wordform }
import ru.kfu.itis.issst.uima.morph.model.{ Wordform => DictWordform }
import org.apache.uima.cas.FeatureStructure
import org.apache.uima.fit.descriptor.ExternalResource
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionaryHolder
import ru.kfu.itis.issst.uima.morph.dictionary.WordUtils
import org.apache.uima.fit.factory.AnalysisEngineFactory

/**
 * Created by fsqcds on 07/05/14.
 */
class Lemmatizer extends JCasAnnotator_ImplBase {
  // TODO how to assign a scala val to a Java annotation attribute?
  @ExternalResource(key = "morphDictionary", mandatory = true)
  private var dictHolder: MorphDictionaryHolder = null

  def jaccardCoef(first: Set[String], second: Set[String]) = {
    (first & second).size.toDouble / (first | second).size
  }

  def findLemma(wordform: Wordform): String = {
    val dict = dictHolder.getDictionary
    val wordText = WordUtils.normalizeToDictionaryForm(wordform.getWord.getCoveredText)
    val entries = dict.getEntries(wordText)
    val targetGrammems = wordform.getGrammems

    if (entries.size > 0 && targetGrammems != null) {
      val lemmaId = dict.getEntries(wordText).maxBy((dictWf: DictWordform) => {
        val wfGrammems: Set[String] = dict.getGramModel().toGramSet(dictWf.getGrammems).toSet
        jaccardCoef(targetGrammems.toArray.toSet, wfGrammems)
      }).getLemmaId

      dictHolder.getDictionary.getLemma(lemmaId).getString
    } else {
      wordText
    }
  }

  def process(aJCAS: JCas) {
    select(aJCAS, classOf[Word]).foreach((word: Word) => {
      word.getWordforms.toArray.foreach((wordformFS: FeatureStructure) => {
        val wordform = wordformFS.asInstanceOf[Wordform]
        try {
          val lemma = findLemma(wordform)
          wordform.setLemma(lemma)
        } catch {
          case e: IndexOutOfBoundsException => {}
        }
      })
    })
  }
}

object Lemmatizer {
  val ResourceKeyDictionary = "morphDictionary"
  def createDescription() = AnalysisEngineFactory.createEngineDescription(classOf[Lemmatizer],
    LemmatizerAPI.getTypeSystemDescription)
}