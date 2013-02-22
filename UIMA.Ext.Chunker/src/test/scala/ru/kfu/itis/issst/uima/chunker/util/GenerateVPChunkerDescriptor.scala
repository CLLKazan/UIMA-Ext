/**
 *
 */
package ru.kfu.itis.issst.uima.chunker.util
import org.uimafit.factory.AnalysisEngineFactory._
import org.uimafit.factory.TypeSystemDescriptionFactory._
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import ru.kfu.itis.issst.uima.chunker.VPRecognizer

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
object GenerateVPChunkerDescriptor {

  def main(args: Array[String]): Unit = {
    val importedTsDesc = createTypeSystemDescription(
      "org.opencorpora.morphology-ts", "ru.kfu.itis.issst.uima.chunker.ts-chunking")
    val nprDesc = createPrimitiveDescription(classOf[VPRecognizer], importedTsDesc)
    val os = new BufferedOutputStream(
      new FileOutputStream(
        "src/main/resources/ru/kfu/itis/issst/uima/chunker/VPRecognizer.xml"))
    try {
      nprDesc.toXML(os, true)
    } finally {
      os.close()
    }
  }

}