/**
 *
 */
package ru.kfu.itis.issst.uima.fsmm.pattern
import ru.kfu.itis.issst.uima.fsmm.nfsm.VariableContext
import ru.kfu.itis.issst.uima.fsmm.nfsm.VariableContext

/**
 * Represents an attribute value matcher
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
// TODO move VariableContext and matchers to 'matchers' package
trait ValueMatcher {
  type ActualType
  type TargetType

  def matches(actual: ActualType, expected: TargetType, varCtx: VariableContext): (Boolean, VariableContext)

}