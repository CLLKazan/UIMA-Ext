/**
 *
 */
package ru.kfu.itis.issst.uima.morph.search
import org.apache.uima.jcas.JCas
import scala.collection.JavaConversions._
import org.opencorpora.cas.Wordform
import scala.collection.{ mutable => cm }
import org.apache.uima.cas.Type
import org.opencorpora.cas.Word
import ru.kfu.itis.cll.uima.cas.AnnotationUtils.toList
import org.apache.uima.cas.Feature
import org.apache.uima.cas.CAS
import org.apache.uima.analysis_component.CasAnnotator_ImplBase
import org.apache.uima.UimaContext
import MorphSearcher._
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.SerializedDictionaryResource
import org.apache.uima.resource.ResourceInitializationException
import ru.kfu.itis.cll.uima.util.AnnotatorUtils._
import java.net.URL
import org.apache.commons.io.IOUtils
import org.apache.uima.cas.TypeSystem
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.{ Wordform => WfModel }
import org.apache.uima.util.Level
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Lemma

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class MorphSearcher extends CasAnnotator_ImplBase {

  private var morphDict: MorphDictionary = _

  private var targetLemmaSet: Set[Lemma] = _
  private var targetLemmaIdSet: Set[Int] = _

  private var targetTypeName: String = _
  private var targetType: Type = _
  private var wordformFeatureName: String = _
  private var wordformFeature: Feature = _

  override def initialize(ctx: UimaContext) {
    super.initialize(ctx)
    // get dict object
    val morphDictKey = ResourceMorphDict
    morphDict = ctx.getResourceObject(morphDictKey) match {
      case sdr: SerializedDictionaryResource => sdr.getDictionary()
      case someRes => initError("Unknown resource under key %s: %s", morphDictKey, someRes)
    }
    // get target anno type and its wf feature 
    targetTypeName = ctx.getConfigParameterValue(ParamTargetAnnoType).asInstanceOf[String]
    wordformFeatureName = ctx.getConfigParameterValue(ParamWordformFeature).asInstanceOf[String]
    mandatoryParam(ParamTargetAnnoType, targetTypeName)
    mandatoryParam(ParamWordformFeature, wordformFeatureName)

    // get source wordforms
    val sourceWfsUrl = new URL(
      ctx.getConfigParameterValue(ParamSourceWordformsUrl).asInstanceOf[String])
    mandatoryParam(ParamSourceWordformsUrl, sourceWfsUrl)
    val sourceWfsStream = sourceWfsUrl.openStream()
    val sourceWfStrings = try {
      IOUtils.readLines(sourceWfsStream, "utf-8").toList
        .map(_.trim()).filterNot(_.isEmpty).toSet
    } finally {
      sourceWfsStream.close()
    }
    targetLemmaIdSet = extractLemmaIdSet(sourceWfStrings)
    targetLemmaSet = targetLemmaIdSet.map(morphDict.getLemma(_))
    ctx.getLogger().log(Level.INFO, targetLemmaSet.mkString(
      "Target lemma set:\n", "\n", ""))
  }

  override def typeSystemInit(ts: TypeSystem) {
    super.typeSystemInit(ts)
    targetType = ts.getType(targetTypeName)
    annotationTypeExist(targetTypeName, targetType)
    wordformFeature = featureExist(targetType, wordformFeatureName)
  }

  override def process(cas: CAS): Unit = process(cas.getJCas())

  private def extractLemmaIdSet(wfStrings: Set[String]): Set[Int] = {
    val initialLemmaSet =
      for (wfStr <- wfStrings; wfModel <- morphDict.getEntries(wfStr))
        yield wfModel.getLemmaId()
    collectLinkedLemmas(initialLemmaSet, initialLemmaSet)
  }

  private def collectLinkedLemmas(lemmaSet: Set[Int], lastAdded: Set[Int]): Set[Int] = {
    require(lastAdded.subsetOf(lemmaSet))
    val setOfLinked: Set[Int] = toScalaInt(
      lastAdded.flatMap(morphDict.getLemmaOutlinks(_).keySet().toSet) ++
        lastAdded.flatMap(morphDict.getLemmaInlinks(_).keySet().toSet)
    )
    val union = lemmaSet ++ setOfLinked
    if (lemmaSet.size == union.size)
      union
    else collectLinkedLemmas(union, setOfLinked)
  }

  private def toScalaInt(integerSet: Set[Integer]): Set[Int] =
    integerSet.map(i => i: Int)

  private def process(cas: JCas) {
    val wordIndex = cas.getAnnotationIndex(Word.`type`)
    for (
      rawWord <- wordIndex; word = rawWord.asInstanceOf[Word];
      wf <- toList(word.getWordforms(), classOf[Wordform])
    ) if (targetLemmaIdSet contains wf.getLemmaId())
      makeTargetAnnotation(word, wf, targetType)
  }

  private def makeTargetAnnotation(word: Word, wf: Wordform, targetType: Type) {
    val cas = word.getCAS()
    val targetAnno = cas.createAnnotation(targetType, word.getBegin(), word.getEnd())
    if (wordformFeature != null) {
      targetAnno.setFeatureValue(wordformFeature, wf)
    }
    cas.addFsToIndexes(targetAnno)
  }

  private def initError(msg: String, args: Any*): Nothing =
    throw new ResourceInitializationException(
      new IllegalStateException(
        msg.format(args: _*)))

}

object MorphSearcher {
  val ResourceMorphDict = "MorphologyDict"
  val ParamSourceWordformsUrl = "sourceWordformsUrl"
  val ParamTargetAnnoType = "targetAnnotationType"
  val ParamWordformFeature = "wordformFeature"
}