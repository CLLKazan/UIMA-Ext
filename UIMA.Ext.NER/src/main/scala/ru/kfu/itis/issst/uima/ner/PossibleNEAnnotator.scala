/**
 *
 */
package ru.kfu.itis.issst.uima.ner
import org.uimafit.component.JCasAnnotator_ImplBase
import org.apache.uima.jcas.JCas
import ru.kfu.cll.uima.tokenizer.fstype.Token
import org.opencorpora.cas.Word
import scala.collection.mutable.HashSet
import org.apache.commons.lang3.builder.HashCodeBuilder
import scala.collection.JavaConversions._
import java.{ lang => jl }
import ru.kfu.itis.issst.uima.phrrecog._
import org.apache.uima.cas.Type
import ru.kfu.cll.uima.tokenizer.fstype.CW
import org.uimafit.util.FSCollectionFactory
import scala.collection.immutable.SortedSet
import ru.kfu.cll.uima.segmentation.fstype.QSegment
import org.uimafit.util.CasUtil
import ru.kfu.itis.cll.uima.cas.AnnotationOffsetComparator
import scala.math.Ordering._
import ru.kfu.itis.issst.uima.phrrecog.cas.NounPhrase
import org.apache.uima.cas.text.AnnotationFS
import org.apache.uima.cas.text.AnnotationIndex
import org.apache.uima.cas.FSIterator

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class PossibleNEAnnotator extends JCasAnnotator_ImplBase {

  private var cwType: Type = _

  override def process(jCas: JCas) {
    val ts = jCas.getTypeSystem
    cwType = jCas.getCasType(CW.typeIndexID)
    val candidates = HashSet.empty[PossibleNEWrapper]
    // search among NPs
    // TODO in NPR put an initial preposition into separate feature
    // TODO NPR must ensure that a dependents array feature value is ordered by words appearance in a text
    for (phrase <- jCas.getAnnotationIndex(NounPhrase.typeIndexID).asInstanceOf[jl.Iterable[NounPhrase]])
      if (ts.subsumes(cwType, getFirstWord(phrase, true).getType()))
        candidates += PossibleNEWrapper.of(phrase); // this semicolon is required
    // apply enrichment by adding adjacent prepositional phrases to candidates
    {
      val newCandidates = HashSet.empty[PossibleNEWrapper]
      for (cand <- candidates)
        newCandidates ++= addAdjacentNP(jCas, cand)
      candidates ++= newCandidates
    }
    // TODO apply enrichment by adding candidates that consist of consecutive CWs

    // apply enrichment by adding full content of a QSegment if it does not wrap direct speech 
    for (qs <- jCas.getAnnotationIndex(QSegment.typeIndexID).asInstanceOf[jl.Iterable[QSegment]]) {
      // TODO
    }
    // make annotations
  }

  private def addAdjacentNP(jCas: JCas, pne: PossibleNEWrapper): TraversableOnce[PossibleNEWrapper] = {
    val npIter = jCas.getAnnotationIndex(NounPhrase.typeIndexID).asInstanceOf[AnnotationIndex[NounPhrase]].iterator()
    // inner recursive function
    def addAdjacentNP(pne: PossibleNEWrapper): List[PossibleNEWrapper] = {
      val pneLast = pne.tokens.lastKey
      npIter.moveTo(pneLast)
      if (npIter.get.getBegin == pneLast.getBegin && npIter.isValid) npIter.moveToNext()
      if (npIter.isValid && adjoin(pneLast, getFirstWord(npIter.get, false))) {
        val longerPNE = pne + npIter.get
        longerPNE :: addAdjacentNP(longerPNE)
      } else Nil
    } // end
    // invoke
    addAdjacentNP(pne)
  }

  // TODO move to utilities object
  private def adjoin(w1: Word, w2: Word): Boolean = adjoin(w1.getToken, w2.getToken)

  // TODO test
  private def adjoin(t1: AnnotationFS, t2: AnnotationFS): Boolean = {
    val jCas = t1.getCAS.getJCas
    val tokenIter = jCas.getAnnotationIndex(Token.typeIndexID).asInstanceOf[AnnotationIndex[Token]].iterator()
    tokenIter.moveTo(t1)
    assert(t1 == tokenIter.get)
    tokenIter.moveToNext()
    tokenIter.isValid && tokenIter.get == t2
  }
}

private[ner] class PossibleNEWrapper private (val tokens: SortedSet[Word], val head: Word) {
  require(tokens.contains(head))

  // TODO how to determine head?
  // way1: check cases
  def +(np: NounPhrase): PossibleNEWrapper = new PossibleNEWrapper(this.tokens ++ getWords(np, false), this.head)

  override def equals(obj: Any): Boolean =
    obj.isInstanceOf[PossibleNEWrapper] && {
      val that = obj.asInstanceOf[PossibleNEWrapper]
      this.tokens == that.tokens && this.head == that.head
    }
  override def hashCode: Int =
    new HashCodeBuilder().append(tokens).append(head).toHashCode()
}

private[ner] object PossibleNEWrapper {
  def of(np: NounPhrase): PossibleNEWrapper =
    // TODO consider adding internal punctuation to 'tokens' feature of PossibleNE.
    // This should be implemented in NPR annotator if required.
    new PossibleNEWrapper(getWords(np, true), np.getHead)
}