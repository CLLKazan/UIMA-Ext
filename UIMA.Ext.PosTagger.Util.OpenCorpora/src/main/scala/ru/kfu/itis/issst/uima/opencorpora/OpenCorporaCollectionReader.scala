package ru.kfu.itis.issst.uima.opencorpora

import java.io.{InputStream, File}

import org.apache.commons.io.{IOUtils, FileUtils}
import org.apache.uima.UimaContext
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase
import org.apache.uima.fit.descriptor.ConfigurationParameter
import org.apache.uima.jcas.JCas
import org.apache.uima.util.{ProgressImpl, Progress}
import ru.kfu.cll.uima.segmentation.fstype.Sentence

import scala.xml.{NodeSeq, Node, XML}

/**
 * @author Rinat Gareev
 */
class OpenCorporaCollectionReader extends JCasCollectionReader_ImplBase {

  @ConfigurationParameter
  var srcXmlFile: File
  // state fields
  var srcXmlStream: InputStream
  var docs: NodeSeq
  var docsIter: Iterator[Node]
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
    val pars = (doc \ "paragraphs").head
    val txtBuilder = new StringBuilder
    for(par @ <paragraph>{_*}</paragraph> <- pars.child) {
      for(sent @ <sentence>{_*}</sentence> <- par.child) {
        val sentTxt = (sent \ "source").text
        val sentBegin = txtBuilder.length
        txtBuilder ++= sentTxt
        val sentEnd = txtBuilder.length
        for(tok @ <token>{_*}</token> <- (sent \ "tokens").head.child)
        makeSentence(jCas, sentBegin, sentEnd)
        txtBuilder += ' '
      }
      txtBuilder += '\n'
    }

  }

  private def makeSentence(jCas:JCas, begin:Int, end:Int): Unit = {
    new Sentence(jCas, begin, end).addToIndexes()
  }

  override def getProgress: Array[Progress] = Array(new ProgressImpl(readCounter, -1, Progress.ENTITIES))
}
