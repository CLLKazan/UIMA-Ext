/**
 *
 */
package ru.kfu.itis.issst.uima.fsmm
import nfsm.AnnotationMatcher

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
package object nfsm {
  // private[nfsm] type InputMatcher[A] = AnnotationMatcher[A]
  type Action[A] = (List[A], Map[String, List[A]]) => Unit
}