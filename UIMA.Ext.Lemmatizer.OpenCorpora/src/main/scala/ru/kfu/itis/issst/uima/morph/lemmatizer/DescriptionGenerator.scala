package ru.kfu.itis.issst.uima.morph.lemmatizer

import java.io.File
import org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription
import org.uimafit.factory.AnalysisEngineFactory
import ru.kfu.itis.cll.uima.io.IoUtils
import ru.kfu.itis.issst.uima.morph.dictionary.MorphDictionaryAPI
import org.apache.uima.resource.metadata.MetaDataObject
import ru.kfu.itis.issst.uima.tokenizer.TokenizerAPI
import ru.kfu.itis.issst.uima.segmentation.SentenceSplitterAPI
import org.apache.uima.resource.metadata.impl.Import_impl
import ru.kfu.itis.cll.uima.util.PipelineDescriptorUtils
import ru.kfu.itis.issst.uima.morph.dictionary.MorphDictionaryAPIFactory
import ru.kfu.itis.issst.uima.morph.dictionary.MorphologyAnnotator

object DescriptionGenerator {
  def getDescription() = {
    val extDictDesc = MorphDictionaryAPIFactory.getMorphDictionaryAPI.getResourceDescriptionForCachedInstance
    AnalysisEngineFactory.createPrimitiveDescription(classOf[Lemmatizer], MorphologyAnnotator.RESOURCE_KEY_DICTIONARY, extDictDesc)
  }

  def getDescriptionWithDep() = {
    val aeDescriptions = scala.collection.mutable.Map.empty[String, MetaDataObject]
    aeDescriptions("tokenizer") = TokenizerAPI.getAEImport()
    aeDescriptions("sentenceSplitter") = SentenceSplitterAPI.getAEImport()
    //
    val posTaggerDescImport = new Import_impl()
    posTaggerDescImport.setName("pos_tagger")
    aeDescriptions("pos-tagger") = posTaggerDescImport
    //
    aeDescriptions("lemmatizer") = getDescription()

    import scala.collection.JavaConversions._
    PipelineDescriptorUtils.createAggregateDescription(aeDescriptions)
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
