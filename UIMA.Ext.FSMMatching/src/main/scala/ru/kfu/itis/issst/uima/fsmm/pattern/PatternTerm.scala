/**
 *
 */
package ru.kfu.itis.issst.uima.fsmm.pattern

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
sealed abstract class PatternTerm

case class AtomicRestriction[T, E](
  attribute: AttributeExtractor[T], matcher: ValueMatcher[T, E], value: E) extends PatternTerm

case class DisjunctionTerm(left: PatternTerm, right: PatternTerm) extends PatternTerm

case class ConjunctionTerm(left: PatternTerm, right: PatternTerm) extends PatternTerm

case class QuantifiedTerm(operand: PatternTerm, quantifier: Quantifier) extends PatternTerm

case class ConcatenationTerm(left: PatternTerm, right: PatternTerm) extends PatternTerm