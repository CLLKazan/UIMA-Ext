package ru.kfu.itis.issst.uima.shaltef.mappings.pattern

import ru.kfu.itis.issst.uima.phrrecog.cas.Phrase
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import org.apache.commons.lang3.builder.HashCodeBuilder

trait PhraseConstraint {
  def matches(phr: Phrase, ctx: MatchingContext): Boolean
}

class PhraseConstraintFactory {
  def phraseConstraint(t: ConstraintTarget, op: BinaryConstraintOperator, v: ConstraintValue): PhraseConstraint =
    new BinOpPhraseConstraint(t, op, v)

  def phraseConstraint(op: UnaryConstraintOperator, v: ConstraintValue): PhraseConstraint =
    new UnOpPhraseConstraint(op, v)
}

private[mappings] class BinOpPhraseConstraint private[pattern] (
  val target: ConstraintTarget, val op: BinaryConstraintOperator, val value: ConstraintValue)
  extends PhraseConstraint {

  override def matches(phr: Phrase, ctx: MatchingContext): Boolean =
    op(target.getValue(phr), value.getValue(ctx))

  override def equals(obj: Any): Boolean = obj match {
    case that: BinOpPhraseConstraint =>
      this.target == that.target && this.op == that.op && this.value == that.value
    case _ => false
  }

  override def hashCode(): Int =
    new HashCodeBuilder().append(target).append(op).append(value).toHashCode()

  override def toString = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
    .append(target).append(op).append(value).toString
}

private[mappings] class UnOpPhraseConstraint private[pattern] (
  val op: UnaryConstraintOperator, val value: ConstraintValue)
  extends PhraseConstraint {
  override def matches(phr: Phrase, ctx: MatchingContext): Boolean =
    op(phr, value.getValue(ctx))

  override def equals(obj: Any): Boolean = obj match {
    case that: UnOpPhraseConstraint =>
      this.op == that.op && this.value == that.value
    case _ => false
  }

  override def hashCode(): Int =
    new HashCodeBuilder().append(op).append(value).toHashCode()

  override def toString = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
    .append(op).append(value).toString
}