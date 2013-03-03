package ru.kfu.itis.issst.uima.phrrecog.util

import ru.kfu.itis.issst.uima.phrrecog._
import scala.collection.Map
import org.apache.uima.jcas.JCas
import org.opencorpora.cas.Word
import NPAnnotationStringParser._
import scala.collection.immutable.TreeSet
import org.apache.uima.jcas.cas.FSArray
import ru.kfu.itis.issst.uima.phrrecog.cas.NounPhrase
import grizzled.slf4j.Logging

class NPAnnotationStringParser extends AnnotationStringParser with Logging {

  def parseTokens(jCas: JCas, prefixedTokensMap: Map[String, Seq[Word]]) {
    val unprefixedWords = prefixedTokensMap.get(null) match {
      case Some(list) => list
      case None => throw new IllegalStateException(
        "No head in %s".format(prefixedTokensMap))
    }
    val prepWordOpt = prefixedTokensMap.get(PrefixPreposition) match {
      case None => None
      case Some(Seq(prepWord)) => Some(prepWord)
      case Some(list) => {
        val sortedList = TreeSet.empty[Word](annOffsetComp) ++ list
        val prepWord = new Word(jCas)
        prepWord.setBegin(sortedList.firstKey.getBegin)
        prepWord.setEnd(sortedList.lastKey.getEnd)
        info("Compound preposition detected: %s".format(prepWord.getCoveredText))
        Some(prepWord)
      }
    }
    val particleWordOpt = prefixedTokensMap.get(PrefixParticle) match {
      case None => None
      case Some(Seq(particleWord)) => Some(particleWord)
      case Some(list) => throw new IllegalStateException(
        "Multiple words with particle prefix: %s".format(prefixedTokensMap))
    }
    if (prefixedTokensMap.size > 3) throw new IllegalStateException(
      "Unknown prefixes in %s".format(prefixedTokensMap))

    val headWord = unprefixedWords.head
    val dependentWordAnnos = TreeSet.empty[Word](annOffsetComp) ++ unprefixedWords.tail

    val dependentsFsArray = new FSArray(jCas, dependentWordAnnos.size)
    var fsArrayIndex = 0
    for (dwAnno <- dependentWordAnnos) {
      dependentsFsArray.set(fsArrayIndex, dwAnno)
      fsArrayIndex += 1
    }

    val phrase = new NounPhrase(jCas)
    phrase.setBegin(headWord.getBegin())
    phrase.setEnd(headWord.getEnd())
    if (prepWordOpt.isDefined) phrase.setPreposition(prepWordOpt.get)
    if (particleWordOpt.isDefined) phrase.setParticle(particleWordOpt.get)
    phrase.setHead(headWord)
    phrase.setDependents(dependentsFsArray)

    phrase.addToIndexes()
  }
}

object NPAnnotationStringParser {
  val PrefixPreposition = "prep"
  val PrefixParticle = "prcl"
}