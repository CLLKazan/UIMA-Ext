package ru.kfu.itis.issst.uima.shaltef.mappings.pattern

import ru.kfu.itis.issst.uima.phrrecog.cas.Phrase
import ru.kfu.itis.issst.uima.phrrecog.fsArrayToTraversable

sealed trait ConstraintOperator

trait UnaryConstraintOperator {
  def apply(phr: Phrase, arg: Any): Boolean
}

trait BinaryConstraintOperator {
  def apply(leftArg: Any, rightArg: Any): Boolean
}

case object Equals extends BinaryConstraintOperator {
  override def apply(leftArg: Any, rightArg: Any): Boolean =
    leftArg == rightArg
}

case object HasHeadsPath extends UnaryConstraintOperator {
  def apply(phr: Phrase, arg: Any): Boolean =
    arg match {
      case paths: Set[Iterable[String]] => paths.exists(apply(phr, _))
      case path: Iterable[String] => matches(phr, path.toList)
      case u => throw new IllegalStateException("Can't apply HasHeadsPath to arg %s".format(u))
    }

  private def matches(phr: Phrase, expectedHeads: List[String]): Boolean =
    if (expectedHeads.isEmpty) true
    else if (phr == null) false
    else if (phr.getHead.getWord.getCoveredText == expectedHeads.head) {
      val depPhrases = fsArrayToTraversable(phr.getDependentPhrases, classOf[Phrase])
      if (depPhrases.isEmpty) matches(null, expectedHeads.tail)
      else depPhrases.exists(matches(_, expectedHeads.tail))
    } else false
}