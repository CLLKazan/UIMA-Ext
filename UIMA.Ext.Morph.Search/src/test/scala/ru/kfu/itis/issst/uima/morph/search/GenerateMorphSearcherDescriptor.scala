/**
 *
 */
package ru.kfu.itis.issst.uima.morph.search

import org.uimafit.factory.AnalysisEngineFactory._
import java.io.FileOutputStream
import org.apache.commons.io.IOUtils
/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
object GenerateMorphSearcherDescriptor {
  def main(args: Array[String]) {
    val outputPath = "src/main/resources/ru/kfu/itis/issst/uima/morph/search/MorphSearcher-template.xml"
    val desc = createPrimitiveDescription(classOf[MorphSearcher])
    val out = new FileOutputStream(outputPath)
    try {
      desc.toXML(out);
    } finally {
      IOUtils.closeQuietly(out);
    }
  }
}