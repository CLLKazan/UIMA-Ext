package ru.kfu.itis.issst.uima.shaltef.mappings

import grizzled.slf4j.Logging
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary
import scala.collection.JavaConversions.{ collectionAsScalaIterable, asScalaSet }

package object pattern extends Logging {

  private[pattern] val packageLogger = logger

  val grammemeAliases = Map("case" -> "CAse", "gndr" -> "GNdr")

  private[pattern] def getGramCategory(morphDict: MorphDictionary, gcId: String): Option[Set[String]] = {
    // lookup alias
    val gramCat = grammemeAliases.get(gcId) match {
      case Some(gc) => gc
      case None => gcId
    }
    morphDict.getGrammemWithChildrenBits(gramCat, false) match {
      case null => None
      case gramBS =>
        val gramIds = morphDict.toGramSet(gramBS)
        if (gramIds == null || gramIds.isEmpty())
          throw new IllegalStateException("Empty grammeme set for gramCat: %s".format(gramCat))
        Some(gramIds.toSet)
    }
  }

}