/**
 *
 */
package ru.kfu.itis.issst.uima.chunker
import org.uimafit.component.JCasAnnotator_ImplBase
import org.apache.uima.jcas.JCas
import org.opencorpora.cas.Word
import org.apache.uima.cas.text.AnnotationFS
import com.google.common.collect.ComparisonChain
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import org.apache.uima.jcas.tcas.Annotation
import org.uimafit.util.FSCollectionFactory
import org.opencorpora.cas.Wordform

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class VPChunkingAnnotator extends JCasAnnotator_ImplBase {

  override def process(jCas: JCas) {
    val targetVerbs = collectTargetVerbs(jCas)
  }

  private def collectTargetVerbs(jCas: JCas): List[Word] = {
    val resultList = ListBuffer.empty[Word]
    val _wordIndex: Iterable[Annotation] = jCas.getAnnotationIndex(Word.typeIndexID)
    val wordIndex = _wordIndex.asInstanceOf[Iterable[Word]]
    /*
    for (
      word <- wordIndex;
      wf <- FSCollectionFactory.create(word.getWordforms(), classOf[Wordform]);
      // TODO create and use constants from morphology module
      if (FSCollectionFactory.wf.getGrammems())
    ) {
      // TODO
    }
    */
    resultList.toList
  }

}

// TODO move to some utility package 
class AnnotationOffsetOrdering[A <: AnnotationFS] extends Ordering[A] {
  override def compare(first: A, second: A) =
    ComparisonChain.start()
      .compare(first.getBegin(), second.getBegin())
      .compare(second.getEnd(), first.getEnd())
      .compare(first.getType().getName(), second.getType().getName())
      .result()
}