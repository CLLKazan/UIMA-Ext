package ru.kfu.itis.issst.corpus.util

import scopt.OptionParser
import AutoMergeCorpus._
import java.io.File
import ru.kfu.itis.issst.corpus.statistics.dao.corpus.{ CorpusDAO => ROCorpusDAO }
import ru.kfu.itis.issst.corpus.statistics.dao.corpus.XmiFileTreeCorpusDAO
import java.net.URI
import scala.collection.JavaConversions._
import org.apache.uima.resource.metadata.TypeSystemDescription
import org.apache.uima.util.CasCreationUtils
import org.apache.uima.cas.CAS
import ru.kfu.itis.issst.corpus.dao.CorpusDAO
import org.apache.uima.cas.Type
import ru.kfu.itis.cll.uima.util.AnnotatorUtils
import org.apache.uima.cas.TypeSystem
import org.apache.uima.cas.FSIterator
import org.apache.uima.cas.text.AnnotationFS

private class AutoMergeCorpus(srcCorpus: ROCorpusDAO, outputCorpus: CorpusDAO,
                              typeSystemDesc: TypeSystemDescription,
                              mergerId: String,
                              mergeCfg: MergeConfig) {

  private val outCas = CasCreationUtils.createCas(typeSystemDesc, null, null)
  private type AnnoIterator = FSIterator[AnnotationFS]

  def run() {
    val srcCas = CasCreationUtils.createCas(typeSystemDesc, null, null)
    for (
      docUri <- srcCorpus.getDocuments;
      annotatorId <- srcCorpus.getAnnotatorIds(docUri)
    ) try {
      srcCorpus.getDocumentCas(docUri, annotatorId, srcCas)
      addToOutput(docUri, annotatorId, srcCas)
    } finally {
      srcCas.reset()
    }
  }

  private def addToOutput(docUri: URI, annotatorId: String, cas: CAS) {
    if (outputCorpus.hasDocument(docUri, mergerId)) {
      outCas.reset()
      outputCorpus.getDocumentCas(docUri, mergerId, outCas)
      merge(cas)
      outputCorpus.persist(docUri, mergerId, outCas)
    } else {
      outputCorpus.persist(docUri, mergerId, cas)
    }
  }

  private def merge(otherCas: CAS) {
    for (annoTypeName <- mergeCfg.annotationTypes) {
      val annoType = getTypeSystem.getType(annoTypeName)
      AnnotatorUtils.annotationTypeExist(annoTypeName, annoType)
      merge(otherCas, annoType)
    }
  }

  private def merge(otherCas: CAS, annoType: Type) {
    val outIter = outCas.getAnnotationIndex(annoType).iterator()
    outIter.moveToFirst()
    val otherIter = otherCas.getAnnotationIndex(annoType).iterator()
    otherIter.moveToFirst()
    merge(outIter, otherIter)
  }

  private def merge(outIter: AnnoIterator, inIter: AnnoIterator): Unit =
    if (inIter.isValid()) {
      val candAnno = inIter.get()
      //
      outIter.moveTo(candAnno)
      if (!outIter.isValid()) outIter.moveToLast()
      var overlaps: List[AnnotationFS] = Nil
      while (outIter.isValid() && outIter.get.getEnd > candAnno.getBegin) {
        if (outIter.get.getBegin < candAnno.getEnd) // i.e. overlaps
          overlaps ::= outIter.get
        outIter.moveToPrevious()
      }
      overlaps match {
        case List() => add(candAnno) // TODO REPORT
        case List(single) if sameBoundaries(candAnno, single) => // do nothing
        case _ => add(candAnno) // TODO REPORT about this conflict
      }
      // TODO
      ???
      //
      inIter.moveToNext()
      merge(outIter, inIter)
    }
  
  private def sameBoundaries(first:AnnotationFS, second:AnnotationFS):Boolean =
    first.getBegin == second.getBegin && first.getEnd == second.getEnd

  private def add(srcAnno: AnnotationFS) {
    val newAnno = outCas.createAnnotation(srcAnno.getType, srcAnno.getBegin, srcAnno.getEnd)
    outCas.addFsToIndexes(newAnno)
  }

  private def getTypeSystem: TypeSystem = outCas.getTypeSystem
}

object AutoMergeCorpus {
  private case class Config(
    srcCorpusBaseDir: File = null,
    outputCorpusBaseDir: File = null,
    mergerId: String = "curator",
    mergeCfgFile: File = null)

  def main(args: Array[String]) {
    val cmdParser = new OptionParser[Config]("Corpus Auto-merging Tool") {
      opt[File]('s', "source") required () valueName ("<dir>") action (
        (arg, cfg) => cfg.copy(srcCorpusBaseDir = arg))
      opt[File]('o', "output") required () valueName ("<dir>") action (
        (arg, cfg) => cfg.copy(outputCorpusBaseDir = arg))
      opt[String]('m', "merger-id") optional () valueName ("<annotator-id>") action (
        (arg, cfg) => cfg.copy(mergerId = arg))
      opt[File]('c', "merge-config") required () valueName ("<dir>") action (
        (arg, cfg) => cfg.copy(mergeCfgFile = arg))
    }
    cmdParser.parse(args, Config()) match {
      case Some(cfg) =>
        val srcCorpus = toROCorpus(cfg.srcCorpusBaseDir)
        val outCorpus = toCorpus(cfg.outputCorpusBaseDir)
        val tsDesc = getTypeSystemDesc(cfg.srcCorpusBaseDir)
        val mergeCfg = MergeConfig.read(cfg.mergeCfgFile)
        new AutoMergeCorpus(srcCorpus, outCorpus, tsDesc, cfg.mergerId, mergeCfg).run()
      case None => sys.exit(1)
    }
  }

  private def toROCorpus(baseDir: File): ROCorpusDAO = new XmiFileTreeCorpusDAO(baseDir.getPath)

  private def toCorpus(baseDir: File): CorpusDAO = ??? // TODO

  private def getTypeSystemDesc(corpusDir: File) = XmiFileTreeCorpusDAO.getTypeSystem(corpusDir.getPath)
}
 