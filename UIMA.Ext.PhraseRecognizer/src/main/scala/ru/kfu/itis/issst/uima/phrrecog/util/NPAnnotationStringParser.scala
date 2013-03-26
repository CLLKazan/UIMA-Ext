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
import org.apache.uima.cas.text.AnnotationFS
import ru.kfu.itis.issst.uima.phrrecog.cas.Phrase
import org.uimafit.util.FSCollectionFactory
import scala.collection.JavaConversions.asJavaIterable
import org.opencorpora.cas.Wordform
import ru.kfu.itis.cll.uima.cas.FSUtils

class NPAnnotationStringParserFactory extends PhraseStringParsersFactory {
  override def createParser(jCas: JCas, tokens: Array[AnnotationFS]): NPAnnotationStringParser =
    new NPAnnotationStringParser(jCas, tokens)
}

class NPAnnotationStringParser(protected val jCas: JCas, protected val tokens: Array[AnnotationFS])
  extends PhraseStringParsers with Logging {

  override protected def createAnnotation(prefixedWordformsMap: Map[String, Seq[Wordform]], depPhrases: Seq[Phrase]): NounPhrase = {
    val unprefixedWfs = prefixedWordformsMap.get(null) match {
      case Some(list) => list
      case None => throw new IllegalStateException(
        "No head in %s".format(prefixedWordformsMap))
    }
    val prepWfOpt = prefixedWordformsMap.get(PrefixPreposition) match {
      case None => None
      case Some(Seq(prepWf)) => Some(prepWf)
      case Some(list) => {
        val sortedList = TreeSet.empty[Wordform](wfOffsetComp) ++ list
        val prepWord = new Word(jCas)
        prepWord.setBegin(sortedList.firstKey.getWord.getBegin)
        prepWord.setEnd(sortedList.lastKey.getWord.getEnd)
        info("Compound preposition detected: %s".format(prepWord.getCoveredText))

        val prepWf = new Wordform(jCas)
        prepWf.setWord(prepWord)
        prepWord.setWordforms(FSUtils.toFSArray(jCas, prepWf))
        Some(prepWf)
      }
    }
    val particleWfOpt = prefixedWordformsMap.get(PrefixParticle) match {
      case None => None
      case Some(Seq(particleWf)) => Some(particleWf)
      case Some(list) => throw new IllegalStateException(
        "Multiple words with particle prefix: %s".format(prefixedWordformsMap))
    }
    if (prefixedWordformsMap.size > 3) throw new IllegalStateException(
      "Unknown prefixes in %s".format(prefixedWordformsMap))

    val headWf = unprefixedWfs.head

    val dependentWfAnnos = TreeSet.empty[Wordform](wfOffsetComp) ++ unprefixedWfs.tail
    val depWordformsFsArray = new FSArray(jCas, dependentWfAnnos.size)
    FSCollectionFactory.fillArrayFS(depWordformsFsArray, dependentWfAnnos)

    val depPhrasesFsArray = new FSArray(jCas, depPhrases.size)
    FSCollectionFactory.fillArrayFS(depPhrasesFsArray, depPhrases)

    val phrase = new NounPhrase(jCas)
    phrase.setBegin(headWf.getWord.getBegin)
    phrase.setEnd(headWf.getWord.getEnd)
    if (prepWfOpt.isDefined) phrase.setPreposition(prepWfOpt.get)
    if (particleWfOpt.isDefined) phrase.setParticle(particleWfOpt.get)
    phrase.setHead(headWf)
    phrase.setDependentWords(depWordformsFsArray)
    phrase.setDependentPhrases(depPhrasesFsArray)
    phrase
  }
}

object NPAnnotationStringParser {
  val PrefixPreposition = "prep"
  val PrefixParticle = "prcl"
}