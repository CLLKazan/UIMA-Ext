package ru.kfu.itis.issst.uima.morph.lemmatizer

import java.io.File
import org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription
import org.uimafit.factory.AnalysisEngineFactory
import ru.ksu.niimm.cll.uima.morph.opencorpora.MorphologyAnnotator
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.CachedSerializedDictionaryResource
import ru.kfu.itis.issst.uima.depparser.mst.GeneratePipelineDescriptorForDepParsing
import ru.kfu.itis.cll.uima.io.IoUtils

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
    // TODO in JAVA 7
    // val fileWriter = Files.newBufferedWriter(Paths.get(pathToXML), StandardCharsets.UTF_8)
    val fileWriter = IoUtils.openBufferedWriter(new File(pathToXML))
    description.toXML(fileWriter)
    fileWriter.close()
  }
}
