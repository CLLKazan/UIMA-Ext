package ru.kfu.itis.issst.uima.shaltef.mappings.pattern

import ru.kfu.itis.issst.uima.phrrecog.cas.Phrase
import scala.collection.immutable.Iterable
import org.apache.commons.lang3.builder.HashCodeBuilder

trait PhrasePattern {
  def matches(phr: Phrase, ctx: MatchingContext): Boolean
}

private[mappings] class ConstraintConjunctionPhrasePattern(
  val constraints: Iterable[PhraseConstraint])
  extends PhrasePattern {

  override def matches(phr: Phrase, ctx: MatchingContext): Boolean = {
    var matched = true
    val iter = constraints.iterator
    while (matched && iter.hasNext)
      matched = iter.next().matches(phr, ctx)
    matched
  }

  override def equals(obj: Any): Boolean = obj match {
    case that: ConstraintConjunctionPhrasePattern => this.constraints == that.constraints
    case _ => false
  }

  override def hashCode(): Int =
    new HashCodeBuilder().append(constraints).toHashCode()

  override def toString = new StringBuilder("ConstraintConjunction:").append(constraints).toString
}