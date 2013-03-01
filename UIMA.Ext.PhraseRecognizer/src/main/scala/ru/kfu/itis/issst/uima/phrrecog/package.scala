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

package object phrrecog {

  val PhraseTypeNP = "NP"
  val PhraseTypeVP = "VP"

  private val annOffsetComp = Ordering.comparatorToOrdering(
    AnnotationOffsetComparator.instance(classOf[Word]))

  /**
   * Returns the first word of NP.
   * If ignoreAux is true then leading preposition or particle is ignored.
   * Note! If NP is prepositional then a preposition is ignored.
   */
  def getFirstWord(np: NounPhrase, ignoreAux: Boolean): Word = {
    var candidates = np.getHead() :: Nil
    if (!ignoreAux && np.getPreposition != null) np.getPreposition :: candidates
    if (!ignoreAux && np.getParticle != null) np.getParticle :: candidates
    np.getDependents() match {
      case null =>
      case depsFS if depsFS.size == 0 =>
      case depsFS => FSCollectionFactory.create(depsFS, classOf[Word]).head :: candidates
    }
    candidates.minBy(_.getBegin)
  }

  def getWords(np: NounPhrase, ignoreAux: Boolean): SortedSet[Word] = {
    var result = SortedSet.empty[Word](annOffsetComp) + np.getHead
    val depsFS = np.getDependents
    if (depsFS != null && depsFS.size() > 0) result ++= FSCollectionFactory.create(depsFS, classOf[Word])
    if (!ignoreAux && np.getPreposition != null) result += np.getPreposition
    if (!ignoreAux && np.getParticle != null) result += np.getParticle
    result
  }
}