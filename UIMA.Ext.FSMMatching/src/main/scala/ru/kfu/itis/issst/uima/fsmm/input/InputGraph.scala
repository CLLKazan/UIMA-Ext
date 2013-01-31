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
   * return the tuple:
   * 	1. the set of annotations starting from F
   * 	where F is minimal offset strictly greater than 'from' and have annotations
   * 	2. F
   * If there are no such annotation return (empty set, from)
   */
  def next(from: Int): (Set[A], Int)

  // TODO check
  def next(anno: A): (Set[A], Int) = next(getAnnotationEnd(anno))

  /**
   * @return anno end (exclusive)
   */
  def getAnnotationEnd(anno: A): Int
}