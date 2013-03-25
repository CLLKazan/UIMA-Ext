package ru.kfu.itis.issst.uima.shaltef.mappings

import ru.kfu.itis.issst.uima.phrrecog.cas.Phrase
import scala.collection.immutable.Iterable

trait PhrasePattern {
  def matches(phr: Phrase): Boolean
}

private[mappings] class ConstraintConjunctionPhrasePattern(
  val constraints: Iterable[PhraseConstraint])
  extends PhrasePattern {

  override def matches(phr: Phrase): Boolean = {
    var matched = true
    val iter = constraints.iterator
    while (matched && iter.hasNext)
      matched = iter.next().matches(phr)
    matched
  }

  override def equals(obj: Any): Boolean = obj match {
    case that: ConstraintConjunctionPhrasePattern => this.constraints == that.constraints
    case _ => false
  }

  override def toString = new StringBuilder("ConstraintConjunction:").
    append(constraints.toString).toString
}