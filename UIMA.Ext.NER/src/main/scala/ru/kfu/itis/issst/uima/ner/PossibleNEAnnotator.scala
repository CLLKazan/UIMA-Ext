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
import ru.kfu.itis.issst.uima.phrrecog.cas.Phrase
import scala.collection.JavaConversions._
import java.{ lang => jl }
import ru.kfu.itis.issst.uima.phrrecog._
import org.apache.uima.cas.Type
import ru.kfu.cll.uima.tokenizer.fstype.CW
import org.uimafit.util.FSCollectionFactory
import scala.collection.immutable.SortedSet
import ru.kfu.cll.uima.segmentation.fstype.QSegment

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class PossibleNEAnnotator extends JCasAnnotator_ImplBase {

  private var cwType: Type = _

  override def process(jCas: JCas) {
    cwType = jCas.getCasType(CW.typeIndexID)
    val candidates = HashSet.empty[PossibleNEWrapper]
    // search among NPs
    // TODO in NPR put an initial preposition into separate feature
    for (phrase <- jCas.getAnnotationIndex(Phrase.typeIndexID).asInstanceOf[jl.Iterable[Phrase]])
      if (PhraseTypeNP == phrase.getPhraseType() && getFirstToken(phrase).getType() == cwType)
        candidates += wrap(phrase)
    // TODO apply enrichment by adding adjacent prepositional phrases to candidates
        
    // TODO apply enrichment by adding candidates that consist of consecutive CWs
    
    // apply enrichment by adding full content of a QSegment if it does not wrap direct speech 
    for (qs <- jCas.getAnnotationIndex(QSegment.typeIndexID).asInstanceOf[jl.Iterable[QSegment]]) {
      // TODO
    }
    // make annotations
  }

  /**
   * Returns the token that contains the first word of NP.
   * If NP is prepositional then a preposition is ignored.
   */
  private def getFirstToken(phr: Phrase): Token = {
    val head = phr.getHead()
    val deps = phr.getDependents()
    val firstOfDepsOpt =
      if (deps == null || deps.size() == 0) None
      else Some(FSCollectionFactory.create(deps, classOf[Word]).minBy(_.getBegin()))
    val firstWord = firstOfDepsOpt match {
      case Some(firstOfDeps) =>
        if (firstOfDeps.getBegin() < head.getBegin()) firstOfDeps
        else head
      case None => head
    }
    firstWord.getToken().asInstanceOf[Token]
  }

  private def wrap(phr: Phrase): PossibleNEWrapper = {
    val deps = phr.getDependents()
    val head = phr.getHead()
    val words = if (deps == null || deps.size() == 0) Set(head)
    else FSCollectionFactory.create(deps, classOf[Word]).toSet + head
    // TODO consider adding internal punctuation to 'tokens' feature of PossibleNE.
    // This should be implemented in NPR annotator if required.
    new PossibleNEWrapper(words, phr.getHead)
  }
}

private[ner] class PossibleNEWrapper(val tokens: Set[Word], val head: Word) {
  override def equals(obj: Any): Boolean =
    obj.isInstanceOf[PossibleNEWrapper] && {
      val that = obj.asInstanceOf[PossibleNEWrapper]
      this.tokens == that.tokens && this.head == that.head
    }
  override def hashCode: Int =
    new HashCodeBuilder().append(tokens).append(head).toHashCode()
}