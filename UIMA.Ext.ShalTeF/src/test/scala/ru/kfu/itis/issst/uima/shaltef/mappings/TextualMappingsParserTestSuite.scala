package ru.kfu.itis.issst.uima.shaltef.mappings

import org.scalatest.FunSuite
import org.mockito.Mockito._
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary
import org.scalatest.mock.MockitoSugar
import scala.collection.JavaConversions.{ seqAsJavaList, bufferAsJavaList }
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform
import java.io.File
import ru.kfu.itis.issst.uima.shaltef.util.CasTestUtils
import ru.kfu.itis.issst.uima.shaltef.mappings.impl.DefaultDepToArgMapping
import ru.kfu.itis.issst.uima.shaltef.mappings.impl.DefaultDepToArgMappingsHolder
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.ConstraintValueFactory
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.Equals
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.ConstraintConjunctionPhrasePattern
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.PhraseConstraint
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.PhraseConstraintFactory
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.ConstraintTargetFactory
import java.util.BitSet
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.ConstraintConjunctionPhrasePattern
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.HasHeadsPath

class TextualMappingsParserTestSuite extends FunSuite with MockitoSugar with CasTestUtils {

  private val ts = loadTypeSystem("mappings.ts-test")

  test("Parse mappings in release.txt") {
    val morphDict = mock[MorphDictionary]
    // stub invocations for trigger lemma search
    when(morphDict.getEntries("выпустил")).thenReturn(
      Wordform.builder(morphDict, 100).build() :: Nil)
    when(morphDict.getEntries("выйдет")).thenReturn(
      Wordform.builder(morphDict, 200).build :: Nil)
    // stub invocations for grammeme extractors
    val caseBS = new BitSet
    caseBS.set(100)
    val gndrBS = new BitSet
    caseBS.set(200)
    when(morphDict.getGrammemWithChildrenBits("CAse", false)).thenReturn(caseBS)
    when(morphDict.getGrammemWithChildrenBits("GNdr", false)).thenReturn(gndrBS)
    when(morphDict.toGramSet(caseBS)).thenReturn(List("nomn", "accs", "ablt"))
    when(morphDict.toGramSet(gndrBS)).thenReturn(List("masc", "femn", "neut"))

    val constrValueFactory = new ConstraintValueFactory(morphDict)
    val constrTargetFactory = new ConstraintTargetFactory(morphDict)
    val constrFactory = new PhraseConstraintFactory
    val parser = TextualMappingsParser(new MappingsParserConfig(morphDict))

    val mappingsBuilder = DepToArgMappingsBuilder()
    val templateAnnoType = ts.getType("test.Release")
    assert(templateAnnoType != null, "Can't final Release annotation type")
    parser.parse(new File("src/test/resources/mappings/release.txt").toURI.toURL,
      templateAnnoType, mappingsBuilder)

    val subjFeat = templateAnnoType.getFeatureByBaseName("subj")
    val objFeat = templateAnnoType.getFeatureByBaseName("obj")
    val dateFeat = templateAnnoType.getFeatureByBaseName("date")

    val mappings = mappingsBuilder.build().asInstanceOf[DefaultDepToArgMappingsHolder]
    assert(mappings.triggerLemmaId2Mappings.size === 2)

    import constrValueFactory._
    import constrTargetFactory._
    import constrFactory._
    val pattern1 = new ConstraintConjunctionPhrasePattern(
      phraseConstraint(headFeature("case"), Equals, constant("nomn")) ::
        phraseConstraint(headFeature("gndr"), Equals, triggerFeatureReference("gndr")) ::
        Nil)
    val pattern2 = new ConstraintConjunctionPhrasePattern(
      phraseConstraint(headFeature("case"), Equals, constant("accs"))
        :: Nil)
    val pattern3 = new ConstraintConjunctionPhrasePattern(
      phraseConstraint(prepositionTarget, Equals, constant("в"))
        :: Nil)
    assert(mappings.triggerLemmaId2Mappings(100) === new DefaultDepToArgMapping(
      templateAnnoType, Set(100),
      new SlotMapping(pattern1, false, Some(subjFeat)) ::
        new SlotMapping(pattern2, false, Some(objFeat)) ::
        new SlotMapping(pattern3, true, Some(dateFeat)) :: Nil)
      :: Nil)

    val pattern4 = new ConstraintConjunctionPhrasePattern(
      phraseConstraint(headFeature("case"), Equals, constant("nomn")) ::
        phraseConstraint(headFeature("gndr"), Equals, triggerFeatureReference("gndr")) :: Nil)
    val pattern5 = new ConstraintConjunctionPhrasePattern(
      phraseConstraint(prepositionTarget, Equals, constant("в")) ::
        phraseConstraint(HasHeadsPath, constantCollectionAlternatives(
          Set(List("году"))))
        :: Nil)
    val pattern6 = new ConstraintConjunctionPhrasePattern(
      phraseConstraint(HasHeadsPath, constantCollectionAlternatives(
        Set(List("пресс-релиза"), List("сообщения", "сайте"))))
        :: Nil)
    val pattern7 = new ConstraintConjunctionPhrasePattern(
      phraseConstraint(prepositionTarget, Equals, constant("на"))
        :: Nil)
    assert(mappings.triggerLemmaId2Mappings(200) === new DefaultDepToArgMapping(
      templateAnnoType, Set(200),
      new SlotMapping(pattern4, false, Some(objFeat)) ::
        new SlotMapping(pattern5, false, Some(dateFeat)) ::
        new SlotMapping(pattern6, false, Some(subjFeat)) ::
        new SlotMapping(pattern7, false, None) :: Nil)
      :: Nil)
  }

}