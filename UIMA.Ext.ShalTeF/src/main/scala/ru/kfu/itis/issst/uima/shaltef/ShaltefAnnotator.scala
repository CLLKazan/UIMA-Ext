package ru.kfu.itis.issst.uima.shaltef

import org.apache.uima.jcas.JCas
import org.apache.uima.cas.CAS
import org.apache.uima.UimaContext
import org.apache.uima.analysis_component.CasAnnotator_ImplBase
import org.apache.uima.cas.TypeSystem
import ShaltefAnnotator._
import ru.kfu.itis.cll.uima.util.AnnotatorUtils._
import scala.collection.mutable
import java.net.URL
import ru.kfu.itis.issst.uima.shaltef.mappings.MappingsParser
import ru.kfu.itis.issst.uima.shaltef.mappings.DepToArgMappingsHolder
import org.apache.uima.cas.Type
import ru.kfu.cll.uima.segmentation.fstype.Sentence
import scala.collection.JavaConversions.iterableAsScalaIterable
import org.uimafit.util.CasUtil
import org.opencorpora.cas.Word
import java.{ util => jul }
import scala.{ collection => sc }
import ru.kfu.itis.issst.uima.phrrecog
import org.uimafit.util.FSCollectionFactory
import org.opencorpora.cas.Wordform
import org.apache.uima.cas.text.AnnotationFS
import ru.kfu.itis.issst.uima.phrrecog.cas.Phrase
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.PhrasePattern
import org.apache.uima.cas.Feature
import ru.kfu.itis.issst.uima.phrrecog.cas.NounPhrase
import scala.collection.mutable.ListBuffer
import ru.kfu.itis.issst.uima.shaltef.mappings.DepToArgMappingsHolder
import ru.kfu.itis.issst.uima.shaltef.mappings.SlotMapping
import ru.kfu.itis.issst.uima.shaltef.mappings.DepToArgMappingsBuilder
import ru.kfu.itis.issst.uima.shaltef.mappings.MappingsParser
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.SerializedDictionaryResource
import ru.kfu.itis.issst.uima.shaltef.mappings.TextualMappingsParser
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.MatchingContext

class ShaltefAnnotator extends CasAnnotator_ImplBase {

  private val templateType2File = mutable.Map.empty[String, URL]
  private var mappingParser: MappingsParser = _
  private var mappingsHolder: DepToArgMappingsHolder = _
  // CAS types
  private var ts: TypeSystem = _
  private var wordType: Type = _

  override def initialize(ctx: UimaContext) {
    super.initialize(ctx)
    val templMappingFileStrings = ctx.getConfigParameterValue(ParamTemplateMappingFiles)
      .asInstanceOf[Array[String]]
    mandatoryParam(ParamTemplateMappingFiles, templMappingFileStrings)
    for (templMappingFileStr <- templMappingFileStrings)
      templMappingFileStr.split(":") match {
        case Array(templateAnnoType, urlStr) => {
          templateType2File += (templateAnnoType -> new URL(urlStr))
        }
        case otherArr => throw new IllegalArgumentException(
          "Can't parse templateFile param value %s".format(otherArr))
      }
    // get dict object
    val morphDictKey = ResourceKeyMorphDict
    val morphDict = ctx.getResourceObject(morphDictKey) match {
      case sdr: SerializedDictionaryResource => sdr.getDictionary()
      case someRes => throw new IllegalStateException(
        "Unknown resource under key %s: %s".format(morphDictKey, someRes))
    }
    mappingParser = TextualMappingsParser(morphDict)
  }

  override def typeSystemInit(ts: TypeSystem) {
    this.ts = ts
    wordType = ts.getType(classOf[Word].getName)
    val mappingsBuilder = DepToArgMappingsBuilder()
    // parse mapping files
    for ((templateAnnoTypeName, url) <- templateType2File) {
      val templateAnnoType = ts.getType(templateAnnoTypeName)
      annotationTypeExist(templateAnnoTypeName, templateAnnoType)
      mappingParser.parse(url, templateAnnoType, mappingsBuilder)
    }
    mappingsHolder = mappingsBuilder.build()
  }

  override def process(cas: CAS): Unit = process(cas.getJCas())

  private def process(jCas: JCas) {
    for (
      segm <- jCas.getAnnotationIndex(Sentence.typeIndexID);
      w <- CasUtil.selectCovered(wordType, segm).asInstanceOf[jul.List[Word]];
      if w.getWordforms() != null;
      wf <- FSCollectionFactory.create(w.getWordforms(), classOf[Wordform])
    ) if (mappingsHolder.containsTriggerLemma(wf.getLemmaId()))
      onIndicatorWordformDetected(segm, w, wf)
  }

  private def onIndicatorWordformDetected(segm: AnnotationFS, word: Word, wf: Wordform) {
    val cas = segm.getView()
    // build phrase index - optimization point: reuse for the same segment segm
    val phraseIndex = buildPhraseIndex(segm, word)
    for (mapping <- mappingsHolder.getMappingsTriggeredBy(wf)) {
      val template = cas.createAnnotation(
        mapping.templateAnnoType, segm.getBegin(), segm.getEnd())
      val matchedPhrases = mutable.Set.empty[Phrase]
      def fillTemplate(iter: Iterator[SlotMapping]): Boolean =
        if (iter.hasNext) {
          val slotMapping = iter.next()
          val matchingCtx = MatchingContext(wf)
          phraseIndex.searchPhrase(
            slotMapping.pattern.matches(_, matchingCtx), matchedPhrases) match {
              case None =>
                if (slotMapping.isOptional) fillTemplate(iter)
                else false
              case Some(mPhrase) =>
                template.setFeatureValue(slotMapping.slotFeature, makeCoveringAnnotation(mPhrase))
                matchedPhrases += mPhrase
                fillTemplate(iter)
            }
        } else true // END of fillTemplate
      // invoke recursive inner function
      if (fillTemplate(mapping.getSlotMappings.iterator))
        cas.addFsToIndexes(template)
    }
  }

  private def makeCoveringAnnotation(phraseAnno: Phrase): AnnotationFS = {
    val cas = phraseAnno.getView()
    val (begin, end) = phraseAnno match {
      case np: NounPhrase => phrrecog.getOffsets(np)
      // TODO low priority
      case other => throw new UnsupportedOperationException
    }
    cas.createAnnotation(cas.getAnnotationType(), begin, end)
  }

  private def buildPhraseIndex(segm: AnnotationFS, refWord: Word) = new PhraseIndex(segm, refWord, ts)
}

object ShaltefAnnotator {
  val ParamTemplateMappingFiles = "TemplateMappingFiles"
  val ResourceKeyMorphDict = "MorphDict"
}

private[shaltef] class PhraseIndex(segm: AnnotationFS, refWord: Word, ts: TypeSystem) {
  private val npType: Type = ts.getType(classOf[NounPhrase].getName)
  // refWordIndex points to the closest phrase on the left to refWord;
  // if there is no such phrase in the segment then refWordIndex = -1
  val (phraseSeq: Seq[Phrase], refWordIndex) = {
    val buffer = ListBuffer.empty[NounPhrase]
    for (np <- CasUtil.selectCovered(npType, segm).asInstanceOf[jul.List[NounPhrase]])
      phrrecog.getDependencyChain(np, refWord) match {
        case Some(refWordDepChain) =>
          // TODO keep head chain of refWord separately
          val refWordNP = refWordDepChain.head
          buffer ++= phrrecog.traversableNPArray(refWordNP.getDependentPhrases)
        case None => buffer += np
      }
    val refWordIndex = buffer.indexWhere(refWord.getBegin < _.getBegin) match {
      case -1 => buffer.size - 1 // means refWord is on right to the last phrase
      case i => i - 1
    }
    (buffer, refWordIndex)
  }

  // EXTENSION POINT: introduce traverse strategy
  private val phraseTraverseSeq = {
    val (beforeRefSeq, afterRefSeq) = phraseSeq.splitAt(refWordIndex + 1)
    val buffer = ListBuffer.empty[Phrase]
    val iterBefore = beforeRefSeq.iterator
    val iterAfter = afterRefSeq.iterator
    while (iterBefore.hasNext || iterAfter.hasNext) {
      if (iterAfter.hasNext) buffer += iterAfter.next()
      if (iterBefore.hasNext) buffer += iterBefore.next()
    }
    buffer.toList
  }

  def searchPhrase(pred: Phrase => Boolean, setOfIgnored: sc.Set[Phrase]): Option[Phrase] =
    phraseTraverseSeq.find(candPhr =>
      !setOfIgnored.contains(candPhr) && pred(candPhr))
}