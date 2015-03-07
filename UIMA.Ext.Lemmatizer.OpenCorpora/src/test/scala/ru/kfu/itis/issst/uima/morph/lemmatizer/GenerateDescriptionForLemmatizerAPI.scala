/**
 *
 */
package ru.kfu.itis.issst.uima.morph.lemmatizer

import org.apache.uima.fit.factory.ExternalResourceFactory
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import java.io.File

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
object GenerateDescriptionForLemmatizerAPI {

  def main(args: Array[String]): Unit = {
    val outputFile = new File("src/main/resources/" + LemmatizerAPI.AE_LEMMATIZER.replace('.', '/') + ".xml")
    val desc = Lemmatizer.createDescription()
    ExternalResourceFactory.bindExternalResource(desc,
      Lemmatizer.ResourceKeyDictionary, LemmatizerAPI.MORPH_DICTIONARY_RESOURCE_NAME)
    val out = FileUtils.openOutputStream(outputFile)
    try {
      desc.toXML(out)
    } finally {
      IOUtils.closeQuietly(out)
    }
  }

}