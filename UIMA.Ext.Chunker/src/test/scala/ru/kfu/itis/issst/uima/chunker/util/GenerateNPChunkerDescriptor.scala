/**
 *
 */
package ru.kfu.itis.issst.uima.chunker.util
import org.uimafit.factory.AnalysisEngineFactory._
import ru.kfu.itis.issst.uima.chunker.NPRecognizer
import org.uimafit.factory.TypeSystemDescriptionFactory._
import java.io.BufferedOutputStream
import java.io.FileOutputStream

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
object GenerateNPChunkerDescriptor {

  def main(args: Array[String]): Unit = {
    val nprImportedTsDesc = createTypeSystemDescription(
      "org.opencorpora.morphology-ts", "ru.kfu.itis.issst.uima.chunker.ts-chunking")
    val nprDesc = createPrimitiveDescription(classOf[NPRecognizer], nprImportedTsDesc)
    val os = new BufferedOutputStream(
      new FileOutputStream(
        "src/main/resources/ru/kfu/itis/issst/uima/chunker/NPRecognizer.xml"))
    try {
      nprDesc.toXML(os, true)
    } finally {
      os.close()
    }
  }

}