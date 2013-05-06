/**
 *
 */
package ru.kfu.itis.issst.uima.phrrecog.util
import org.uimafit.factory.AnalysisEngineFactory._
import ru.kfu.itis.issst.uima.phrrecog.NPRecognizer
import org.uimafit.factory.TypeSystemDescriptionFactory._
import java.io.BufferedOutputStream
import java.io.FileOutputStream

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
object GenerateNPRecognizerDescriptor {

  def main(args: Array[String]): Unit = {
    val nprImportedTsDesc = createTypeSystemDescription(
      "org.opencorpora.morphology-ts", "ru.kfu.itis.issst.uima.phrrecog.ts-phrase-recognizer")
    val nprDesc = createPrimitiveDescription(classOf[NPRecognizer], nprImportedTsDesc)
    val os = new BufferedOutputStream(
      new FileOutputStream(
        "src/main/resources/ru/kfu/itis/issst/uima/phrrecog/NPRecognizer.xml"))
    try {
      nprDesc.toXML(os, true)
    } finally {
      os.close()
    }
  }

}