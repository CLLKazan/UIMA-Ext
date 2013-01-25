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
  attribute: AttributeExtractor[A], matcher: ValueMatcher, value: Any, label: String = null)
  extends PatternTerm(label)

case class DisjunctionTerm(left: PatternTerm, right: PatternTerm, label: String = null)
  extends PatternTerm(label)

case class QuantifiedTerm(operand: PatternTerm, quantifier: Quantifier, label: String = null)
  extends PatternTerm(label)

case class ConcatenationTerm(left: PatternTerm, right: PatternTerm, label: String = null)
  extends PatternTerm(label)