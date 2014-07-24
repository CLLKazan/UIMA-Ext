package ru.kfu.itis.issst.uima.morph.lemmatizer

import scala.collection.JavaConversions._
import org.scalatest._
import ru.ksu.niimm.cll.uima.morph.ml.GeneratePipelineDescriptorForTPSAnnotator
import org.uimafit.factory.AnalysisEngineFactory
import org.uimafit.pipeline.SimplePipeline
import org.uimafit.util.JCasUtil.select
import org.apache.uima.util.CasCreationUtils
import org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.CachedSerializedDictionaryResource
import java.io.File
import ru.ksu.niimm.cll.uima.morph.opencorpora.MorphologyAnnotator
import org.opencorpora.cas.{Wordform, Word}
import org.apache.uima.cas.FeatureStructure
import ru.kfu.itis.issst.uima.morph.dictionary.MorphDictionaryAPIFactory

/**
 * Created by fsqcds on 07/05/14.
 */
class LemmatizerSpec extends FlatSpec with Matchers {
  "Lemmatizer" should "generate correct lemmas" in {
    val posPipelineDesc = GeneratePipelineDescriptorForTPSAnnotator.getDescription
    val extDictDesc = MorphDictionaryAPIFactory.getMorphDictionaryAPI.getResourceDescriptionForCachedInstance
    val lemmatizerDesc = AnalysisEngineFactory.createPrimitiveDescription(classOf[Lemmatizer], MorphologyAnnotator.RESOURCE_KEY_DICTIONARY, extDictDesc)
    val aggregateDesc = AnalysisEngineFactory.createAggregateDescription(posPipelineDesc, lemmatizerDesc)
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
