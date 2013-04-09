package ru.kfu.itis.issst.uima.shaltef.mappings.impl

import ru.kfu.itis.issst.uima.shaltef.mappings.DepToArgMapping
import org.apache.uima.cas.Type
import ru.kfu.itis.issst.uima.shaltef.mappings.SlotMapping
import scala.collection.immutable.Iterable
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import org.apache.commons.lang3.builder.HashCodeBuilder

private[mappings] class DefaultDepToArgMapping(val templateAnnoType: Type,
  val triggerLemmaIds: Set[Int], val slotMappings: List[SlotMapping])
  extends DepToArgMapping {

  override def equals(obj: Any): Boolean =
    obj match {
      case that: DefaultDepToArgMapping => this.templateAnnoType == that.templateAnnoType &&
        this.triggerLemmaIds == that.triggerLemmaIds && this.slotMappings == that.slotMappings
      case _ => false
    }

  override def hashCode(): Int =
    new HashCodeBuilder().append(templateAnnoType).append(triggerLemmaIds).append(slotMappings)
      .toHashCode()

  override def toString: String = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
    append("templateAnnoType", templateAnnoType).
    append("triggerLemmaIds", triggerLemmaIds).
    append("slotMappings", slotMappings).toString;
}