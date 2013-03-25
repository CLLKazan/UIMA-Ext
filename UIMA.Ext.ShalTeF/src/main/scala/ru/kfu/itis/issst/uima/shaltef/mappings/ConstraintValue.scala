package ru.kfu.itis.issst.uima.shaltef.mappings

sealed abstract class ConstraintValue {

}

case class ConstantValue(valueString: String) extends ConstraintValue

case class TriggerFeatureReference(featureName: String) extends ConstraintValue