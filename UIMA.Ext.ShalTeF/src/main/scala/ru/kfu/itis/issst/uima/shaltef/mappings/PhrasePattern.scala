package ru.kfu.itis.issst.uima.shaltef.mappings

import ru.kfu.itis.issst.uima.phrrecog.cas.Phrase

trait PhrasePattern {

  val isOptional: Boolean
  def matches(phr: Phrase): Boolean

}