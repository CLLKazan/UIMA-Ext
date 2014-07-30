package ru.kfu.itis.issst.uima.morph.lemmatizer

import scala.collection.JavaConversions._
import org.scalatest._
import org.uimafit.factory.AnalysisEngineFactory
import org.uimafit.pipeline.SimplePipeline
import org.uimafit.util.JCasUtil.select
import org.apache.uima.util.CasCreationUtils
import org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription
import java.io.File
import org.opencorpora.cas.{ Wordform, Word }
import org.apache.uima.cas.FeatureStructure
import ru.kfu.itis.issst.uima.morph.dictionary.MorphDictionaryAPIFactory
import ru.kfu.itis.issst.uima.morph.dictionary.MorphologyAnnotator
import ru.kfu.itis.cll.uima.util.PipelineDescriptorUtils
import ru.kfu.itis.issst.uima.tokenizer.TokenizerAPI
import ru.kfu.itis.issst.uima.segmentation.SentenceSplitterAPI
import ru.kfu.itis.issst.uima.postagger.PosTaggerAPI
import org.uimafit.factory.ExternalResourceFactory

/**
 * Created by fsqcds on 07/05/14.
 */
class LemmatizerSpec extends FlatSpec with Matchers {
  "Lemmatizer" should "generate correct lemmas" in {
    val lemmatizerDesc = Lemmatizer.createDescription()
    val aggregateDesc = PipelineDescriptorUtils.createAggregateDescription(
      // descriptions
      TokenizerAPI.getAEImport() :: SentenceSplitterAPI.getAEImport() :: PosTaggerAPI.getAEImport()
        :: lemmatizerDesc :: Nil,
      // names
      "tokenizer" :: "sentenc-splitter" :: "pos-tagger" :: "lemmatizer" :: Nil)
    // add dictionary
    val extDictDesc = MorphDictionaryAPIFactory.getMorphDictionaryAPI.getResourceDescriptionForCachedInstance
    extDictDesc.setName(PosTaggerAPI.MORPH_DICTIONARY_RESOURCE_NAME)
    PipelineDescriptorUtils.getResourceManagerConfiguration(aggregateDesc).addExternalResource(extDictDesc)
    ExternalResourceFactory.bindExternalResource(aggregateDesc,
      "lemmatizer/" + Lemmatizer.ResourceKeyDictionary, PosTaggerAPI.MORPH_DICTIONARY_RESOURCE_NAME)
    // 
    val jCas = CasCreationUtils.createCas(aggregateDesc).getJCas
    jCas.setDocumentText("Душа моя озарена неземной радостью. Oracle купил Sun")
    SimplePipeline.runPipeline(jCas, aggregateDesc)
    val lemmas = select(jCas, classOf[Word]).flatMap((word: Word) => {
      word.getWordforms.toArray.map((wordformFS: FeatureStructure) => {
        wordformFS.asInstanceOf[Wordform].getLemma
      })
    })
    lemmas should be(List("душа", "мой", "озарён", "неземной", "радость", "oracle", "купил", "sun"))
  }
}
