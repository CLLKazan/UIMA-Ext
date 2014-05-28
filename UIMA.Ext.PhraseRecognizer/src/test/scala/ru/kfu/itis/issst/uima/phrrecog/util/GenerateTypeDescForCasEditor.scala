/**
 *
 */
package ru.kfu.itis.issst.uima.phrrecog.util

import org.uimafit.factory.TypeSystemDescriptionFactory
import org.apache.uima.util.CasCreationUtils
import java.util.Arrays
import java.io.FileOutputStream
import org.apache.commons.io.IOUtils
import ru.kfu.itis.issst.uima.tokenizer.TokenizerAPI
import ru.kfu.itis.issst.uima.segmentation.SentenceSplitterAPI

/**
 * @author Rinat Gareev
 *
 */
object GenerateTypeDescForCasEditor {

  def main(args: Array[String]) {
    val outPath = "desc/types/TypeSystem4CasViewer.xml";
    var tsDesc = TypeSystemDescriptionFactory.createTypeSystemDescription(
      "ru.kfu.itis.issst.uima.phrrecog.ts-phrase-recognizer",
      "ru.kfu.itis.cll.uima.commons.Commons-TypeSystem",
      TokenizerAPI.TYPESYSTEM_TOKENIZER,
      SentenceSplitterAPI.TYPESYSTEM_SENTENCES);
    tsDesc = CasCreationUtils.mergeTypeSystems(Arrays.asList(tsDesc));
    val fos = new FileOutputStream(outPath);
    try {
      tsDesc.toXML(fos);
    } finally {
      IOUtils.closeQuietly(fos);
    }
  }
}