/**
 *
 */
package ru.kfu.itis.issst.uima.phrrecog.util

import org.uimafit.factory.AnalysisEngineFactory._
import org.uimafit.factory.TypeSystemDescriptionFactory._
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import ru.kfu.itis.issst.uima.morph.NumProcessor

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
object GenerateNumProcessorDescriptor {

  def main(args: Array[String]): Unit = {
    val importedTsDesc = createTypeSystemDescription(
      "org.opencorpora.morphology-ts", "ru.kfu.cll.uima.tokenizer.tokenizer-TypeSystem")
    val nprDesc = createPrimitiveDescription(classOf[NumProcessor], importedTsDesc)
    val os = new BufferedOutputStream(
      new FileOutputStream(
        "src/main/resources/ru/kfu/itis/issst/uima/morph/NumProcessor.xml"))
    try {
      nprDesc.toXML(os, true)
    } finally {
      os.close()
    }
  }

}