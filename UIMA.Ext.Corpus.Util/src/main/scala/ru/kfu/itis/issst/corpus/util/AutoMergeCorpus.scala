package ru.kfu.itis.issst.corpus.util

import scopt.OptionParser
import AutoMergeCorpus._
import java.io.File
import ru.kfu.itis.issst.corpus.statistics.dao.corpus.CorpusDAO
import ru.kfu.itis.issst.corpus.statistics.dao.corpus.XmiFileTreeCorpusDAO
import java.net.URI
import scala.collection.JavaConversions._
import org.apache.uima.resource.metadata.TypeSystemDescription
import org.apache.uima.util.CasCreationUtils
import org.apache.uima.cas.CAS
import org.apache.uima.cas.Type
import ru.kfu.itis.cll.uima.util.AnnotatorUtils
import org.apache.uima.cas.TypeSystem
import org.apache.uima.cas.FSIterator
import org.apache.uima.cas.text.AnnotationFS
import scala.collection.mutable.ListBuffer
import com.typesafe.scalalogging.StrictLogging
import scala.collection.{ mutable => mu }
import ru.kfu.itis.cll.uima.cas.AnnotationUtils

private class AutoMergeCorpus(srcCorpus: CorpusDAO, outputCorpus: CorpusDAO,
  typeSystemDesc: TypeSystemDescription,
  mergerId: String,
  mergeCfg: MergeConfig) extends StrictLogging {

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
      logger.info(s"About to merge $docUri from $annotatorId")
      merge(cas)
      outputCorpus.persist(docUri, mergerId, outCas)
    } else {
      outputCorpus.persist(docUri, mergerId, cas)
      logger.info(s"$docUri is initialized using data from $annotatorId")
    }
  }

  private def merge(otherCas: CAS) {
    for (annoTypeName <- mergeCfg.annotationTypes) {
      val annoType = getTypeSystem.getType(annoTypeName)
      AnnotatorUtils.annotationTypeExist(annoTypeName, annoType)
      merge(otherCas, annoType)
      logger.debug(s"Finished merging for type $annoTypeName")
    }
  }

  private def merge(otherCas: CAS, annoType: Type) {
    val outIdx = outCas.getAnnotationIndex(annoType)
    // we should memorize outIdx annotations now because this idx can change here
    val outAll = List.empty ++ outIdx
    val outTouched = mu.Set.empty[AnnotationFS]
    //
    val overlapIdx = AnnotationUtils.createOverlapIndex(outIdx.iterator());

    val inputIdx = otherCas.getAnnotationIndex(annoType).iterator()

    for (candAnno <- inputIdx) {
      val overlaps = List.empty ++ overlapIdx.getOverlapping(candAnno.getBegin, candAnno.getEnd)
      overlaps match {
        case List() => {
          add(candAnno)
          logger.info("{} is unconfirmed. Added.", formatAnno(candAnno))
        }
        case List(single) if sameBoundaries(candAnno, single) => {
          // do nothing
          logger.debug("{} is confirmed", formatAnno(candAnno))
        }
        case _ => {
          // search for exact boundaries
          if (overlaps.exists(sameBoundaries(candAnno, _))) {
            // do nothing
            logger.debug("{} is confirmed", formatAnno(candAnno))
          } else {
            // conflict
            add(candAnno)
            logger.warn("Conflict: {}\nIncoming: {}",
              overlaps.map(formatAnno), formatAnno(candAnno))
          }
        }
      }
      outTouched ++= overlaps
    }
    // report about untouched out annotations as unconfirmed
    for (
      uncAnno <- outAll if !outTouched.contains(uncAnno)
    ) {
      logger.info("{} is unconfirmed", formatAnno(uncAnno))
    }
  }

  private def formatAnno(anno: AnnotationFS) =
    s"${anno.getType.getShortName}[${anno.getBegin},${anno.getCoveredText}]"

  private def sameBoundaries(first: AnnotationFS, second: AnnotationFS): Boolean =
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
      opt[File]('c', "merge-config") required () valueName ("<file>") action (
        (arg, cfg) => cfg.copy(mergeCfgFile = arg))
    }
    cmdParser.parse(args, Config()) match {
      case Some(cfg) =>
        val srcCorpus = toCorpus(cfg.srcCorpusBaseDir)
        val outCorpus = toCorpus(cfg.outputCorpusBaseDir)
        val tsDesc = getTypeSystemDesc(cfg.srcCorpusBaseDir)
        val mergeCfg = MergeConfig.read(cfg.mergeCfgFile)
        new AutoMergeCorpus(srcCorpus, outCorpus, tsDesc, cfg.mergerId, mergeCfg).run()
      case None => sys.exit(1)
    }
  }

  private def toCorpus(baseDir: File): CorpusDAO = new XmiFileTreeCorpusDAO(baseDir.getPath)

  private def getTypeSystemDesc(corpusDir: File) = XmiFileTreeCorpusDAO.getTypeSystem(corpusDir.getPath)
}
 