/**
 *
 */
package ru.kfu.itis.issst.uima.benchmarking

import AEBenchmark._
import org.apache.uima.analysis_engine.AnalysisEngineDescription
import org.apache.uima.collection.CollectionReaderDescription
import scopt.OptionParser
import java.io.File
import org.uimafit.factory.ResourceCreationSpecifierFactory
import com.typesafe.config.ConfigFactory
import ru.kfu.itis.cll.uima.cpe.StatusCallbackListenerAdapter
import org.apache.uima.cas.CAS
import org.apache.uima.collection.EntityProcessStatus
import com.typesafe.scalalogging.StrictLogging
import org.apache.uima.util.ProcessTrace
import scala.collection.JavaConversions._
import org.apache.uima.util.ProcessTraceEvent

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class AEBenchmark(args: ArgConfig) extends StrictLogging {
  val cfg = ConfigFactory.load()

  def run() {
    // CPEBuilder
    // listeners
  }
}

private[benchmarking] class RecordingStatusCallbackListener extends StatusCallbackListenerAdapter with StrictLogging {
  override def entityProcessComplete(cas: CAS, epStatus: EntityProcessStatus) {
    if (epStatus.isException())
      logger.error("AE processing exception(s) is detected:\n{}", epStatus.getExceptions())
    else if (epStatus.isEntitySkipped())
      logger.warn("AE skipped a document!")
    else {
      entityProcessComplete(cas, epStatus.getProcessTrace())
    }
  }

  private def entityProcessComplete(cas: CAS, trace: ProcessTrace) {
    import ProcessTraceEvent._
    for (pte <- trace.getEvents())
      pte.getType() match {
        case ANALYSIS => // TODO
        case SERVICE => ???
        case _ => // do nothing
      }
  }
}

object AEBenchmark {

  case class ArgConfig(aeDesc: AnalysisEngineDescription = null,
    dataDesc: CollectionReaderDescription = null)

  private val cmdParser = new OptionParser[ArgConfig]("Analysis Engine Benchmark") {
    opt[File]("--ae") required () valueName ("<analysis-engine-desc-xml>") validate (validateFileExistence) action {
      (descFile, cfg) => cfg.copy(aeDesc = parseAEDesc(descFile))
    }
    opt[File]("--data") required () valueName ("<collection-reader-desc-xml>") validate (validateFileExistence) action {
      (descFile, cfg) => cfg.copy(dataDesc = parseColReaderDesc(descFile))
    }
  }

  def main(args: Array[String]) {
    cmdParser.parse(args, ArgConfig()) match {
      case Some(argCfg) => new AEBenchmark(argCfg).run()
      case None => sys.exit(1)
    }
  }

  private def parseAEDesc(f: File) =
    ResourceCreationSpecifierFactory.createResourceCreationSpecifier(f.getPath(), null).
      asInstanceOf[AnalysisEngineDescription]

  private def parseColReaderDesc(f: File) =
    ResourceCreationSpecifierFactory.createResourceCreationSpecifier(f.getPath(), null).
      asInstanceOf[CollectionReaderDescription]

  def validateFileExistence(f: File): Either[String, Unit] =
    if (f.isFile()) Right()
    else Left(s"$f is not an existing file")
}