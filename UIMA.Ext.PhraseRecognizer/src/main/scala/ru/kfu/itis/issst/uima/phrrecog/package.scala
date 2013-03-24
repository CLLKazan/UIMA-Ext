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
import org.apache.uima.cas.FeatureStructure
import scala.util.control.Breaks

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
      toTraverseableLocal(np, ignoreAux).foreach(f)
      for (subNP <- traversableNPArray(np.getDependentPhrases()))
        phrrecog.toTraversable(subNP, false).foreach(f(_))
    }
  }

  private def toTraverseableLocal[U](np: NounPhrase, ignoreAux: Boolean): Traversable[Word] = new Traversable[Word] {
    override def foreach[U](f: Word => U) {
      f(np.getHead)
      if (!ignoreAux && np.getPreposition != null) f(np.getPreposition)
      if (!ignoreAux && np.getParticle != null) f(np.getParticle)
      np.getDependentWords() match {
        case null =>
        case depsFS => for (i <- 0 until depsFS.size)
          f(depsFS.get(i).asInstanceOf[Word])
      }
    }
  }

  private def toTraverseableLocal[U](np: NounPhrase): Traversable[Word] =
    toTraverseableLocal(np, false)

  // TODO low priority: move to scala-uima-common utility package
  private def fsArrayToTraversable[FST <: FeatureStructure](
    fsArr: ArrayFS, fstClass: Class[FST]): Traversable[FST] = new Traversable[FST] {
    override def foreach[U](f: FST => U): Unit =
      if (fsArr != null)
        for (i <- 0 until fsArr.size)
          f(fsArr.get(i).asInstanceOf[FST])
  }

  def traversableNPArray(npArr: ArrayFS): Traversable[NounPhrase] =
    fsArrayToTraversable(npArr, classOf[NounPhrase])

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

  /**
   * @return None if tree of given np does not contain given word.
   * Else return list where head is a sub-np containing given word and tail is ancestor NPs chain.
   */
  def getDependencyChain(np: NounPhrase, w: Word): Option[List[NounPhrase]] = {
    require(w != null, "w is NULL")
    def searchLocal(ancestorChain: List[NounPhrase], np: NounPhrase): Option[List[NounPhrase]] = {
      if (toTraverseableLocal(np).exists(_ == w)) Some(np :: ancestorChain)
      else {
        val breaks = new Breaks
        import breaks.{ break, breakable }
        var result: Option[List[NounPhrase]] = None
        breakable {
          for (subNP <- traversableNPArray(np.getDependentPhrases)) {
            result = searchLocal(np :: ancestorChain, subNP)
            if (result.isDefined)
              break
          }
        }
        result
      }
    }
    searchLocal(Nil, np)
  }
}