package ru.kfu.itis.issst.uima.opencorpora

import java.io.{InputStream, File}

import org.apache.commons.io.{IOUtils, FileUtils}
import org.apache.uima.UimaContext
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase
import org.apache.uima.fit.descriptor.ConfigurationParameter
import org.apache.uima.fit.factory.{ExternalResourceFactory, TypeSystemDescriptionFactory, CollectionReaderFactory, AnnotationFactory}
import org.apache.uima.fit.pipeline.SimplePipeline
import org.apache.uima.jcas.JCas
import org.apache.uima.util.{ProgressImpl, Progress}
import org.opencorpora.cas.{Wordform, Word}
import ru.kfu.cll.uima.segmentation.fstype.Sentence
import ru.kfu.cll.uima.tokenizer.fstype._
import ru.kfu.itis.cll.uima.cas.FSUtils
import ru.kfu.itis.cll.uima.commons.DocumentMetadata
import ru.kfu.itis.cll.uima.consumer.XmiWriter
import ru.kfu.itis.cll.uima.util.DocumentUtils
import ru.kfu.itis.issst.uima.morph.commons.{GramModelBasedTagMapper, TagAssembler}
import ru.kfu.itis.issst.uima.morph.dictionary.MorphDictionaryAPIFactory.getMorphDictionaryAPI
import ru.kfu.itis.issst.uima.opencorpora.OpenCorporaCollectionReader._
import ru.kfu.itis.issst.uima.postagger.{MorphCasUtils, PosTaggerAPI}
import ru.kfu.itis.issst.uima.segmentation.SentenceSplitterAPI
import ru.kfu.itis.issst.uima.tokenizer.TokenizerAPI

import scala.xml.{NodeSeq, Node, XML}
import scala.collection.JavaConversions._

/**
 * @author Rinat Gareev
 */
class OpenCorporaCollectionReader extends JCasCollectionReader_ImplBase {

  @ConfigurationParameter(name = PARAM_SOURCE_CORPUS_XML)
  var srcXmlFile: File = _
  // state fields
  var srcXmlStream: InputStream = _
  var docs: NodeSeq = _
  var docsIter: Iterator[Node] = _
  var readCounter = 0

  override def initialize(ctx: UimaContext): Unit = {
    srcXmlStream = FileUtils.openInputStream(srcXmlFile)
    docs = XML.load(srcXmlStream) \ "text"
    docsIter = docs.iterator
  }

  override def close(): Unit = {
    IOUtils.closeQuietly(srcXmlStream)
  }

  override def hasNext: Boolean = docsIter.hasNext

  override def getNext(jCas: JCas): Unit = {
    val doc = docsIter.next()
    // set meta
    val docId = doc \@ "id"
    val docMetaAnno = new DocumentMetadata(jCas, 0, 0)
    // FIXME hot fix
    docMetaAnno.setSourceUri(s"file:$docId")
    docMetaAnno.addToIndexes()
    //
    val pars = (doc \ "paragraphs").head
    val txtBuilder = new StringBuilder
    for(par @ <paragraph>{_*}</paragraph> <- pars.child) {
      for(sent @ <sentence>{_*}</sentence> <- par.child) {
        val sentTxt = (sent \ "source").text
        val sentBegin = txtBuilder.length
        txtBuilder ++= sentTxt
        val sentEnd = txtBuilder.length
        val tokenElems = for(tok @ <token>{_*}</token> <- (sent \ "tokens").head.child) yield {
          val tokTxt = tok \@ "text"
          val (lemmaOpt, lemmaIdOpt) = extractLemma(tok) match {
            case None => (None, None)
            case Some((lemma, lemmaId)) => (Some(lemma), Some(lemmaId))
          }
          TokenElem(tokTxt, lemmaOpt, lemmaIdOpt, extractGrams(tok))
        }
        try {
          val tokenProtos = parseToken(tokenElems, 0, sentTxt).
            map(pr => pr.copy(begin = pr.begin + sentBegin, end = pr.end + sentEnd))
          makeSentenceAnno(jCas, sentBegin, sentEnd)
          tokenProtos.foreach(makeTokenAnno(jCas, _))
        } catch {
          case ex: Exception =>
            getLogger.error(s"Skipped sentence:\n$sentTxt", ex)
        }
        txtBuilder += ' '
      }
      txtBuilder += '\n'
    }
    jCas.setDocumentText(txtBuilder.toString())
  }

  private def makeTokenAnno(jCas:JCas, tokenProto: TokenProto): Unit = {
    import tokenProto._
    val tokAnno = AnnotationFactory.createAnnotation(jCas, begin, end, tokenType)
    //
    if(PosTaggerAPI.canCarryWord(tokAnno)) {
      val wordAnno = new Word(jCas, begin, end)
      wordAnno.setToken(tokAnno)
      //
      val wf = new Wordform(jCas)
      if (lemma.isDefined) wf.setLemma(lemma.get)
      if (lemmaId.isDefined) wf.setLemmaId(lemmaId.get)
      wf.setWord(wordAnno)
      MorphCasUtils.addGrammemes(jCas, wf,
        removeUnknownGrammems(grams))
      wordAnno.setWordforms(FSUtils.toFSArray(jCas, wf))
      //
      wordAnno.addToIndexes()
    }
  }

  private def removeUnknownGrammems(grams:Set[String]) = grams -- FILTERED_GRAMS

  private def extractLemma(tokElem: Node): Option[(String, Int)] = {
    val lemmaElems = tokElem \\ "l"
    if (lemmaElems.size != 1)
      throw new IllegalStateException(
        s"Too much l:\n$tokElem")
    val l = lemmaElems.head
    val lemma = l \@ "t"
    val lemmaId = (l \@ "id").toInt
    if (lemmaId == 0) None
    else Some((lemma, lemmaId))
  }

  private def extractGrams(tokElem: Node): Set[String] =
    (for (gElem <- tokElem \\ "g") yield gElem \@ "v").toSet


  private def parseToken(tokens: Seq[TokenElem], cursor: Int, sentTxt: String): List[TokenProto] = {
    if(tokens.isEmpty) Nil
    else {
      val token = tokens.head
      val tokTxt = token.txt
      require(!tokTxt.isEmpty)
      sentTxt.indexOf(tokTxt, cursor) match {
        case -1 => throw new IllegalStateException(
          s"Can't find '$token' from position $cursor in sentence:\n$sentTxt")
        case tokRelBegin =>
          val tokRelEnd = tokRelBegin + tokTxt.length
          // return
          TokenProto(getTokenType(token), tokRelBegin, tokRelEnd, token.grams, token.lemma, token.lemmaId) ::
            parseToken(tokens.tail, tokRelEnd, sentTxt)
      }
    }
  }

  private def getTokenType(token: TokenElem): Class[_ <: Token] = {
    import token._
    if (grams.contains("PNCT")) classOf[PM]
    else if (grams.contains("NUMB") ||
      grams.contains("ROMN")) classOf[NUM]
    else if (Character.isUpperCase(txt.head)) classOf[CW]
    else classOf[SW]
  }


  private def makeSentenceAnno(jCas: JCas, begin: Int, end: Int): Unit = {
    new Sentence(jCas, begin, end).addToIndexes()
  }

  override def getProgress: Array[Progress] = Array(new ProgressImpl(readCounter, -1, Progress.ENTITIES))
}

object OpenCorporaCollectionReader {

  def createDescription(srcXmlFile: File) = {
    val tsd = TypeSystemDescriptionFactory.createTypeSystemDescription(
      DocumentUtils.TYPESYSTEM_COMMONS,
      TokenizerAPI.TYPESYSTEM_TOKENIZER,
      SentenceSplitterAPI.TYPESYSTEM_SENTENCES,
      PosTaggerAPI.TYPESYSTEM_POSTAGGER
    )
    CollectionReaderFactory.createReaderDescription(
      classOf[OpenCorporaCollectionReader], tsd,
      PARAM_SOURCE_CORPUS_XML, srcXmlFile.getPath)
  }

  final val PARAM_SOURCE_CORPUS_XML = "sourceCorpusXml"
  // FIXME use an according version of dictionary, remove only "UNKN" and "NUMB"
  val FILTERED_GRAMS = Set("UNKN", "NUMB", "ROMN", "LATN", "Anph", "Adjx", "Impx")
  private val NUM_PATTERN = "\\d+"

  private case class TokenElem(txt: String,
                               lemma: Option[String],
                               lemmaId: Option[Int],
                               grams: Set[String])

  private case class TokenProto(tokenType: Class[_ <: Token],
                                begin: Int,
                                end: Int,
                                grams: Set[String] = Set(),
                                lemma: Option[String] = None,
                                lemmaId: Option[Int] = None)
}

object ReadOpenCorporaToXMI {
  def main(args: Array[String]): Unit = {
    val (srcXmlPath, outputDirPath) = args match {
      case Array(sxp, odp) => (sxp, odp)
      case _ =>
        println("Usage: <sourceCorpusXml> <outpurDir>")
        sys.exit(1)
    }
    val srcXmlFile = new File(srcXmlPath)
    if (!srcXmlFile.isFile) {
      println(s"$srcXmlFile is not an existing file")
      sys.exit(1)
    }
    val readerDesc = createDescription(srcXmlFile)
    val tagAssemblerDesc = TagAssembler.createDescription();
    {
      val gramModelDesc = getMorphDictionaryAPI.getGramModelDescription
      ExternalResourceFactory.bindExternalResource(tagAssemblerDesc,
        GramModelBasedTagMapper.RESOURCE_GRAM_MODEL, gramModelDesc)
    }
    val xmiWriterDesc = XmiWriter.createDescription(new File(outputDirPath), false)
    SimplePipeline.runPipeline(readerDesc, tagAssemblerDesc, xmiWriterDesc)
  }
}