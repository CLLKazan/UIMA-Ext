/**
 *
 */
package ru.kfu.itis.issst.uima.phrrecog.util
import org.uimafit.component.JCasAnnotator_ImplBase
import org.apache.uima.jcas.JCas
import java.io.File
import ru.kfu.itis.cll.uima.cas.AnnotationUtils
import ru.kfu.itis.cll.uima.commons.DocumentMetadata
import java.net.URI
import org.apache.commons.io.FilenameUtils
import scala.io.Source
import scala.collection.JavaConversions.iterableAsScalaIterable
import ru.kfu.cll.uima.segmentation.fstype.Paragraph
import org.apache.uima.cas.text.AnnotationFS
import scala.collection.mutable.ListBuffer
import StandoffAnnotationsProcessor._
import org.opencorpora.cas.Word
import org.apache.uima.jcas.cas.FSArray
import java.util.regex.Pattern
import org.uimafit.util.CasUtil
import ru.kfu.cll.uima.tokenizer.fstype.Token
import org.apache.uima.cas.Type
import ru.kfu.itis.issst.uima.phrrecog.cas.Phrase
import org.apache.uima.UimaContext
import ru.kfu.itis.cll.uima.util.AnnotatorUtils._
import scala.collection.mutable.HashMap

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class StandoffAnnotationsProcessor extends JCasAnnotator_ImplBase {

  // state
  private var tokenType: Type = _
  private var annoStrParser: AnnotationStringParser = _

  override def initialize(ctx: UimaContext) {
    super.initialize(ctx)
    val annoStrParserClassName = ctx.getConfigParameterValue(ParamAnnotationStringParserClass).asInstanceOf[String]
    mandatoryParam(ParamAnnotationStringParserClass, annoStrParserClassName)
    val annoStrParserClass = Class.forName(annoStrParserClassName)
    annoStrParser = annoStrParserClass.newInstance().asInstanceOf[AnnotationStringParser]
  }

  override def process(jCas: JCas) {
    val annFile = getAnnFile(jCas)
    val annLines = Source.fromFile(annFile, "utf-8").getLines()
    val paraIdx = jCas.getAnnotationIndex(Paragraph.typeIndexID)
    tokenType = jCas.getCasType(Token.typeIndexID)
    for ((paragraph, paragrNum) <- paraIdx.zipWithIndex)
      if (annLines.hasNext)
        parseAnnoLine(paragraph, annLines.next())
      else throw new IllegalStateException("Standoff annotations files ends before line %s".format(paragrNum))
  }

  private def parseAnnoLine(paraAnno: AnnotationFS, annLine: String) {
    val jCas = paraAnno.getCAS().getJCas()
    val paraOffset = paraAnno.getBegin
    val phraseStrings = annLine.split("\\|")
    val paraTokens = getParagraphTokens(paraAnno)
    for (phrStr <- phraseStrings) {
      val prefixWordMap = HashMap.empty[String, ListBuffer[Word]]
      val rawWords = tokenize(phrStr)
      for (rawWord <- rawWords) {
        val (wordPrefix, wordStr, wordNum) = parseWordString(rawWord)
        val (wordBegin, wordEnd) = getOffsets(paraTokens, wordStr, wordNum)
        val word = new Word(jCas)
        word.setBegin(wordBegin)
        word.setEnd(wordEnd)
        prefixWordMap.get(wordPrefix) match {
          case None => prefixWordMap(wordPrefix) = {
            val list = ListBuffer.empty[Word]
            list += word
            list
          }
          case Some(list) => list += word
        }
      }
      if (!prefixWordMap.isEmpty) annoStrParser.parseTokens(jCas, prefixWordMap)
    }
  }

  private def getParagraphTokens(paraAnno: AnnotationFS): Array[AnnotationFS] = {
    val tokensList = CasUtil.selectCovered(paraAnno.getCAS(), tokenType, paraAnno)
    tokensList.toArray(new Array[AnnotationFS](tokensList.size()))
  }

  private def getOffsets(txtTokens: Array[AnnotationFS], word: String, numberOpt: Option[Int]): (Int, Int) = {
    // define recursive function
    def getOffsets(fromToken: Int, number: Int): AnnotationFS = {
      val wordIndex = txtTokens.indexWhere(_.getCoveredText() == word, fromToken)
      if (wordIndex < 0)
        throw new IllegalStateException("Cant find word #%s %s in line:\n%s".format(
          number, word, makeParagraphString(txtTokens)))
      if (number == 1) txtTokens(wordIndex)
      else getOffsets(wordIndex + 1, number - 1)
    }
    val number = if (numberOpt.isDefined) numberOpt.get else 1
    val offsetsAnno = getOffsets(0, number)
    if (!numberOpt.isDefined && txtTokens.filter(_.getCoveredText() == word).size > 1)
      throw new IllegalStateException("Ambiguous word reference %s in line:\n%s".format(
        word, makeParagraphString(txtTokens)))
    (offsetsAnno.getBegin(), offsetsAnno.getEnd())
  }

  private def makeParagraphString(txtTokens: Array[AnnotationFS]): String =
    txtTokens.map(_.getCoveredText()).mkString(" ")

  private def getAnnFile(jCas: JCas): File = {
    val docMeta = AnnotationUtils.getSingleAnnotation(jCas, classOf[DocumentMetadata])
    if (docMeta == null) throw new IllegalStateException("No DocumentMetadata")
    val docUri = new URI(docMeta.getSourceUri())
    val docFile = new File(docUri)
    val baseName = FilenameUtils.getBaseName(docFile.getName())
    val annFile = new File(docFile.getParent(), baseName + ".ann")
    if (!annFile.isFile())
      throw new IllegalStateException("No ann file for %s".format(docFile))
    annFile
  }
}

object StandoffAnnotationsProcessor {
  val ParamAnnotationStringParserClass = "AnnotationStringParserClass"

  private val WordPattern = Pattern.compile("([\\p{Alnum}_]+=)?(\\d+:)?(.+)")

  private def tokenize(str: String): List[String] =
    str.split("\\s+").toList.dropWhile(_.isEmpty)

  private def parseWordString(str: String): (String, String, Option[Int]) = {
    val matcher = WordPattern.matcher(str)
    if (!matcher.matches()) throw new IllegalStateException("Illegal word string: %s".format(str))
    val prefix = matcher.group(1) match {
      case null => null
      case "" => null
      case prefix => prefix.substring(0, prefix.length() - 1)
    }
    val numberOpt = matcher.group(2) match {
      case null => None
      case "" => None
      case num => Some(num.substring(0, num.length() - 1).toInt)
    }
    val wordTxt = matcher.group(3)
    (prefix, wordTxt, numberOpt)
  }
}

trait AnnotationStringParser {
  def parseTokens(jCas: JCas, prefixedTokensMap: scala.collection.Map[String, Seq[Word]])
}