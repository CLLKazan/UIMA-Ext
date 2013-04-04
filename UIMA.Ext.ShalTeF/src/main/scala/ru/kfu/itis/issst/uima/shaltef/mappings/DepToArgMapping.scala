package ru.kfu.itis.issst.uima.shaltef.mappings

import org.apache.uima.cas.Type
import org.apache.uima.cas.Feature
import scala.collection.immutable.Iterable
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import ru.kfu.itis.issst.uima.shaltef.mappings.pattern.PhrasePattern

trait DepToArgMapping {

  val triggerLemmaIds: Set[Int]

  val templateAnnoType: Type

  def getSlotMappings: Iterable[SlotMapping]
}

class SlotMapping(val pattern: PhrasePattern, val isOptional: Boolean, 
    val slotFeatureOpt: Option[Feature]) {
  override def equals(obj: Any): Boolean = obj match {
    case that: SlotMapping => this.pattern == that.pattern && this.isOptional == that.isOptional &&
      this.slotFeatureOpt == that.slotFeatureOpt
    case _ => false
  }
  override def toString(): String = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
    append(slotFeatureOpt).append(isOptional).append(pattern).toString
}