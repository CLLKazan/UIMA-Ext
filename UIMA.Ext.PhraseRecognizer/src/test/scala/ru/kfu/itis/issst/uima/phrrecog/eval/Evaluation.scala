/**
 *
 */
package ru.kfu.itis.issst.uima.phrrecog.eval
import org.uimafit.factory.AnalysisEngineFactory._
import ru.kfu.itis.cll.uima.consumer.XmiWriter
import org.uimafit.factory.CollectionReaderFactory
import ru.kfu.itis.cll.uima.cpe.FileDirectoryCollectionReader
import org.uimafit.factory.TypeSystemDescriptionFactory._
import ru.kfu.itis.cll.uima.cpe.CpeBuilder
import ru.kfu.itis.cll.uima.cpe.ReportingStatusCallbackListener
import java.util.Properties
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.FileInputStream
import ru.kfu.itis.cll.uima.eval.EvaluationLauncher
import ru.kfu.itis.cll.uima.cpe.StatusCallbackListenerAdapter

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
trait Evaluation {

  protected def pipelineDescName: String
  protected def evaluationConfigPath: String

  protected def runPipeline(inputDirPath: String, outputDirPath: String) {
    val colReaderDesc = {
      import FileDirectoryCollectionReader._
      val tsDesc = createTypeSystemDescription("ru.kfu.itis.cll.uima.commons.Commons-TypeSystem")
      CollectionReaderFactory.createDescription(classOf[FileDirectoryCollectionReader],
        tsDesc, PARAM_DIRECTORY_PATH, inputDirPath);
    }

    val pipelineDesc = createAnalysisEngineDescription(pipelineDescName)

    val xmiWriterDesc = {
      import XmiWriter._
      createPrimitiveDescription(classOf[XmiWriter],
        PARAM_OUTPUTDIR, outputDirPath);
    }

    // build cpe
    val cpeBuilder = new CpeBuilder
    cpeBuilder.setReader(colReaderDesc)
    val aeList = pipelineDesc :: xmiWriterDesc :: Nil
    aeList.foreach(cpeBuilder.addAnalysisEngine(_))
    cpeBuilder.setMaxProcessingUnitThreatCount(1)
    val cpe = cpeBuilder.createCpe()
    cpe.addStatusCallbackListener(new ReportingStatusCallbackListener(cpe))
    cpe.addStatusCallbackListener(new StatusCallbackListenerAdapter {
      override def collectionProcessComplete() {
        evaluate(inputDirPath, outputDirPath)
      }
    })
    println("About to start CPE...")
    cpe.process()
  }

  private def evaluate(goldDirPath: String, sysoutDirPath: String) {
    val evalProps = new Properties
    val evalPropsReader = {
      new BufferedReader(
        new InputStreamReader(
          new FileInputStream(evaluationConfigPath), "utf-8"));
    }
    try {
      evalProps.load(evalPropsReader)
    } finally {
      evalPropsReader.close()
    }
    evalProps.setProperty("goldCasDirectory.dir", goldDirPath)
    evalProps.setProperty("systemCasDirectory.dir", sysoutDirPath)

    EvaluationLauncher.runUsingProperties(evalProps)
  }

}