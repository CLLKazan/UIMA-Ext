package ru.kfu.itis.issst.uima.morph.lemmatizer

import java.io.File
import java.nio.file.{Paths, Files}
import java.nio.charset.StandardCharsets

import org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription
import org.uimafit.factory.AnalysisEngineFactory

import ru.ksu.niimm.cll.uima.morph.opencorpora.MorphologyAnnotator
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.CachedSerializedDictionaryResource
import ru.kfu.itis.issst.uima.depparser.mst.GeneratePipelineDescriptorForDepParsing

object DescriptionGenerator {
  def getDescription() = {
    val extDictDesc = createExternalResourceDescription(classOf[CachedSerializedDictionaryResource], new File("dict.opcorpora.ser"))
    AnalysisEngineFactory.createPrimitiveDescription(classOf[Lemmatizer], MorphologyAnnotator.RESOURCE_KEY_DICTIONARY, extDictDesc)
  }

  def getDescriptionWithDep() = {
    val depPipelineDesc = GeneratePipelineDescriptorForDepParsing.getDescription()

    val lemmatizerDesc = getDescription()

    AnalysisEngineFactory.createAggregateDescription(depPipelineDesc, lemmatizerDesc)
  }

  def serializeDescriptionWithDep(pathToXML: String) = {
    val description = getDescriptionWithDep()
    val fileWriter = Files.newBufferedWriter(Paths.get(pathToXML), StandardCharsets.UTF_8)
    description.toXML(fileWriter)
    fileWriter.close()
  }
}
