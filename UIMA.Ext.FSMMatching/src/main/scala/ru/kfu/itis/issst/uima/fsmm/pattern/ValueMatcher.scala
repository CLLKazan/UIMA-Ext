/**
 *
 */
package ru.kfu.itis.issst.uima.fsmm.pattern

/**
 * Represents an attribute value matcher
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
trait ValueMatcher[A,E] {

  def matches(actual: A, expected: E)

}