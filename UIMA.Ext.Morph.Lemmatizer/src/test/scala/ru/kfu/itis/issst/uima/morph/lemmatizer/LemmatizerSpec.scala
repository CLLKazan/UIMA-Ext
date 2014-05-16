package ru.kfu.itis.issst.uima.morph.lemmatizer

import org.scalatest._
import ru.ksu.niimm.cll.uima.morph.ml.GeneratePipelineDescriptorForTPSAnnotator
import org.uimafit.factory.AnalysisEngineFactory
import org.uimafit.pipeline.SimplePipeline
import org.apache.uima.util.CasCreationUtils
import org.uimafit.factory.ExternalResourceFactory.{createExternalResourceDescription, createDependencyAndBind}
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.{CachedSerializedDictionaryResource, MorphDictionaryHolder}
import java.io.File
import ru.ksu.niimm.cll.uima.morph.opencorpora.MorphologyAnnotator
import java.util.Arrays

/**
 * Created by fsqcds on 07/05/14.
 */
class LemmatizerSpec extends FlatSpec with Matchers {
  "Lemmatizer" should "be runnable" in {
    val posPipelineDesc = GeneratePipelineDescriptorForTPSAnnotator.getDescription
    val extDictDesc = createExternalResourceDescription(classOf[CachedSerializedDictionaryResource], new File("dict.opcorpora.ser"))
    val lemmatizerDesc = AnalysisEngineFactory.createPrimitiveDescription(classOf[Lemmatizer], MorphologyAnnotator.RESOURCE_KEY_DICTIONARY, extDictDesc)
    val aggregateDesc = AnalysisEngineFactory.createAggregateDescription(posPipelineDesc, lemmatizerDesc)
    val jCas = CasCreationUtils.createCas(aggregateDesc).getJCas
    jCas.setDocumentText("Какой-то русский текст написан здесь.")
    SimplePipeline.runPipeline(jCas, aggregateDesc)
  }
}
