/**
 *
 */
package ru.kfu.itis.issst.uima.fsmm.pattern

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
sealed abstract class PatternTerm(val label: String)

case class AtomicRestriction[A](
  attribute: AttributeExtractor[A], matcher: ValueMatcher, value: Any,
  override val label: String = null)
  extends PatternTerm(label)

case class DisjunctionTerm(left: PatternTerm, right: PatternTerm,
  override val label: String = null)
  extends PatternTerm(label)

case class QuantifiedTerm(operand: PatternTerm, quantifier: Quantifier,
  override val label: String = null)
  extends PatternTerm(label)

case class ConcatenationTerm(left: PatternTerm, right: PatternTerm,
  override val label: String = null)
  extends PatternTerm(label)