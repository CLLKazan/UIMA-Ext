package ru.kfu.itis.issst.uima.shaltef.mappings

import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import ru.kfu.itis.issst.uima.phrrecog.cas.Phrase

abstract class ConstraintTarget {

  def getValue(phr: Phrase): Any

}

class HeadGrammemeConstraint(val gramCategory: String) extends ConstraintTarget {
  override def getValue(phr: Phrase): Any = {
    // TODO
    throw new UnsupportedOperationException
  }
  override def equals(obj: Any): Boolean = obj match {
    case that: HeadGrammemeConstraint => this.gramCategory == that.gramCategory
    case _ => false
  }
  override def toString = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
    .append(gramCategory).toString
}

object PrepositionConstraint extends ConstraintTarget {
  override def getValue(phr: Phrase): Any = {
    // TODO
    throw new UnsupportedOperationException
  }
}

object WordsConstraint extends ConstraintTarget {
  override def getValue(phr: Phrase): Any = {
    // TODO
    throw new UnsupportedOperationException
  }
}