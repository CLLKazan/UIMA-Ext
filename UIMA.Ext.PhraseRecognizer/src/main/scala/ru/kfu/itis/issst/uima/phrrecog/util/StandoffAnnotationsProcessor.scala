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
  private var annoStrParserFactory: PhraseStringParsersFactory = _

  override def initialize(ctx: UimaContext) {
    super.initialize(ctx)
    val annoStrParserFactoryClassName = ctx.getConfigParameterValue(ParamAnnotationStringParserFactoryClass).asInstanceOf[String]
    mandatoryParam(ParamAnnotationStringParserFactoryClass, annoStrParserFactoryClassName)
    val annoStrParserClass = Class.forName(annoStrParserFactoryClassName)
    annoStrParserFactory = annoStrParserClass.newInstance().asInstanceOf[PhraseStringParsersFactory]
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
    val annoStrParser = annoStrParserFactory.createParser(jCas, paraTokens)
    for (phrStr <- phraseStrings)
      annoStrParser.parse(phrStr).addToIndexes()
  }

  private def getParagraphTokens(paraAnno: AnnotationFS): Array[AnnotationFS] = {
    val tokensList = CasUtil.selectCovered(paraAnno.getCAS(), tokenType, paraAnno)
    tokensList.toArray(new Array[AnnotationFS](tokensList.size()))
  }

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
  val ParamAnnotationStringParserFactoryClass = "AnnotationStringParserFactoryClass"
}