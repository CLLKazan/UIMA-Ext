/**
 *
 */
package ru.kfu.itis.issst.uima.fsmm.nfsm

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class AnnotationMatcher[A] {

  def matches(anno: A, varCtx: VariableContext): (Boolean, VariableContext)

}