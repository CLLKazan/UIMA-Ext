package ru.kfu.itis.issst.uima.phrrecog.util

import scala.util.parsing.combinator.RegexParsers
import ru.kfu.itis.issst.uima.phrrecog.cas.Phrase
import org.opencorpora.cas.Word
import org.apache.uima.cas.text.AnnotationFS
import org.apache.uima.jcas.JCas
import scala.collection.mutable
import scala.collection.mutable.Queue
import scala.collection.mutable.ListBuffer
import scala.collection.Map
import scala.collection.Seq
import org.opencorpora.cas.Wordform
import ru.kfu.itis.cll.uima.cas.FSUtils

private[util] trait PhraseStringParsers extends RegexParsers {

  protected val jCas: JCas
  protected val tokens: Array[AnnotationFS]

  def parse(str: String): Phrase =
    parseAll(phraseString, str) match {
      case Success(phrase, _) => phrase
      case NoSuccess(msg, _) => throw new IllegalStateException(
        "Parse error '%s' in str:\n%s".format(msg, str))
    }

  private def phraseString: Parser[Phrase] = rep1(phraseElem) ^^ {
    case elemsList => {
      val prefixedWfsMap = mutable.Map.empty[String, ListBuffer[Wordform]]
      val depPhrases = ListBuffer.empty[Phrase]
      for (elem <- elemsList)
        elem match {
          case subPhrase: Phrase => depPhrases += subPhrase
          case (prefixOpt: Option[String], wf: Wordform) => prefixedWfsMap.get(prefixOpt.getOrElse(null)) match {
            case None => prefixedWfsMap(prefixOpt.getOrElse(null)) = ListBuffer.empty += wf
            case Some(q) => q += wf
          }
        }
      createAnnotation(prefixedWfsMap, depPhrases)
    }
  }

  protected def createAnnotation(
    prefixedWordformsMap: Map[String, Seq[Wordform]],
    depPhrases: Seq[Phrase]): Phrase

  private def phraseElem = "{" ~> phraseString <~ "}" | prefixedWord

  private def prefixedWord: Parser[(Option[String], Wordform)] = opt("""[\p{Alnum}_]+""".r <~ "=") ~ wordOccurrence ^^ {
    case prefixOpt ~ wf => (prefixOpt, wf)
  }

  private def wordOccurrence: Parser[Wordform] = opt("""\d+""".r <~ ":") ~ """[^\s{}]+""".r ^^ {
    case Some(occNumStr) ~ wordStr => getWordformAnno(wordStr, Some(occNumStr.toInt))
    case None ~ wordStr => getWordformAnno(wordStr, None)
  }

  private def getWordformAnno(wordStr: String, occNum: Option[Int]): Wordform = {
    val (wordBegin, wordEnd) = getOffsets(tokens, wordStr, occNum)
    val word = new Word(jCas)
    word.setBegin(wordBegin)
    word.setEnd(wordEnd)

    val wf = new Wordform(jCas)
    wf.setWord(word)
    word.setWordforms(FSUtils.toFSArray(jCas, wf))

    wf
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
}