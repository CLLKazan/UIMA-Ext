/**
 *
 */
package ru.kfu.itis.issst.uima.morph.bsc

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
trait ActionEvaluator {
  def evaluateAction(tokens: IndexedSeq[String], tags: IndexedSeq[Tag], actionTarget: Int): Double
}