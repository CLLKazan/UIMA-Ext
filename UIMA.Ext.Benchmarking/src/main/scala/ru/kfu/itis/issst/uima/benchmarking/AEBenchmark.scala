/**
 *
 */
package ru.kfu.itis.issst.uima.benchmarking

import AEBenchmark._
import org.apache.uima.analysis_engine.AnalysisEngineDescription
import org.apache.uima.collection.CollectionReaderDescription
import scopt.OptionParser
import java.io.File
import org.apache.uima.fit.factory.ResourceCreationSpecifierFactory
import com.typesafe.config.ConfigFactory
import ru.kfu.itis.cll.uima.cpe.StatusCallbackListenerAdapter
import org.apache.uima.cas.CAS
import org.apache.uima.collection.EntityProcessStatus
import com.typesafe.scalalogging.StrictLogging
import org.apache.uima.util.ProcessTrace
import scala.collection.JavaConversions._
import org.apache.uima.util.ProcessTraceEvent
import ru.kfu.itis.cll.uima.util.DocumentUtils
import com.github.tototoshi.csv.CSVWriter
import ru.kfu.itis.cll.uima.cpe.CpeBuilder
import org.apache.uima.resource.metadata.impl.Import_impl
import ru.kfu.itis.cll.uima.util.PipelineDescriptorUtils
import org.apache.uima.collection.CollectionProcessingEngine
import scala.concurrent.{ promise, Await }
import scala.concurrent.duration.Duration

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class AEBenchmark(args: ArgConfig) extends StrictLogging {
  private val cpeRunPromise = promise[Boolean]
  private val outLock = new AnyRef()
  private val csvWriter = CSVWriter.open(args.outputFile)
  writeHeader()

  def run() {
    val cpeBuilder = new CpeBuilder()
    cpeBuilder.setMaxProcessingUnitThreatCount(1)
    cpeBuilder.setReader(args.dataDesc)
    cpeBuilder.addAnalysisEngine(args.aeDesc)
    val cpe = cpeBuilder.createCpe()
    // listeners
    cpe.addStatusCallbackListener(recordingStatusCallbackListener)
    // run
    cpe.process()
    //
    Await.ready(cpeRunPromise.future, Duration.Inf)
  }

  private val recordingStatusCallbackListener = new StatusCallbackListenerAdapter {
    override def entityProcessComplete(cas: CAS, epStatus: EntityProcessStatus) {
      if (epStatus.isException())
        logger.error("AE processing exception(s) is detected:\n{}", epStatus.getExceptions())
      else if (epStatus.isEntitySkipped())
        logger.warn("AE skipped a document!")
      else {
        entityProcessComplete(cas, epStatus.getProcessTrace())
      }
    }

    override def collectionProcessComplete() {
      csvWriter.close()
      logger.info("Finished.")
      cpeRunPromise.success(true)
    }

    override def aborted() {
      logger.info("Aborted.")
      cpeRunPromise.success(false)
    }

    private def entityProcessComplete(cas: CAS, trace: ProcessTrace) {
      def processEvent(ev: ProcessTraceEvent) {
        logger.debug("ProcessTraceEvent with type {}", ev.getType())
        ev.getSubEvents().foreach(processEvent)
        import ProcessTraceEvent._
        ev.getType() match {
          case "Analysis" => writeEvent(cas, ev)
          case ANALYSIS => writeEvent(cas, ev)
          case SERVICE => ??? // TODO
          case _ =>
        }
      }
      trace.getEvents().foreach(processEvent)
    }

    private def writeEvent(cas: CAS, pte: ProcessTraceEvent) {
      write(AnalysisRecord(
        docURI = DocumentUtils.getDocumentUri(cas),
        docSize = cas.getDocumentText().length(),
        analyzerName = pte.getComponentName(),
        durationMS = pte.getDuration(),
        casSizeKb = cas.size()))
    }
  }

  private def write(rec: AnalysisRecord) = outLock.synchronized {
    import rec._
    csvWriter.writeRow(List(docURI, docSize, analyzerName, durationMS, casSizeKb))
  }

  private def writeHeader() = outLock.synchronized {
    csvWriter.writeRow(List("DocURI", "DocSize", "Analyzer", "DurationMs", "CasSizeKb"))
  }
}

private[benchmarking] case class AnalysisRecord(
  docURI: String, docSize: Int,
  analyzerName: String, durationMS: Int, casSizeKb: Int)

object AEBenchmark {

  case class ArgConfig(aeDesc: AnalysisEngineDescription = null,
    dataDesc: CollectionReaderDescription = null,
    outputFile: File = null)

  private val RootAEName = "RootAE";

  private val cmdParser = new OptionParser[ArgConfig]("Analysis Engine Benchmark") {
    opt[File]("ae-path") valueName ("<analysis-engine-desc-xml>") validate (validateFileExistence) action {
      (descFile, cfg) => cfg.copy(aeDesc = createAEDesc(Right(descFile)))
    }
    opt[String]("ae-name") valueName ("<analysis-engine-FQN>") action {
      (aeName, cfg) => cfg.copy(aeDesc = createAEDesc(Left(aeName)))
    }
    opt[File]("data") required () valueName ("<collection-reader-desc-xml>") validate (validateFileExistence) action {
      (descFile, cfg) => cfg.copy(dataDesc = parseColReaderDesc(descFile))
    }
    opt[File]('o', "out") required () valueName ("<output-file>") action {
      (outFile, cfg) => cfg.copy(outputFile = outFile)
    }
  }

  def main(args: Array[String]) {
    cmdParser.parse(args, ArgConfig()) match {
      case Some(argCfg) => new AEBenchmark(argCfg).run()
      case None => sys.exit(1)
    }
  }

  private[benchmarking] def createAEDesc(importValue: Either[String, File]) = {
    val `import` = new Import_impl()
    importValue match {
      case Left(name) => `import`.setName(name)
      case Right(path) => `import`.setLocation(path.getPath());
    }
    val aeDesc = PipelineDescriptorUtils.createAggregateDescription(Map("rootAE" -> `import`))
    aeDesc.getMetaData().setName(RootAEName)
    aeDesc
  }

  private[benchmarking] def parseColReaderDesc(f: File) =
    ResourceCreationSpecifierFactory.createResourceCreationSpecifier(f.getPath(), null).
      asInstanceOf[CollectionReaderDescription]

  def validateFileExistence(f: File): Either[String, Unit] =
    if (f.isFile()) Right()
    else Left(s"$f is not an existing file")
}