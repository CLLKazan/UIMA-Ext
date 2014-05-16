package ru.kfu.itis.issst.uima.morph.lemmatizer

import org.scalatest._
import ru.ksu.niimm.cll.uima.morph.ml.GeneratePipelineDescriptorForTPSAnnotator
import org.uimafit.factory.AnalysisEngineFactory
import org.uimafit.pipeline.SimplePipeline
import org.apache.uima.util.CasCreationUtils

/**
 * Created by fsqcds on 07/05/14.
 */
class LemmatizerSpec extends FlatSpec with Matchers {
  "Lemmatizer" should "do something" in {
    val posPipelineDesc = GeneratePipelineDescriptorForTPSAnnotator.getDescription;
    val lemmatizerDesc = AnalysisEngineFactory.createPrimitiveDescription(classOf[Lemmatizer])
    val aggregateDesc = AnalysisEngineFactory.createAggregateDescription(posPipelineDesc, lemmatizerDesc)
    val jCas = CasCreationUtils.createCas(aggregateDesc).getJCas;
    jCas.setDocumentText("Какой-то русский текст написан здесь.")
    SimplePipeline.runPipeline(jCas, posPipelineDesc, lemmatizerDesc)
  }
}
