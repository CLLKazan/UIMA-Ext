/**
 *
 */
package ru.kfu.itis.issst.uima.fsmm.nfsm.test

import ru.kfu.itis.issst.uima.fsmm
import fsmm.input.InputGraph
import java.util.TreeSet
import scala.collection.mutable.{ Set => MutaSet }
import scala.collection.JavaConversions
import fsmm.pattern._
import fsmm.nfsm._

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class DumbInputGraph extends InputGraph[DumbAnnotation] {

  private val annoSet = new TreeSet[DumbAnnotation]

  def addAnno(name: String, from: Int, to: Int): this.type = {
    annoSet.add(new DumbAnnotation(name, from, to))
    this
  }

  override def next(from: Int): (Set[DumbAnnotation], Int) = {
    annoSet.higher(new DumbAnnotation("%DUMB%", from, Int.MinValue)) match {
      case null => (Set(), from)
      case nextAnno => {
        import JavaConversions._
        val resultSet = annoSet.subSet(nextAnno, true,
          new DumbAnnotation("%DUMB%", nextAnno.begin + 1, Int.MaxValue), false)
          .toSet
        (resultSet, nextAnno.begin)
      }
    }

  }

  override def getAnnotationEnd(anno: DumbAnnotation): Int =
    anno.end
}

class DumbAnnotation(val name: String, val begin: Int, val end: Int) extends Ordered[DumbAnnotation] {
  override def compare(that: DumbAnnotation): Int = {
    if (begin < that.begin) -1
    else if (begin > that.begin) 1
    else if (end < that.end) 1
    else if (end > that.end) -1
    else 0
  }

  override val toString = name + "[" + begin + "," + end + "]"
}

class NameExtractor extends AttributeExtractor[DumbAnnotation] {
  override def getValue(anno: DumbAnnotation): String = anno.name
}

// TODO move to matchers package in src/main
class StringMatcher extends ValueMatcher {
  type ActualType = String
  type TargetType = String
  override def matches(actual: String, expected: String, varCtx: VariableContext): (Boolean, VariableContext) =
    (actual == expected, varCtx)
}