package ru.kfu.itis.issst.uima.shaltef.mappings

trait MatchingContext {

}

private[mappings] class DefaultMatchingContext extends MatchingContext {

}

object MatchingContext {
  def apply(): MatchingContext = new DefaultMatchingContext()
}