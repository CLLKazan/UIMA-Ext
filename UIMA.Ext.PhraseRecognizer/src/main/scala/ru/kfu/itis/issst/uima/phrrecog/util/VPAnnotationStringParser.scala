package ru.kfu.itis.issst.uima.phrrecog.util

import org.opencorpora.cas.Word
import org.apache.uima.jcas.JCas
import org.apache.uima.jcas.cas.FSArray
import ru.kfu.itis.issst.uima.phrrecog.cas.VerbPhrase
import scala.collection.Map
import ru.kfu.itis.issst.uima.phrrecog.cas.Phrase
import org.apache.uima.cas.text.AnnotationFS

class VPAnnotationStringParserFactory extends PhraseStringParsersFactory {
  override def createParser(jCas: JCas, tokens: Array[AnnotationFS]) =
    new VPAnnotationStringParser(jCas, tokens)
}

class VPAnnotationStringParser(protected val jCas: JCas, protected val tokens: Array[AnnotationFS]) extends PhraseStringParsers {

  protected override def createAnnotation(prefixedTokensMap: Map[String, Seq[Word]], depPhrases: Seq[Phrase]): VerbPhrase = {
    val unprefixedWords = prefixedTokensMap.get(null) match {
      case Some(list) => list
      case None => throw new IllegalStateException(
        "No head in %s".format(prefixedTokensMap))
    }
    if (prefixedTokensMap.size > 1) throw new IllegalStateException(
      "Unknown prefixes in %s".format(prefixedTokensMap))
    val headWord = unprefixedWords.head
    val dependentWordAnnos = unprefixedWords.tail

    val dependentsFsArray = new FSArray(jCas, dependentWordAnnos.size)
    var fsArrayIndex = 0
    for (dwAnno <- dependentWordAnnos) {
      dependentsFsArray.set(fsArrayIndex, dwAnno)
      fsArrayIndex += 1
    }

    val phrase = new VerbPhrase(jCas)
    phrase.setBegin(headWord.getBegin())
    phrase.setEnd(headWord.getEnd())
    phrase.setHead(headWord)
    phrase.setDependentWords(dependentsFsArray)

    phrase
  }

}