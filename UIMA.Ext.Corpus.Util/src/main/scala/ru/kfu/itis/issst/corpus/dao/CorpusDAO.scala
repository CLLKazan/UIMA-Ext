/**
 *
 */
package ru.kfu.itis.issst.corpus.dao

import java.net.URI
import org.apache.uima.cas.CAS

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
trait CorpusDAO extends ru.kfu.itis.issst.corpus.statistics.dao.corpus.CorpusDAO {

  def persist(docUri: URI, annotatorId: String, cas: CAS)

}