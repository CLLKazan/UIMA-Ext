/**
 *
 */
package ru.kfu.itis.issst.uima.ner

import org.uimafit.factory.AnalysisEngineFactory._
import org.uimafit.factory.TypeSystemDescriptionFactory._
import java.io.BufferedOutputStream
import java.io.FileOutputStream

/**
 * @author Rinat Gareev
 *
 */
object GeneratePossibleNEAnnotatorDesc {

  def main(args: Array[String]) {
    val importedTsDesc = createTypeSystemDescription(
      "ru.kfu.cll.uima.tokenizer.tokenizer-TypeSystem", "org.opencorpora.morphology-ts",
      "ru.kfu.itis.issst.uima.phrrecog.ts-phrase-recognizer", "ru.kfu.itis.issst.uima.ner.ts-ner")
    val nerDesc = createPrimitiveDescription(classOf[PossibleNEAnnotator], importedTsDesc)
    val os = new BufferedOutputStream(
      new FileOutputStream(
        "src/main/resources/ru/kfu/itis/issst/uima/ner/PossibleNEAnnotator.xml"))
    try {
      nerDesc.toXML(os, true)
    } finally {
      os.close()
    }
  }

}