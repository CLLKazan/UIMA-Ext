/**
 *
 */
package ru.kfu.itis.issst.uima.shaltef.mappings.impl

import ru.kfu.itis.issst.uima.shaltef.mappings.DepToArgMappingsPostProcessor
import ru.kfu.itis.issst.uima.shaltef.mappings.DepToArgMappingsBuilder
import ru.kfu.itis.issst.uima.shaltef.mappings.DepToArgMapping
import ru.kfu.itis.issst.uima.shaltef.mappings.SlotMapping
import ru.kfu.itis.issst.uima.shaltef.mappings.SlotMapping
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.PhrasePattern
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.ConstraintConjunctionPhrasePattern
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.PhraseConstraint
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.BinOpPhraseConstraint
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.PrepositionTarget
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.ConstraintConjunctionPhrasePattern
import ru.kfu.itis.issst.uima.shaltef.mappings.MappingsParserConfig
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.Equals
import grizzled.slf4j.Logging

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class EnforcePrepositionConstraintPostProcessor(config: MappingsParserConfig) extends DepToArgMappingsPostProcessor
  with Logging {

  import config.constraintFactory._
  import config.constraintTargetFactory._
  import config.constraintValueFactory._

  override def postprocess(mpBuilder: DepToArgMappingsBuilder) {
    for (mp <- mpBuilder.getMappings) {
      val newMP = enforcePrepositionConstraints(mp)
      if (mp != newMP) {
        mpBuilder.replace(mp, newMP)
        info("Preposition constraint enforced in:\n%s".format(newMP))
      }
    }
  }

  private def enforcePrepositionConstraints(mp: DepToArgMapping): DepToArgMapping = {
    new DefaultDepToArgMapping(mp.templateAnnoType, mp.triggerLemmaIds,
      mp.slotMappings.toList.map(enforcePrepositionConstraint(_)))
  }

  private def enforcePrepositionConstraint(sm: SlotMapping): SlotMapping =
    new SlotMapping(enforcePrepositionConstraint(sm.pattern), sm.isOptional, sm.slotFeatureOpt)

  private def enforcePrepositionConstraint(p: PhrasePattern): PhrasePattern =
    p match {
      case conj: ConstraintConjunctionPhrasePattern =>
        if (conj.constraints.exists(isPrepositionConstraint(_)))
          conj
        else
          new ConstraintConjunctionPhrasePattern(phraseConstraint(
            prepositionTarget, Equals, constant(null))
            :: conj.constraints.toList)
      case _ => throw new UnsupportedOperationException("Can't post-process pattern: %s".format(p))
    }

  private def isPrepositionConstraint(pc: PhraseConstraint) =
    pc match {
      case binPC: BinOpPhraseConstraint => binPC.target == PrepositionTarget
      case _ => false
    }
}