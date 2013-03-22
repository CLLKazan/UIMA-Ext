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
import ru.kfu.itis.issst.uima.shaltef.mappings.PhrasePattern
import org.apache.uima.cas.Feature
import ru.kfu.itis.issst.uima.phrrecog.cas.NounPhrase
import scala.collection.mutable.ListBuffer
import ru.kfu.itis.issst.uima.shaltef.mappings.DepToArgMappingsHolder

class ShaltefAnnotator extends CasAnnotator_ImplBase {

  private val templateType2File = mutable.Map.empty[String, URL]
  private val mappingParser = MappingsParser.getInstance
  private val mappingsHolder = new DepToArgMappingsHolder
  // CAS types
  private var wordType: Type = _
  private var npType: Type = _

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
          "Can't parse templateFile string %s".format(otherArr))
      }
  }

  override def typeSystemInit(ts: TypeSystem) {
    wordType = ts.getType(classOf[Word].getName)
    npType = ts.getType(classOf[NounPhrase].getName)
    // parse mapping files
    for ((templateAnnoTypeName, url) <- templateType2File) {
      val templateAnnoType = ts.getType(templateAnnoTypeName)
      annotationTypeExist(templateAnnoTypeName, templateAnnoType)
      mappingParser.parse(url, templateAnnoType, mappingsHolder)
    }
  }

  override def process(cas: CAS): Unit = process(cas.getJCas())

  private def process(jCas: JCas) {
    for (
      segm <- jCas.getAnnotationIndex(Sentence.typeIndexID);
      w <- CasUtil.selectCovered(wordType, segm).asInstanceOf[jul.List[Word]];
      if w.getWordforms() != null;
      wf <- FSCollectionFactory.create(w.getWordforms(), classOf[Wordform])
    ) if (mappingsHolder.containLemma(wf.getLemmaId()))
      onIndicatorWordformDetected(segm, w, wf)
  }

  private def onIndicatorWordformDetected(segm: AnnotationFS, word: Word, wf: Wordform) {
    val cas = segm.getView()
    // build phrase index - optimization point: reuse for the same segment segm
    val phraseIndex = buildPhraseIndex(segm, word)
    for (mapping <- mappingsHolder.getMappingsFor(wf)) {
      val template = cas.createAnnotation(
        mapping.templateAnnoType, segm.getBegin(), segm.getEnd())
      val matchedPhrases = mutable.Set.empty[Phrase]
      def fillTemplate(iter: Iterator[(PhrasePattern, Feature)]): Boolean =
        if (iter.hasNext) {
          val (phrasePattern, slotFeature) = iter.next()
          phraseIndex.searchPhrase(phrasePattern, matchedPhrases) match {
            case None =>
              if (phrasePattern.isOptional) fillTemplate(iter)
              else false
            case Some(mPhrase) =>
              template.setFeatureValue(slotFeature, makeCoveringAnnotation(mPhrase))
              fillTemplate(iter)
          }
        } else true // END of fillTemplate
      // invoke recursive inner function
      if (fillTemplate(mapping.getSlotPatterns.iterator))
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

  private def buildPhraseIndex(segm: AnnotationFS, refWord: Word) = new PhraseIndex(segm, refWord)

  private class PhraseIndex(segm: AnnotationFS, refWord: Word) {
    // refWordIndex points to the phrase before refWord;
    // if there is no such phrase in the segment then refWordIndex = -1
    private val (phraseSeq: IndexedSeq[Phrase], refWordIndex) = {
      val buffer = ListBuffer.empty[NounPhrase]
      for (np <- CasUtil.selectCovered(npType, segm).asInstanceOf[jul.List[NounPhrase]])
        if (phrrecog.containWord(np, refWord)) {
          // TODO check inner dependencies of refWord inside np
          // if refWord has deps =>
          // 	create annotations for inner NPs - deps of refWord - (do not index them) and add them to buffer
          // TODO keep head chain of refWord separately
        } else buffer += np
      // TODO check refWordIndex computation below
      val refWordIndex = buffer.indexWhere(refWord.getBegin < _.getBegin) match {
        case -1 => buffer.size - 1 // means refWord is after last phrase
        case i => i - 1
      }
      (buffer.toIndexedSeq, refWordIndex)
    }

    def searchPhrase(pattern: PhrasePattern, setOfIgnored: sc.Set[Phrase]): Option[Phrase] = {
      // EXTENSION POINT: introduce traverse strategy
      // TODO
      throw new UnsupportedOperationException
    }
  }
}

object ShaltefAnnotator {
  val ParamTemplateMappingFiles = "TemplateMappingFiles"
}