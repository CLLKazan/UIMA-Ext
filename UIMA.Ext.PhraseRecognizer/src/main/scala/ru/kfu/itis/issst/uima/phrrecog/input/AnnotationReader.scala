/**
 *
 */
package ru.kfu.itis.issst.uima.phrrecog.input
import scala.util.parsing.input.Reader
import org.apache.uima.cas.text.AnnotationFS
import scala.util.parsing.input.Position
import org.uimafit.util.CasUtil

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class AnnotationSpan[A >: Null <: AnnotationFS](annoList: List[A]) {
  require(!annoList.isEmpty, "annotation list is empty")

  private val baseOffset = annoList.head.getBegin
  private lazy val endOffset = annoList.last.getEnd

  // protected val endOfSequence: A

  lazy val reader: Reader[A] = new AnnotationReader(annoList)

  private class AnnotationReader private[AnnotationSpan] (inputList: List[A]) extends Reader[A] {

    override val atEnd: Boolean = inputList.isEmpty

    override val first: A =
      if (atEnd) null
      else inputList.head

    override val rest: AnnotationReader =
      if (atEnd) this
      else new AnnotationReader(inputList.tail)

    override lazy val pos: Position =
      if (atEnd) new EndOfSequencePosition
      else new AnnotationPosition(first)
  }

  class AnnotationPosition(anno: AnnotationFS) extends Position {
    require(anno.getBegin() >= baseOffset, "illegal begin of annotation")

    override val line: Int = 1
    // NOTE! Column numbers start at 1
    override val column: Int = anno.getBegin() - baseOffset + 1

    override lazy val lineContents: String = inputContentString
  }

  class EndOfSequencePosition extends Position {
    override val line: Int = 1
    // NOTE! Column numbers start at 1
    override val column: Int = endOffset

    override lazy val lineContents: String = inputContentString
  }

  private lazy val inputContentString = {
    val sb = new StringBuilder()
    appendAnnotationContent(sb, 0, annoList)
    sb.toString()
  }

  private def appendAnnotationContent(sb: StringBuilder, lastAnnoEnd: Int, list: List[A]): Unit =
    if (!list.isEmpty) {
      val anno = list.head
      lastAnnoEnd.until(anno.getBegin()).foreach(sb.append(' '))
      sb.append(anno.getCoveredText())
      appendAnnotationContent(sb, anno.getEnd(), list.tail)
    }
}