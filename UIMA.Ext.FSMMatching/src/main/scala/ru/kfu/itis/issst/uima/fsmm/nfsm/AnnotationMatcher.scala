/**
 *
 */
package ru.kfu.itis.issst.uima.fsmm.nfsm
import ru.kfu.itis.issst.uima.fsmm.pattern
import pattern.AttributeExtractor
import pattern.ValueMatcher

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class AnnotationMatcher[A](
  val attributeExtractor: AttributeExtractor[A],
  val matcher: ValueMatcher, val expectedValue: Any) {

  def matches(anno: A, varCtx: VariableContext): (Boolean, VariableContext) = {
    val attrValue = attributeExtractor.getValue(anno)
    matcher.matches(attrValue.asInstanceOf[matcher.ActualType],
      expectedValue.asInstanceOf[matcher.TargetType], varCtx)
  }

}

object AnnotationMatcher {
}