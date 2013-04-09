package ru.kfu.itis.issst.uima.shaltef.mappings.impl

import ru.kfu.itis.issst.uima.shaltef.mappings.DepToArgMappingsBuilder
import ru.kfu.itis.issst.uima.shaltef.mappings.DepToArgMapping
import ru.kfu.itis.issst.uima.shaltef.mappings.DepToArgMappingsHolder
import org.opencorpora.cas.Wordform
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.collection.immutable.TreeMap
import ru.kfu.itis.issst.uima.shaltef.mappings.DepToArgMapping
import grizzled.slf4j.Logging

private[mappings] class DefaultDepToArgMappingsBuilder extends DepToArgMappingsBuilder with Logging {

  private val triggerLemmaId2Mappings = mutable.Map.empty[Int, ListBuffer[Int]]
  private var id2Mapping = TreeMap.empty[Int, DepToArgMapping]
  private val mapping2Id = mutable.Map.empty[DepToArgMapping, Int]

  override def add(mp: DepToArgMapping) {
    if (mapping2Id.contains(mp))
      warn("Attempt to add duplicate mapping:\n%s".format(mp))
    else {
      val mpId =
        if (id2Mapping.isEmpty) 1
        else id2Mapping.lastKey + 1
      id2Mapping += (mpId -> mp)
      mapping2Id(mp) = mpId
      for (tli <- mp.triggerLemmaIds) {
        val buf = triggerLemmaId2Mappings.get(tli) match {
          case Some(buf) => buf
          case None => {
            val newBuf = ListBuffer.empty[Int]
            triggerLemmaId2Mappings(tli) = newBuf
            newBuf
          }
        }
        buf += mpId
      }
    }
  }

  override def getMappings(): Iterable[DepToArgMapping] =
    id2Mapping.keys.map(id2Mapping(_)).toList

  override def replace(old: DepToArgMapping, newMp: DepToArgMapping): Unit =
    mapping2Id.get(old) match {
      case Some(mpId) =>
        id2Mapping += (mpId -> newMp)
        mapping2Id.remove(old)
        if (mapping2Id.contains(newMp))
          throw new IllegalStateException(
            "Replacement will raise duplicate mappings:\n%s".format(newMp))
        mapping2Id(newMp) = mpId
      case None => throw new IllegalStateException(
        "Can't replace non-registered mapping:\n%s".format(old))
    }

  override def build(): DepToArgMappingsHolder =
    new DefaultDepToArgMappingsHolder(
      triggerLemmaId2Mappings.mapValues(
        _.toList.map(id2Mapping(_)))
        .toMap)
}

private[mappings] class DefaultDepToArgMappingsHolder(
  val triggerLemmaId2Mappings: Map[Int, List[DepToArgMapping]])
  extends DepToArgMappingsHolder {

  def containsTriggerLemma(lemmaId: Int): Boolean =
    triggerLemmaId2Mappings.contains(lemmaId)

  def getMappingsTriggeredBy(wf: Wordform): Iterable[DepToArgMapping] =
    triggerLemmaId2Mappings.getOrElse(wf.getLemmaId(), Nil)
}