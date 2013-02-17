/**
 *
 */
package ru.kfu.itis.issst.uima.chunker.util

import org.uimafit.factory.TypeSystemDescriptionFactory
import org.apache.uima.util.CasCreationUtils
import java.util.Arrays
import java.io.FileOutputStream
import org.apache.commons.io.IOUtils

/**
 * @author Rinat Gareev
 *
 */
object GenerateTypeDescForCasEditor {

  def main(args: Array[String]) {
    val outPath = "desc/types/TypeSystem4CasViewer.xml";
    var tsDesc = TypeSystemDescriptionFactory.createTypeSystemDescription(
      "ru.kfu.itis.issst.uima.chunker.ts-chunking",
      "ru.kfu.itis.cll.uima.commons.Commons-TypeSystem",
      "ru.kfu.cll.uima.tokenizer.tokenizer-TypeSystem",
      "ru.kfu.cll.uima.segmentation.segmentation-TypeSystem");
    tsDesc = CasCreationUtils.mergeTypeSystems(Arrays.asList(tsDesc));
    val fos = new FileOutputStream(outPath);
    try {
      tsDesc.toXML(fos);
    } finally {
      IOUtils.closeQuietly(fos);
    }
  }
}