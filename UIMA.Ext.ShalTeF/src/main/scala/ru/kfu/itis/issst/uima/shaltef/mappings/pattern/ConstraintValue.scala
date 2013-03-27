package ru.kfu.itis.issst.uima.shaltef.mappings.pattern

trait ConstraintValue {

  def getValue(ctx: MatchingContext): Any

}