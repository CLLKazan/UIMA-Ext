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
import org.uimafit.util.CasUtil
import ru.kfu.itis.cll.uima.cas.AnnotationOffsetComparator
import scala.math.Ordering._
import ru.kfu.itis.issst.uima.phrrecog.cas.NounPhrase
import org.apache.uima.cas.text.AnnotationFS
import org.apache.uima.cas.text.AnnotationIndex
import org.apache.uima.cas.FSIterator
import ru.kfu.cll.uima.tokenizer.fstype.NUM
import org.apache.uima.cas.text.AnnotationFS
import ru.kfu.cll.uima.segmentation.SegmentationUtils._
import ru.kfu.cll.uima.tokenizer.TokenUtils._
import ru.kfu.itis.issst.uima.ner.cas.PossibleNE
import ru.kfu.itis.cll.uima.cas.FSUtils

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class PossibleNEAnnotator extends JCasAnnotator_ImplBase {

  private var cwType: Type = _
  private var numType: Type = _
  private var tokenType: Type = _

  override def process(jCas: JCas) {
    val ts = jCas.getTypeSystem
    cwType = jCas.getCasType(CW.typeIndexID)
    numType = jCas.getCasType(NUM.typeIndexID)
    tokenType = jCas.getCasType(Token.typeIndexID)

    val candidates = HashSet.empty[PossibleNEWrapper]
    // search among NPs
    // TODO search among inner NPs
    for (phrase <- jCas.getAnnotationIndex(NounPhrase.typeIndexID).asInstanceOf[jl.Iterable[NounPhrase]]) {
      val firstToken = getFirstWord(phrase, true) match {
        case null => null
        case firstWord => firstWord.getToken
      }
      if (ts.subsumes(cwType, firstToken.getType) // NP starting with Capitalized Word
        || (ts.subsumes(numType, firstToken.getType) && quoted(phrase))) // NP starting with NUM and wrapped in quotation markes
        candidates += PossibleNEWrapper.of(phrase) // this semicolon is required
    }
    // apply enrichment by adding adjacent prepositional phrases to candidates
    {
      val newCandidates = HashSet.empty[PossibleNEWrapper]
      for (cand <- candidates)
        newCandidates ++= addAdjacentNP(jCas, cand)
      candidates ++= newCandidates
    }
    // make annotations
    for (cand <- candidates) makeAnnotation(jCas, cand)
  }

  private def makeAnnotation(jCas: JCas, pneWrapper: PossibleNEWrapper) {
    val pne = new PossibleNE(jCas)
    val pneTokens = pneWrapper.tokens
    pne.setBegin(pneTokens.firstKey.getBegin)
    pne.setEnd(pneTokens.lastKey.getEnd)
    // set head
    if (pneWrapper.head != null) pne.setHead(pneWrapper.head)
    // set tokens(words)
    pne.setWords(FSUtils.toFSArray(jCas, pneWrapper.tokens))
    pne.addToIndexes()
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

  private def adjoin(w1: Word, w2: Word): Boolean =
    areAdjoining(w1.getToken.asInstanceOf[Token], w2.getToken.asInstanceOf[Token])

  private def quoted(np: NounPhrase): Boolean =
    isLeftQuoted(getFirstWord(np, false).getToken.asInstanceOf[Token]) &&
      isRightQuoted(getLastWord(np, false).getToken.asInstanceOf[Token])
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
    new PossibleNEWrapper(getWords(np, true), np.getHead.getWord)
}