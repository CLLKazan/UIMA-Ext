/**
 *
 */
package ru.kfu.itis.issst.uima

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
import ru.kfu.itis.issst.uima.phrrecog.cas.Phrase
import org.opencorpora.cas.Word
import org.uimafit.util.FSCollectionFactory
import scala.collection.JavaConversions._
import ru.kfu.itis.issst.uima.phrrecog.cas.NounPhrase
import scala.collection.immutable.SortedSet
import scala.math.Ordering
import ru.kfu.itis.cll.uima.cas.AnnotationOffsetComparator
import scala.collection.mutable.ListBuffer
import ru.kfu.itis.cll.uima.cas.FSUtils
import ru.kfu.itis.issst.uima.phrrecog.cas.VerbPhrase
import org.apache.uima.cas.ArrayFS

package object phrrecog {

  val PhraseTypeNP = "NP"
  val PhraseTypeVP = "VP"

  val annOffsetComp = Ordering.comparatorToOrdering(
    AnnotationOffsetComparator.instance(classOf[Word]))

  /**
   * Returns the first word of NP.
   * If ignoreAux is true then leading preposition or particle is ignored.
   */
  def getFirstWord(np: NounPhrase, ignoreAux: Boolean): Word =
    toTraversable(np, ignoreAux).minBy(_.getBegin())

  private[phrrecog] def toTraversable(np: NounPhrase, ignoreAux: Boolean): Traversable[Word] = new Traversable[Word] {
    override def foreach[U](f: Word => U) {
      f(np.getHead)
      if (!ignoreAux && np.getPreposition != null) f(np.getPreposition)
      if (!ignoreAux && np.getParticle != null) f(np.getParticle)
      np.getDependentWords() match {
        case null =>
        case depsFS => for (i <- 0 until depsFS.size)
          f(depsFS.get(i).asInstanceOf[Word])
      }
      np.getDependentPhrases() match {
        case null =>
        case depPhrases => for (i <- 0 until depPhrases.size)
          phrrecog.toTraversable(depPhrases.get(i).asInstanceOf[NounPhrase], false).foreach(f(_))
      }
    }
  }

  /**
   * Returns the last word of NP.
   * If ignoreAux is true then leading preposition or particle is ignored.
   */
  def getLastWord(np: NounPhrase, ignoreAux: Boolean): Word =
    toTraversable(np, ignoreAux).maxBy(_.getBegin)

  def getWords(np: NounPhrase, ignoreAux: Boolean): SortedSet[Word] =
    SortedSet.empty[Word](annOffsetComp) ++ toTraversable(np, ignoreAux)

  def getOffsets(np: NounPhrase): (Int, Int) = (getFirstWord(np, false).getBegin(), getLastWord(np, false).getEnd())

  def containWord(np: NounPhrase, w: Word): Boolean =
    toTraversable(np, false).exists(_ == w)
}