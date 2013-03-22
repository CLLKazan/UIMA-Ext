package ru.kfu.itis.issst.uima.shaltef.mappings

import org.apache.uima.cas.Type
import org.apache.uima.cas.Feature

trait DepToArgMapping {

  val templateAnnoType: Type

  def getSlotPatterns: Iterable[(PhrasePattern, Feature)]
}