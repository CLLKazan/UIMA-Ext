package ru.kfu.itis.issst.uima.shaltef.mappings

import ru.kfu.itis.issst.uima.phrrecog.cas.Phrase
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle

class PhraseConstraint(val target: ConstraintTarget,
  val op: ConstraintOperator, val value: ConstraintValue) {

  def matches(phr: Phrase): Boolean =
    // TODO
    throw new UnsupportedOperationException
  //op(target, value)

  override def equals(obj: Any): Boolean = obj match {
    case that: PhraseConstraint =>
      this.target == that.target && this.op == that.op && this.value == that.value
    case _ => false
  }

  override def toString = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
    .append("target", target).append("op", op).append("value", value).toString
}