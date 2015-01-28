/**
 *
 */
package ru.kfu.itis.issst.uima.benchmarking

import org.scalatest.FlatSpecLike
import AEBenchmark._
import java.io.File
import com.typesafe.scalalogging.StrictLogging

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class AEBenchmarkSpec extends FlatSpecLike with StrictLogging {

  "AEBenchmark" should "write csv with timings" in {
    val b = new AEBenchmark(ArgConfig(
      aeDesc = createAEDesc(Left("ru.kfu.itis.issst.uima.tokenizer.tokenizer-ae")),
      dataDesc = parseColReaderDesc(new File("test-data/col-reader-desc.xml")),
      outputFile = new File("target/ae-benchmark-test-output.csv")))
    val cpe = b.run()
    while (cpe.isProcessing()) {
      logger.info("Test spec is waiting until CPE finished")
      Thread.sleep(1000)
    }
  }

}