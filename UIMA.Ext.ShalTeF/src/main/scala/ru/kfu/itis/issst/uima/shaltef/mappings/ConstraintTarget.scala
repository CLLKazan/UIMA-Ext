package ru.kfu.itis.issst.uima.shaltef.mappings

import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle

abstract class ConstraintTarget {

}

class HeadGrammemeConstraint(val gramCategory: String) extends ConstraintTarget {
  override def equals(obj: Any): Boolean = obj match {
    case that: HeadGrammemeConstraint => this.gramCategory == that.gramCategory
    case _ => false
  }
  override def toString = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
    .append(gramCategory).toString
}

object PrepositionConstraint extends ConstraintTarget

object WordsConstraint extends ConstraintTarget