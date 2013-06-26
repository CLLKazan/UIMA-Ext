/**
 *
 */
package ru.kfu.itis.issst.uima.morph.bsc

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
trait TokenSequenceDAO {
  def getSequences():Iterable[TokenSequence]
}