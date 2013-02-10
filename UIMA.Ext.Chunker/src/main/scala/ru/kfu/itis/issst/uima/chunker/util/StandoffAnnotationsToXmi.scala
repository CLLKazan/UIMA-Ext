/**
 *
 */
package ru.kfu.itis.issst.uima.chunker.util
import org.uimafit.factory.TypeSystemDescriptionFactory._
import org.uimafit.factory.CollectionReaderFactory
import ru.kfu.itis.cll.uima.consumer.XmiWriter
import org.uimafit.factory.AnalysisEngineFactory._
import ru.kfu.itis.cll.uima.cpe.CpeBuilder
import ru.kfu.itis.cll.uima.cpe.ReportingStatusCallbackListener

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
object StandoffAnnotationsToXmi {

  def main(args: Array[String]) {
    if (args.length != 2) {
      println("Usage: <inputDir> <outputDir>")
      exit(1)
    }
    val inputDir = args(0)
    val outputDir = args(1)

    val tsDesc = createTypeSystemDescription(
      "ru.kfu.itis.cll.uima.commons.Commons-TypeSystem",
      "org.opencorpora.morphology-ts",
      "ru.kfu.itis.issst.uima.chunker.ts-chunking")

    val colReaderDesc = {
      import StandoffAnnotationsCollectionReader._
      CollectionReaderFactory.createDescription(classOf[StandoffAnnotationsCollectionReader],
        tsDesc,
        ParamInputDir, inputDir)
    }

    val xmiWriterDesc = {
      import XmiWriter._
      createPrimitiveDescription(classOf[XmiWriter],
        PARAM_OUTPUTDIR, outputDir)
    }

    // build cpe
    val cpeBuilder = new CpeBuilder
    cpeBuilder.setReader(colReaderDesc)
    val aeList = xmiWriterDesc :: Nil
    aeList.foreach(cpeBuilder.addAnalysisEngine(_))
    cpeBuilder.setMaxProcessingUnitThreatCount(1)
    val cpe = cpeBuilder.createCpe()
    cpe.addStatusCallbackListener(new ReportingStatusCallbackListener(cpe))
    println("About to start CPE...")
    cpe.process()
  }

}