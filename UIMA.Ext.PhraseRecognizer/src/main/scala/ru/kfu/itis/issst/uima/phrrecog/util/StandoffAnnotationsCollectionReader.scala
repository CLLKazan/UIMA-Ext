/**
 *
 */
package ru.kfu.itis.issst.uima.phrrecog.util

import org.apache.uima.resource.metadata.ResourceMetaData
import org.apache.uima.util.Logger
import org.apache.uima.resource.ResourceManager
import org.apache.uima.jcas.JCas
import org.apache.uima.UimaContext
import org.uimafit.component.JCasCollectionReader_ImplBase
import java.io.File
import org.uimafit.descriptor.ConfigurationParameter
import StandoffAnnotationsCollectionReader._
import org.apache.commons.io.filefilter.FileFilterUtils
import java.io.FilenameFilter
import org.apache.commons.io.FilenameUtils
import scala.io.Source
import scala.collection.mutable.ListBuffer
import org.apache.uima.jcas.tcas.Annotation
import org.opencorpora.cas.Word
import org.apache.uima.jcas.cas.FSArray
import org.apache.uima.util.Progress
import ru.kfu.itis.cll.uima.commons.DocumentMetadata
import java.util.regex.Pattern
import ru.kfu.itis.issst.uima.phrrecog.cas.Phrase

/**
 * Deprecated. Do not use it!
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
@Deprecated
class StandoffAnnotationsCollectionReader extends JCasCollectionReader_ImplBase {

  // config
  private var inputDir: File = _
  // state
  private var txtFiles: Array[File] = _
  private var currentFileIndex = 0

  override def initialize(ctx: UimaContext) {
    val inputDirPath = ctx.getConfigParameterValue(ParamInputDir).asInstanceOf[String]
    require(inputDirPath != null, "inputDir is not set")
    inputDir = new File(inputDirPath)
    require(inputDir.isDirectory(), "%s is not existing directory".format(inputDir))
    txtFiles = inputDir.listFiles(extensionFileFilter("txt"))
  }

  override def getNext(jCas: JCas) = {
    val txtFile = txtFiles(currentFileIndex)
    currentFileIndex += 1
    val annFile = getAnnFile(txtFile)
    val lineEnding = detectLineEnding(txtFile)

    val txtSrc = Source.fromFile(txtFile, "utf-8")
    val annSrc = Source.fromFile(annFile, "utf-8")
    try {
      fillCas(jCas, txtSrc, annSrc, lineEnding)
    } finally {
      txtSrc.close()
      annSrc.close()
    }
    val docMeta = new DocumentMetadata(jCas)
    docMeta.setSourceUri(txtFile.getAbsoluteFile().toURI().toString())
    docMeta.addToIndexes()
  }

  override def hasNext(): Boolean = currentFileIndex < txtFiles.length

  override def getProgress(): Array[Progress] = Array()

  private def fillCas(jCas: JCas, txtSrc: Source, annSrc: Source, lineEnd: String) {
    val sb = StringBuilder.newBuilder
    val txtLines = txtSrc.getLines()
    val annLines = annSrc.getLines()
    val newAnnotations = ListBuffer.empty[Annotation]

    var curLine = 0
    while (txtLines.hasNext) {
      val txtLine = txtLines.next()
      val txtLineOffset = sb.length
      curLine += 1
      if (!annLines.hasNext)
        throw new IllegalStateException("Unexpected end of .ann file on line %s".format(curLine))
      val annLine = annLines.next()

      newAnnotations ++= parseAnnoLine(jCas, txtLine, annLine, txtLineOffset)

      sb.append(txtLine)
      if (txtLines.hasNext)
        sb.append(lineEnd)
    }

    jCas.setDocumentText(sb.toString)
    // add annotations to index
    newAnnotations.foreach(_.addToIndexes())
  }

  private def parseAnnoLine(jCas: JCas, txtLine: String, annLine: String, txtLineOffset: Int): Traversable[Annotation] = {
    val phraseStrings = annLine.split("\\|")
    val resultList = ListBuffer.empty[Annotation]
    for (phrStr <- phraseStrings; words = tokenize(phrStr); if !words.isEmpty) {
      val (headWordStr, headWordNum) = parseWordString(words.head)
      val dependentWordStrings = words.tail

      val (headWordBegin, headWordEnd) = getOffsets(txtLine, headWordStr, headWordNum)
      val headWord = new Word(jCas)
      headWord.setBegin(txtLineOffset + headWordBegin)
      headWord.setEnd(txtLineOffset + headWordEnd)

      val dependentsFsArray = new FSArray(jCas, dependentWordStrings.size)
      var fsArrayIndex = 0
      val dwList = for (rawDwStr <- dependentWordStrings) yield {
        val (dwStr, dwStrNum) = parseWordString(rawDwStr)
        val (dwBegin, dwEnd) = getOffsets(txtLine, dwStr, dwStrNum)
        val dw = new Word(jCas)
        dw.setBegin(txtLineOffset + dwBegin)
        dw.setEnd(txtLineOffset + dwEnd)
        dependentsFsArray.set(fsArrayIndex, dw)
        fsArrayIndex += 1
        dw
      }

      val phrase = new Phrase(jCas)
      phrase.setBegin(headWord.getBegin())
      phrase.setEnd(headWord.getEnd())
      phrase.setHead(headWord)
      phrase.setDependents(dependentsFsArray)

      resultList += headWord
      resultList ++= dwList
      resultList += phrase
    }
    resultList
  }

  private def parseWordString(str: String): (String, Option[Int]) = {
    val matcher = numberedWordPattern.matcher(str)
    if (!matcher.matches()) (str, None)
    else (matcher.group(2), Option(matcher.group(1).toInt))
  }

  private def getOffsets(txt: String, word: String, numberOpt: Option[Int]): (Int, Int) = {
    // define recursive function
    def getOffsets(from: Int, number: Int): (Int, Int) = {
      val begin = txt.indexOf(word, from)
      if (begin < 0)
        throw new IllegalStateException("Cant find word #%s %s in line:\n%s".format(number, word, txt))
      if (number == 1) (begin, begin + word.length())
      else getOffsets(begin + 1, number - 1)
    }
    val number = if (numberOpt.isDefined) numberOpt.get else 1
    val (begin, end) = getOffsets(0, number)
    if (!numberOpt.isDefined && txt.indexOf(word, begin + 1) > 0)
      throw new IllegalStateException("Ambiguous word reference %s in line:\n%s".format(word, txt))
    (begin, end)
  }

  private def tokenize(str: String): List[String] =
    str.split("\\s+").toList.dropWhile(_.isEmpty)

  // FIXME
  private def detectLineEnding(file: File): String = {
    val src = Source.fromFile(file, "utf-8")
    val dropped = src.dropWhile(ch => ch != '\r' && ch != '\n')
    if (dropped.hasNext)
      dropped.next() match {
        case '\n' => "\n"
        case '\r' =>
          if (dropped.hasNext && dropped.next() == '\n') "\r\n"
          else "\r"
      }
    else {
      // default
      System.getProperty("line.separator")
    }

  }

  private def extensionFileFilter(extensionStr: String): FilenameFilter =
    FileFilterUtils.suffixFileFilter("." + extensionStr)

  private def getAnnFile(txtFile: File): File = {
    val baseName = FilenameUtils.getBaseName(txtFile.getName())
    val annFile = new File(inputDir, baseName + ".ann")
    if (!annFile.isFile())
      throw new IllegalStateException("No ann file for %s".format(txtFile))
    annFile
  }
}

object StandoffAnnotationsCollectionReader {
  val ParamInputDir = "inputDir"
  private val numberedWordPattern = Pattern.compile("(\\d+):(.+)")
}