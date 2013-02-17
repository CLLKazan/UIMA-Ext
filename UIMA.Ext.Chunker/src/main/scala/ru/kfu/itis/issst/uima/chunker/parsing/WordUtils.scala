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
import ru.kfu.itis.cll.uima.cas.FSUtils

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
object WordUtils {

  def checkGrammems(w: Word, pos: String, grs: String*): Boolean = {
    require(w != null, "word annotation is null")
    FSCollectionFactory.create(w.getWordforms(), classOf[Wordform]).exists(wf =>
      pos == wf.getPos()
        && grs.forall(gr => FSUtils.toSet(wf.getGrammems()).contains(gr)))
  }

}