/**
 *
 */
package ru.kfu.itis.issst.uima.fsmm.nfsm

import scala.collection.{ mutable => cm }

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class SimpleNdfsmBuilder {

  // IM <-> InputMatcher
  private type IM = AnnotationMatcher

  // state label => state object 
  private val states = cm.Map.empty[String, State]

  def addState(stateLabel: String): this.type =
    if (states.contains(stateLabel))
      throw new IllegalArgumentException("Duplicate state addition: %s".format(stateLabel))
    else {
      states(stateLabel) = new State
      this
    }

  def addTransition(from: String, to: String, im: IM): this.type = {
    val fromState = getState(from)
    val toState = getState(to)
    fromState.addTransition(toState, im)
    this
  }

  def addEpsilonTransition(from: String, to: String): this.type = {
    val fromState = getState(from)
    val toState = getState(to)
    fromState.addEpsilonTransition(toState)
    this
  }

  def build(initialStateLabel: String): NFiniteStateMachine = {
    // finish all states
    states.values.foreach(_.finishBuild())
    val initialState = getState(initialStateLabel)
    val result = new NFiniteStateMachine(initialState)
    // clean the state fields to allow GC do its work (?)
    states.clear()
    result
  }

  private def getState(stateLabel: String): State =
    states.get(stateLabel) match {
      case None => unknownState(stateLabel)
      case Some(result) => result
    }

  private def unknownState(stateLabel: String): Nothing =
    throw new IllegalArgumentException("Unknown state: %s".format(stateLabel))
}