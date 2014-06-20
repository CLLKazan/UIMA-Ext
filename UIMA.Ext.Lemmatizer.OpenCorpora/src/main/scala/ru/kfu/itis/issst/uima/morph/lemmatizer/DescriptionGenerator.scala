package ru.kfu.itis.issst.uima.morph.lemmatizer

import java.io.File

import org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription
import org.uimafit.factory.AnalysisEngineFactory

import ru.ksu.niimm.cll.uima.morph.opencorpora.MorphologyAnnotator
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.CachedSerializedDictionaryResource

object DescriptionGenerator {
  def getDescription() = {
    val extDictDesc = createExternalResourceDescription(classOf[CachedSerializedDictionaryResource], new File("dict.opcorpora.ser"))
    AnalysisEngineFactory.createPrimitiveDescription(classOf[Lemmatizer], MorphologyAnnotator.RESOURCE_KEY_DICTIONARY, extDictDesc)
  }
}
