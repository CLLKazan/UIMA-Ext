/**
 *
 */
package ru.kfu.itis.issst.uima.phrrecog.util
import org.uimafit.factory.TypeSystemDescriptionFactory._
import org.uimafit.factory.CollectionReaderFactory
import ru.kfu.itis.cll.uima.consumer.XmiWriter
import org.uimafit.factory.AnalysisEngineFactory._
import ru.kfu.itis.cll.uima.cpe.CpeBuilder
import ru.kfu.itis.cll.uima.cpe.ReportingStatusCallbackListener
import ru.kfu.itis.cll.uima.cpe.FileDirectoryCollectionReader
import ru.kfu.itis.issst.uima.tokenizer.InitialTokenizer
import ru.kfu.itis.issst.uima.segmentation.ParagraphSplitter
import ru.kfu.itis.issst.uima.tokenizer.PostTokenizer

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
object StandoffAnnotationsToXmi {

  def main(args: Array[String]) {
    if (args.length != 3) {
      println("Usage: <annoStrParserFactoryClass> <inputDir> <outputDir>")
      exit(1)
    }
    val annoStrParserFactoryClsName = args(0)
    val inputDir = args(1)
    val outputDir = args(2)

    val colReaderDesc = {
      import FileDirectoryCollectionReader._
      val tsDesc = createTypeSystemDescription(
        "ru.kfu.itis.cll.uima.commons.Commons-TypeSystem")
      CollectionReaderFactory.createDescription(classOf[FileDirectoryCollectionReader],
        tsDesc,
        PARAM_DIRECTORY_PATH, inputDir)
    }

    val tokenizerDesc = {
      val tsDesc = createTypeSystemDescription(
        "ru.kfu.cll.uima.tokenizer.tokenizer-TypeSystem")
      createPrimitiveDescription(classOf[InitialTokenizer], tsDesc)
    }

    val postTokenizerDesc = {
      val tsDesc = createTypeSystemDescription(
        "ru.kfu.cll.uima.tokenizer.tokenizer-TypeSystem")
      createPrimitiveDescription(classOf[PostTokenizer], tsDesc)
    }

    val paraSplitterDesc = {
      val tsDesc = createTypeSystemDescription(
        "ru.kfu.cll.uima.segmentation.segmentation-TypeSystem")
      createPrimitiveDescription(classOf[ParagraphSplitter], tsDesc)
    }

    val standoffParserDesc = {
      import StandoffAnnotationsProcessor._
      val tsDesc = createTypeSystemDescription(
        "org.opencorpora.morphology-ts",
        "ru.kfu.itis.issst.uima.phrrecog.ts-phrase-recognizer")
      createPrimitiveDescription(classOf[StandoffAnnotationsProcessor], tsDesc,
        ParamAnnotationStringParserFactoryClass, annoStrParserFactoryClsName)
    }

    val xmiWriterDesc = {
      import XmiWriter._
      createPrimitiveDescription(classOf[XmiWriter],
        PARAM_OUTPUTDIR, outputDir)
    }

    // build cpe
    val cpeBuilder = new CpeBuilder
    cpeBuilder.setReader(colReaderDesc)
    val aeList = tokenizerDesc :: postTokenizerDesc :: paraSplitterDesc :: standoffParserDesc :: xmiWriterDesc :: Nil
    aeList.foreach(cpeBuilder.addAnalysisEngine(_))
    cpeBuilder.setMaxProcessingUnitThreatCount(1)
    val cpe = cpeBuilder.createCpe()
    cpe.addStatusCallbackListener(new ReportingStatusCallbackListener(cpe))
    println("About to start CPE...")
    cpe.process()
  }

}