/**
 *
 */
package ru.kfu.itis.issst.uima.benchmarking

import org.scalatest.FlatSpecLike
import AEBenchmark._
import java.io.File

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class AEBenchmarkSpec extends FlatSpecLike {

  "AEBenchmark" should "write csv with timings" in {
    AEBenchmark.main(Array(
      "--ae-name", "ru.kfu.itis.issst.uima.tokenizer.tokenizer-ae",
      "--data", "test-data/col-reader-desc.xml",
      "-o", "target/ae-benchmark-test-output.csv"))
  }

}