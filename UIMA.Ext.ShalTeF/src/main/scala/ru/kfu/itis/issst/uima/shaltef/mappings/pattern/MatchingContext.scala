package ru.kfu.itis.issst.uima.shaltef.mappings.pattern

import org.opencorpora.cas.Wordform

trait MatchingContext {

  def triggerHead: Wordform

}

private[mappings] class DefaultMatchingContext(val trigger: Wordform) extends MatchingContext {
  val triggerHead = trigger
}

object MatchingContext {
  def apply(trigger: Wordform): MatchingContext = new DefaultMatchingContext(trigger)
}