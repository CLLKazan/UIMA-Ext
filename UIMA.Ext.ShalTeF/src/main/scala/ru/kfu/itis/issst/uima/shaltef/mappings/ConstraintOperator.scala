package ru.kfu.itis.issst.uima.shaltef.mappings

sealed abstract class ConstraintOperator {
  def apply(leftArg: Any, rightArg: Any): Boolean
}

case object Equals extends ConstraintOperator {
  override def apply(leftArg: Any, rightArg: Any): Boolean =
    leftArg == rightArg
}