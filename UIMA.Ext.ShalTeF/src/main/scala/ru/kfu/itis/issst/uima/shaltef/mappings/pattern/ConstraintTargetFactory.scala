package ru.kfu.itis.issst.uima.shaltef.mappings.pattern

import ru.kfu.itis.issst.uima.phrrecog.cas.Phrase
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary
import ru.kfu.itis.issst.uima.phrrecog.cas.NounPhrase

class ConstraintTargetFactory(morphDict: MorphDictionary) {

  def headFeature(featStr: String): ConstraintTarget = {
    getGramCategory(morphDict, featStr) match {
      case Some(gramIds) => new HeadGrammemeConstraint(gramIds)
      case None => throw new IllegalArgumentException(
        "Unknown head constraint target: %s".format(featStr))
    }
  }

  def prepositionTarget: ConstraintTarget = PrepositionConstraint
}

private[pattern] class HeadGrammemeConstraint(protected val gramIds: Set[String])
  extends ConstraintTarget with GrammemeExtractor {

  override def getValue(phr: Phrase): String = extractGrammeme(phr.getHead)

  override def equals(obj: Any): Boolean = obj match {
    case that: HeadGrammemeConstraint => this.gramIds == that.gramIds
    case _ => false
  }
  override def toString =
    new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
      .append(gramIds).toString
}

private[pattern] object PrepositionConstraint extends ConstraintTarget {
  override def getValue(phr: Phrase): String =
    phr match {
      case np: NounPhrase =>
        val prep = np.getPreposition
        if (prep != null)
          prep.getWord.getCoveredText
        else null
      case otherPhrase => null
    }
}