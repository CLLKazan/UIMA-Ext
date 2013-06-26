/**
 *
 */
package ru.kfu.itis.issst.uima.morph.bsc

import grizzled.slf4j.Logging
import java.lang.System.currentTimeMillis
import scala.collection.{ mutable => muta }
import scala.collection.immutable.TreeSet
import Learning._
import scala.collection.mutable.ArrayBuffer

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class Learning(iterationsNum: Int, tsDao: TokenSequenceDAO) extends Logging {
  private var actionEvaluator: ActionEvaluator = null

  def run() {
    initializeActionEvaluator();
    for (i <- 1.to(iterationsNum)) {
      val timeBefore = currentTimeMillis()
      doIteration()
      info("Finished %s-th iteration in %s sec".format(i,
        (currentTimeMillis() - timeBefore) / 1000))
    }
  }

  private def doIteration() {
    for (ts <- tsDao.getSequences())
      learn(ts)
  }

  private def learn(ts: TokenSequence) {
    var acceptedSpans = new TreeSet[Span]()(spanOrdering)
    val candidateSpans = initCandidateSpans(ts)
    def learningStep(): Unit =
      if (!candidateSpans.isEmpty) {
        val bestCandidate = candidateSpans.maxBy()
      }
    learningStep
  }

  private def initCandidateSpans(ts: TokenSequence): muta.Set[CandidateSpan] = {
    val result = muta.Set.empty[CandidateSpan]
    // TODO
    result
  }

  private def initializeActionEvaluator() {
    // TODO read features from configuration
  }

  private class Span(val first: Int, val last: Int) {

    val hyposByState = Map.empty[State, Set[Hypothese]]

    override def toString = {
      val sb = new StringBuilder
      sb.append('(').append(first).append(',').append(last).append(",h=")
      sb.append(hyposByState).append(')').toString
    }
  }

  private class CandidateSpan(leftAccepted: Option[Span], rightAccepted: Option[Span], val extendedBy: Int) {
    assert {
      (!leftAccepted.isDefined || leftAccepted.get.last == extendedBy - 1) &&
        (!rightAccepted.isDefined || rightAccepted.get.first == extendedBy + 1)
    }
    def searchOptimalHypos(acceptedSpans: TreeSet[Span]) {
      val leftContexts = leftAccepted match {
        case Some(ls) =>
          for (state <- ls.hyposByState.keys)
            yield state.applyRight _
        case None => List(noOp _)
      }
      val rightContexts = rightAccepted match {
        case None => List(noOp _)
        case Some(rs) =>
          for (state <- rs.hyposByState.keys)
            yield state.applyLeft _
      }
    }
  }

  private def noOp(tags: ArrayBuffer[Tag]) {}

  private class State(first: Int, last: Int, left: Seq[Tag], right: Seq[Tag]) {
    def applyLeft(target: ArrayBuffer[Tag]) {
      var i = first
      for (t <- left) {
        target(i) = t
        i += 1
      }
    }

    def applyRight(target: ArrayBuffer[Tag]) {
      var i = last - right.length + 1
      for (t <- right) {
        target(i) = t
        i += 1
      }
    }
  }

  private class Hypothese(
    private val tokens: IndexedSeq[String],
    private val tags: IndexedSeq[Tag],
    val lastActionTarget: Int) {
    def getActionScore: Double = actionEvaluator.evaluateAction(tokens, tags, lastActionTarget)
  }
}

object Learning {
  private val spanOrdering = new Ordering[Span] {
    override def compare(x: Span, y: Span): Int = {
      if (x.first > y.last) 1
      else if (x.last < y.first) -1
      else throw new IllegalStateException("Intersecting spans:\n%s\n%s".format(x, y))
    }
  }
}