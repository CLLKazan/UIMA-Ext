package ru.kfu.itis.issst.uima.shaltef.mappings

import org.scalatest.FunSuite
import org.mockito.Mockito._
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary
import org.scalatest.mock.MockitoSugar
import scala.collection.JavaConversions.seqAsJavaList
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform
import java.io.File
import ru.kfu.itis.issst.uima.shaltef.util.CasTestUtils
import ru.kfu.itis.issst.uima.shaltef.mappings.impl.DefaultDepToArgMapping
import ru.kfu.itis.issst.uima.shaltef.mappings.impl.DefaultDepToArgMappingsHolder

class TextualMappingsParserTestSuite extends FunSuite with MockitoSugar with CasTestUtils {

  private val ts = loadTypeSystem("mappings.ts-test")

  test("Parse mappings in release.txt") {
    val morphDict = mock[MorphDictionary]
    when(morphDict.getEntries("выпустил")).thenReturn(
      Wordform.builder(morphDict, 100).build() :: Nil)
    when(morphDict.getEntries("выйдет")).thenReturn(
      Wordform.builder(morphDict, 200).build :: Nil)

    val parser = TextualMappingsParser(morphDict)

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

    val pattern1 = new ConstraintConjunctionPhrasePattern(
      new PhraseConstraint(new HeadGrammemeConstraint("case"), Equals, ConstantValue("nomn")) ::
        new PhraseConstraint(new HeadGrammemeConstraint("gndr"), Equals, TriggerFeatureReference("gndr")) ::
        Nil)
    val pattern2 = new ConstraintConjunctionPhrasePattern(
      new PhraseConstraint(new HeadGrammemeConstraint("case"), Equals, ConstantValue("accs"))
        :: Nil)
    val pattern3 = new ConstraintConjunctionPhrasePattern(
      new PhraseConstraint(PrepositionConstraint, Equals, ConstantValue("в"))
        :: Nil)
    assert(mappings.triggerLemmaId2Mappings(100) === new DefaultDepToArgMapping(
      templateAnnoType, Set(100),
      new SlotMapping(pattern1, false, subjFeat) ::
        new SlotMapping(pattern2, false, objFeat) ::
        new SlotMapping(pattern3, true, dateFeat) :: Nil)
      :: Nil)

    val pattern4 = new ConstraintConjunctionPhrasePattern(
      new PhraseConstraint(new HeadGrammemeConstraint("case"), Equals, ConstantValue("nomn")) ::
        new PhraseConstraint(new HeadGrammemeConstraint("gndr"), Equals, TriggerFeatureReference("gndr")) :: Nil)
    assert(mappings.triggerLemmaId2Mappings(200) === new DefaultDepToArgMapping(
      templateAnnoType, Set(200),
      new SlotMapping(pattern4, false, objFeat) :: Nil)
      :: Nil)
  }

}