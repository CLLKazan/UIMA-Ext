/**
 *
 */
package ru.kfu.itis.issst.uima.shaltef.mappings.impl

import org.scalatest.FunSuite
import scala.collection.JavaConversions.{ seqAsJavaList, bufferAsJavaList }
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary
import ru.kfu.itis.issst.uima.shaltef.mappings.MappingsParserConfig
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.PhraseConstraintFactory
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.ConstraintTargetFactory
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.ConstraintValueFactory
import ru.kfu.itis.issst.uima.shaltef.mappings.DepToArgMappingsBuilder
import org.apache.uima.cas.Type
import ru.kfu.itis.issst.uima.shaltef.mappings.SlotMapping
import org.apache.uima.cas.Feature
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.ConstraintConjunctionPhrasePattern
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.Equals
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.HasHeadsPath
import java.util.BitSet

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class EnforcePrepositionConstraintPostProcessorTestSuite extends FunSuite with MockitoSugar {

  test("Enforce preposition constraint in the case of its absense in a pattern") {
    val morphDict = mock[MorphDictionary]
    val someGrBS = new BitSet
    someGrBS.set(100)
    when(morphDict.getGrammemWithChildrenBits("someGr", false)).thenReturn(someGrBS)
    when(morphDict.toGramSet(someGrBS)).thenReturn(List("v1", "v2"))
    
    val constrValueFactory = new ConstraintValueFactory(morphDict)
    val constrTargetFactory = new ConstraintTargetFactory(morphDict)
    val constrFactory = new PhraseConstraintFactory
    val parserCfg = new MappingsParserConfig(morphDict)

    import constrValueFactory._
    import constrFactory._
    import constrTargetFactory._
    // prepare non-empty builder
    val mpBuilder = DepToArgMappingsBuilder()
    val type1 = mock[Type]
    val feat1 = mock[Feature]
    val type2 = mock[Type]
    val feat2 = mock[Feature]

    val pattern1 = new ConstraintConjunctionPhrasePattern(
      phraseConstraint(headFeature("someGr"), Equals, constant("someGrValue")) :: Nil)
    val pattern2 = new ConstraintConjunctionPhrasePattern(
      phraseConstraint(HasHeadsPath, constantCollectionAlternatives(Set(List("someHead")))) ::
        phraseConstraint(prepositionTarget, Equals, constant("somePrep")) :: Nil)
    mpBuilder.add(new DefaultDepToArgMapping(type1, Set(1, 3, 5),
      new SlotMapping(pattern1, false, Some(feat1)) :: new SlotMapping(pattern2, false, None) :: Nil))

    val pattern3 = new ConstraintConjunctionPhrasePattern(
      phraseConstraint(HasHeadsPath, constantCollectionAlternatives(
        Set(List("oneHead"), List("anotherHead")))) :: Nil)
    mpBuilder.add(new DefaultDepToArgMapping(type2, Set(2, 4, 5),
      new SlotMapping(pattern3, false, Some(feat2)) :: Nil))

    val postProcessor = new EnforcePrepositionConstraintPostProcessor(parserCfg)
    postProcessor.postprocess(mpBuilder)

    val resultMappings = mpBuilder.getMappings
    assert(resultMappings.size === 2)
    val rmIter = resultMappings.iterator

    val pattern1Fixed = new ConstraintConjunctionPhrasePattern(
      phraseConstraint(prepositionTarget, Equals, constant(null)) ::
        phraseConstraint(headFeature("someGr"), Equals, constant("someGrValue")) :: Nil)
    assert(rmIter.next() === new DefaultDepToArgMapping(type1, Set(1, 3, 5),
      new SlotMapping(pattern1Fixed, false, Some(feat1)) :: new SlotMapping(pattern2, false, None) :: Nil))

    val pattern3Fixed = new ConstraintConjunctionPhrasePattern(
      phraseConstraint(prepositionTarget, Equals, constant(null)) ::
        phraseConstraint(HasHeadsPath, constantCollectionAlternatives(
          Set(List("oneHead"), List("anotherHead")))) :: Nil)

    assert(rmIter.next() === new DefaultDepToArgMapping(type2, Set(2, 4, 5),
      new SlotMapping(pattern3Fixed, false, Some(feat2)) :: Nil))
  }

}