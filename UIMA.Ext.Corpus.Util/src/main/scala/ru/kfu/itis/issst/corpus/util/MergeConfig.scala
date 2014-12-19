/**
 *
 */
package ru.kfu.itis.issst.corpus.util

import java.io.File
import org.apache.commons.io.FileUtils
import scala.collection.JavaConversions._

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class MergeConfig(val annotationTypes: Set[String])

object MergeConfig {
  def read(f: File): MergeConfig = new MergeConfig(FileUtils.readLines(f).toSet)
}