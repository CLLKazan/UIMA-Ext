package ru.kfu.itis.issst.uima.morph.lemmatizer

import java.io.File
import org.apache.uima.fit.factory.ExternalResourceFactory.createExternalResourceDescription
import org.apache.uima.fit.factory.AnalysisEngineFactory
import ru.kfu.itis.cll.uima.io.IoUtils
import org.apache.uima.resource.metadata.MetaDataObject
import ru.kfu.itis.issst.uima.tokenizer.TokenizerAPI
import ru.kfu.itis.issst.uima.segmentation.SentenceSplitterAPI
import org.apache.uima.resource.metadata.impl.Import_impl
import ru.kfu.itis.cll.uima.util.PipelineDescriptorUtils
import ru.kfu.itis.issst.uima.postagger.PosTaggerAPI
import ru.kfu.itis.issst.uima.morph.dictionary.MorphDictionaryAPIFactory

object DescriptionGenerator {

  def getDescriptionWithDep() = {
    val aeDescriptions = scala.collection.mutable.Map.empty[String, MetaDataObject]
    aeDescriptions("tokenizer") = TokenizerAPI.getAEImport()
    aeDescriptions("sentenceSplitter") = SentenceSplitterAPI.getAEImport()
    //
    val posTaggerDescImport = new Import_impl()
    posTaggerDescImport.setName("pos_tagger")
    aeDescriptions("pos-tagger") = posTaggerDescImport
    //
    aeDescriptions("lemmatizer") = Lemmatizer.createDescription()
    //
    import scala.collection.JavaConversions._
    val aggrDesc = PipelineDescriptorUtils.createAggregateDescription(aeDescriptions)
    //
    // bind MorphDictionary
    val morphDictDesc = MorphDictionaryAPIFactory.getMorphDictionaryAPI().getResourceDescriptionForCachedInstance()
    morphDictDesc.setName(PosTaggerAPI.MORPH_DICTIONARY_RESOURCE_NAME)
    PipelineDescriptorUtils.getResourceManagerConfiguration(aggrDesc).addExternalResource(morphDictDesc)
    aggrDesc
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
