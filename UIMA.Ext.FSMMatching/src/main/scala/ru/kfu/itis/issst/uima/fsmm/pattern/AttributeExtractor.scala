/**
 *
 */
package ru.kfu.itis.issst.uima.fsmm.pattern
import org.apache.uima.cas.text.AnnotationFS

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
trait AttributeExtractor[A] {

  def getValue(anno: AnnotationFS):A

}