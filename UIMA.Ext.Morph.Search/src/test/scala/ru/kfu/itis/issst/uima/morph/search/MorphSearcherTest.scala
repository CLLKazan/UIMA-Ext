/**
 *
 */
package ru.kfu.itis.issst.uima.morph.search

import org.scalatest.FunSuite
import org.uimafit.factory.AnalysisEngineFactory._
import org.uimafit.factory.TypeSystemDescriptionFactory._
import org.uimafit.factory.ExternalResourceFactory._
import ru.kfu.cll.uima.tokenizer.InitialTokenizer
import ru.ksu.niimm.cll.uima.morph.opencorpora.MorphologyAnnotator
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.SerializedDictionaryResource
import java.io.File
import MorphSearcherTest._
import org.apache.commons.io.IOUtils
import grizzled.slf4j.Logging
import scala.collection.JavaConversions._
import ru.kfu.itis.cll.uima.cas.AnnotationUtils

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class MorphSearcherTest extends FunSuite with Logging {

  private val tokenizerDesc = {
    val tsDesc = createTypeSystemDescription("ru.kfu.cll.uima.tokenizer.tokenizer-TypeSystem")
    createPrimitiveDescription(classOf[InitialTokenizer], tsDesc)
  }
  private val morphDictDesc = {
    val dictPath = System.getProperty(SysPropMorphDictPath)
    if (dictPath == null) null
    else
      createExternalResourceDescription(classOf[SerializedDictionaryResource],
        new File(dictPath))
  }
  private val morphAnalyzerDesc = {
    val tsDesc = createTypeSystemDescription("org.opencorpora.morphology-ts")
    createPrimitiveDescription(classOf[MorphologyAnnotator], tsDesc,
      MorphologyAnnotator.RESOURCE_KEY_DICTIONARY, morphDictDesc)
  }
  private val morphSearcherDesc = {
    val tsDesc = createTypeSystemDescription("ts-morphSearcher-test")
    import MorphSearcher._
    createPrimitiveDescription(classOf[MorphSearcher], tsDesc,
      // config parameters
      ParamTargetAnnoType, "test.MorphMatch",
      ParamWordformFeature, "wordform",
      ParamSourceWordformsUrl, "file:./src/test/resources/test-wfs.txt",
      ResourceMorphDict, morphDictDesc)
  }
  private val testInput1 = {
    val cl = Thread.currentThread().getContextClassLoader()
    val is = cl.getResourceAsStream("test-input-1.txt")
    try {
      IOUtils.toString(is, "utf-8")
    } finally {
      is.close()
    }
  }

  private val testFunc = () => {
    val aeDesc = createAggregateDescription(tokenizerDesc, morphAnalyzerDesc, morphSearcherDesc)
    val ae = createAggregate(aeDesc)
    val cas = ae.newCAS()
    cas.setDocumentText(testInput1)
    ae.process(cas)

    val testType = cas.getTypeSystem().getType("test.MorphMatch")
    val testIdx = cas.getAnnotationIndex(testType)
    assert(testIdx.size() >= 3)
    info(testIdx.map(anno => (anno.getCoveredText(), anno))
      .mkString("MorphMatch annotations cover following spans:\n", "\n", ""))
  }
  private val testDesc = "Test MorphSearcher on test-input-1.txt"

  if (morphDictDesc == null)
    ignore(testDesc)(testFunc())
  else test(testDesc)(testFunc())

}

object MorphSearcherTest {
  val SysPropMorphDictPath = "morph.dictPath"
}