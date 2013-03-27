package ru.kfu.itis.issst.uima.shaltef.mappings.pattern

import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import ru.kfu.itis.issst.uima.phrrecog.cas.Phrase

trait ConstraintTarget {

  def getValue(phr: Phrase): Any

}