/**
 *
 */
package ru.kfu.itis.issst.uima.chunker.parsing
import org.opencorpora.cas.Word
import org.uimafit.util.FSCollectionFactory._
import org.opencorpora.cas.Wordform
import scala.collection.JavaConversions._
import org.uimafit.util.CasUtil
import org.uimafit.util.FSCollectionFactory
import ru.kfu.itis.cll.uima.cas.FSUtils._

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
object WordUtils {

  def checkGrammems(w: Word, pos: String, grms: GrammemeMatcher*): Boolean = {
    require(w != null, "word annotation is null")
    if (w.getWordforms() == null) false
    else FSCollectionFactory.create(w.getWordforms(), classOf[Wordform]).exists(wf =>
      pos == wf.getPos()
        && grms.forall(_ match {
          case GrammemeRequired(gr) => toSet(wf.getGrammems()).contains(gr)
          case GrammemeProhibited(gr) => !toSet(wf.getGrammems()).contains(gr)
        }))
  }

  def has(gr: String) = new GrammemeRequired(gr)
  def hasNot(gr: String) = new GrammemeProhibited(gr)
}

sealed abstract class GrammemeMatcher
case class GrammemeRequired(gr: String) extends GrammemeMatcher
case class GrammemeProhibited(gr: String) extends GrammemeMatcher