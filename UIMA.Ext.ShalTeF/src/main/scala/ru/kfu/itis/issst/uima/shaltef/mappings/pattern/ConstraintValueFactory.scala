package ru.kfu.itis.issst.uima.shaltef.mappings.pattern

import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary

class ConstraintValueFactory(morphDict: MorphDictionary) {
  def constant(valueString: String): ConstraintValue = ConstantScalarValue(valueString)

  def constant(values: Iterable[Any]): ConstraintValue = ConstantCollectionValue(values)

  def triggerFeatureReference(refString: String): ConstraintValue = {
    getGramCategory(morphDict, refString) match {
      case Some(gramCatSet) => new TriggerGrammemeReference(gramCatSet)
      case None => throw new IllegalArgumentException(
        "Unknown trigger feature reference: %s".format(refString))
    }
  }
}

private[pattern] case class ConstantScalarValue(valueString: String) extends ConstraintValue {
  override def getValue(ctx: MatchingContext) = valueString
}

private[pattern] case class ConstantCollectionValue(values: Iterable[Any]) extends ConstraintValue {
  override def getValue(ctx: MatchingContext) = values
}

private[pattern] case class TriggerGrammemeReference(gramIds: Set[String])
  extends ConstraintValue with GrammemeExtractor {

  override def getValue(ctx: MatchingContext): String = extractGrammeme(ctx.triggerHead)
}