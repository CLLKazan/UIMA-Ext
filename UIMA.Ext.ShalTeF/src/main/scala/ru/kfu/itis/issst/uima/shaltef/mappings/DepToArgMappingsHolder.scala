package ru.kfu.itis.issst.uima.shaltef.mappings

import org.opencorpora.cas.Wordform
import ru.kfu.itis.issst.uima.shaltef.mappings.impl.DefaultDepToArgMappingsBuilder

trait DepToArgMappingsHolder {

  def containsTriggerLemma(lemmaId: Int): Boolean

  def getMappingsTriggeredBy(wf: Wordform): Iterable[DepToArgMapping]
}

trait DepToArgMappingsBuilder {

  def add(mp: DepToArgMapping)

  def build(): DepToArgMappingsHolder

}

object DepToArgMappingsBuilder {
  def apply(): DepToArgMappingsBuilder = new DefaultDepToArgMappingsBuilder
}