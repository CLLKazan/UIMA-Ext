package ru.kfu.itis.issst.uima.shaltef.mappings.impl

import ru.kfu.itis.issst.uima.shaltef.mappings.DepToArgMappingsBuilder
import ru.kfu.itis.issst.uima.shaltef.mappings.DepToArgMapping
import ru.kfu.itis.issst.uima.shaltef.mappings.DepToArgMappingsHolder
import org.opencorpora.cas.Wordform
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

private[mappings] class DefaultDepToArgMappingsBuilder extends DepToArgMappingsBuilder {

  private val triggerLemmaId2Mappings = mutable.Map.empty[Int, ListBuffer[DepToArgMapping]]

  override def add(mp: DepToArgMapping) {
    for (tli <- mp.triggerLemmaIds) {
      val buf = triggerLemmaId2Mappings.get(tli) match {
        case Some(buf) => buf
        case None => {
          val newBuf = ListBuffer.empty[DepToArgMapping]
          triggerLemmaId2Mappings(tli) = newBuf
          newBuf
        }
      }
      buf += mp
    }
  }

  override def build(): DepToArgMappingsHolder =
    new DefaultDepToArgMappingsHolder(triggerLemmaId2Mappings.mapValues(_.toList).toMap)
}

private[mappings] class DefaultDepToArgMappingsHolder(
  val triggerLemmaId2Mappings: Map[Int, List[DepToArgMapping]])
  extends DepToArgMappingsHolder {

  def containsTriggerLemma(lemmaId: Int): Boolean =
    triggerLemmaId2Mappings.contains(lemmaId)

  def getMappingsTriggeredBy(wf: Wordform): Iterable[DepToArgMapping] =
    triggerLemmaId2Mappings.getOrElse(wf.getLemmaId(), Nil)
}