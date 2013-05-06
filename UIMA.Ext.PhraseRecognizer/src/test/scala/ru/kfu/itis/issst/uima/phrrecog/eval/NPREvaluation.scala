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
object NPREvaluation extends Evaluation {

  protected override val pipelineDescName = "desc.ae.np-recognizer-aggregate"
  protected override val evaluationConfigPath = "desc/npr-evaluation.properties"

  def main(args: Array[String]) {
    if (args.length != 2)
      error("Usage: <inputDir> <xmiOutputDir>")
    val inputDirPath = args(0)
    val outputDirPath = args(1)

    runPipeline(inputDirPath, outputDirPath)
  }
}