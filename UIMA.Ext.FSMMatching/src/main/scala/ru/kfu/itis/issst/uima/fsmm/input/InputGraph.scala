/**
 *
 */
package ru.kfu.itis.issst.uima.fsmm.input

/**
 * @tparam A input annotation type
 * @author Rinat Gareev
 *
 */
trait InputGraph[A] {
 
  /**
   * return the set of annotations that have begin at the first (non-empty) offset
   */
  def getFirst():Set[A]

}