package ru.kfu.itis.issst.uima.shaltef.util

import org.apache.uima.cas.TypeSystem
import org.apache.uima.util.CasCreationUtils
import java.io.BufferedOutputStream
import org.apache.uima.cas.impl.XmiCasSerializer
import java.io.FileOutputStream
import scala.collection.mutable
import org.opencorpora.cas.Wordform
import ru.kfu.cll.uima.tokenizer.fstype.W
import org.opencorpora.cas.Word
import ru.kfu.itis.cll.uima.cas.FSUtils
import ru.kfu.itis.issst.uima.phrrecog.cas.NounPhrase
import ru.kfu.itis.issst.uima.phrrecog.cas.Phrase
import scala.collection.JavaConversions.{ asJavaCollection, iterableAsScalaIterable }
import org.uimafit.factory.TypeSystemDescriptionFactory._
import org.uimafit.util.FSCollectionFactory._
import org.apache.uima.jcas.cas.FSArray
import ru.kfu.cll.uima.segmentation.fstype.Sentence
import ru.kfu.itis.issst.uima.phrrecog.fsArrayToTraversable
import org.apache.uima.jcas.cas.StringArray
import org.apache.uima.jcas.JCas
import NprCasBuilder._

class NprCasBuilder(val text: String, additionalTypeSystemNames: List[String]) {

  val ts = {
    val tsNames = "ru.kfu.itis.issst.uima.phrrecog.ts-phrase-recognizer" ::
      "ru.kfu.cll.uima.tokenizer.tokenizer-TypeSystem" ::
      "ru.kfu.cll.uima.segmentation.segmentation-TypeSystem" ::
      additionalTypeSystemNames
    val tsDesc = createTypeSystemDescription(tsNames: _*)
    val dumbCas = CasCreationUtils.createCas(tsDesc, null, null)
    dumbCas.getTypeSystem()
  }
  val cas = CasCreationUtils.createCas(ts, null, null, null)
  cas.setDocumentText(text)
  val jCas = cas.getJCas()

  private val wfMap = mutable.Map.empty[String, Wordform]

  def w(begin: Int, end: Int): Wordform = w(text.substring(begin, end), begin, end)

  def w(id: String, begin: Int, end: Int): Wordform = {
    require(!wfMap.contains(id), "Duplicate id: %s".format(id))
    val token = new W(jCas)
    token.setBegin(begin)
    token.setEnd(end)
    token.addToIndexes()
    val word = new Word(jCas)
    word.setBegin(begin)
    word.setEnd(end)
    word.setToken(token)

    val wf = new Wordform(jCas)
    wf.setWord(word)
    word.setWordforms(FSUtils.toFSArray(jCas, wf))

    word.addToIndexes()

    wfMap(id) = wf
    wf
  }

  def w(id: String): Wordform =
    if (id == null) null
    else wfMap(id)

  def np(headId: String, prepId: String = null, particleId: String = null,
    depWordIds: Iterable[String] = Nil, depNPs: Iterable[Phrase] = Nil,
    index: Boolean = false): NounPhrase = {
    val npAnno = new NounPhrase(jCas)
    val head = w(headId)
    npAnno.setBegin(head.getWord.getBegin)
    npAnno.setEnd(head.getWord.getEnd)
    npAnno.setHead(head)
    npAnno.setParticle(w(particleId))
    npAnno.setPreposition(w(prepId))
    if (!depWordIds.isEmpty)
      npAnno.setDependentWords(createFSArray(jCas, depWordIds.map(w(_))).asInstanceOf[FSArray])
    if (!depNPs.isEmpty)
      npAnno.setDependentPhrases(createFSArray(jCas, depNPs).asInstanceOf[FSArray])
    if (index)
      npAnno.addToIndexes()
    npAnno
  }

  def sent(begin: Int, end: Int): Sentence = {
    val sentAnno = new Sentence(jCas)
    sentAnno.setBegin(begin)
    sentAnno.setEnd(end)
    sentAnno.addToIndexes()
    sentAnno
  }

  def serialize(outPath: String) {
    val os = new BufferedOutputStream(new FileOutputStream(outPath))
    try {
      XmiCasSerializer.serialize(cas, null, os, true, null)
    } finally {
      os.close()
    }
  }

  implicit def wf2GrammemeBuilder(wf: Wordform): GrammemeBuilder = new GrammemeBuilder(wf)

  class GrammemeBuilder(wf: Wordform) {
    def addGrammems(grs: String*): Wordform =
      if (grs.isEmpty) wf
      else {
        wf.getWord.removeFromIndexes()
        val oldGrs = FSUtils.toSet(wf.getGrammems)
        wf.setGrammems(createStringArray(oldGrs ++ grs))
        wf.getWord.addToIndexes()
        wf
      }
  }

  private def createStringArray(strs: Iterable[String]): StringArray =
    NprCasBuilder.createStringArray(jCas, strs)
}

object NprCasBuilder {
  // TODO move to util package
  def createStringArray(jCas: JCas, strs: Iterable[String]): StringArray = {
    val result = new StringArray(jCas, strs.size)
    val strIter = strs.iterator
    for (i <- 0 until strs.size)
      result.set(i, strIter.next())
    result
  }
}