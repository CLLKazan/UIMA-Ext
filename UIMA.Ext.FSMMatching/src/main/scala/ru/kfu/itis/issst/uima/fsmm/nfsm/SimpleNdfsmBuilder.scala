/**
 *
 */
package ru.kfu.itis.issst.uima.fsmm.nfsm

import scala.collection.{ mutable => cm }

/**
 * @tparam A type of input annotations
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class SimpleNdfsmBuilder[A] {

  private val fsm = new NFiniteStateMachine[A]
  import fsm.State
  import fsm.FinalState

  // state label => state object 
  private val states = cm.Map.empty[String, State]
  private var initialState: State = _

  def addInitialState(stateLabel: String): this.type =
    doAddState(stateLabel, () =>
      if (initialState != null) throw new IllegalStateException("There is other initial state")
      else {
        initialState = new State(true)
        initialState
      })

  def addFinalState(stateLabel: String): this.type =
    doAddState(stateLabel, () => new FinalState(null))

  def addState(stateLabel: String): this.type =
    doAddState(stateLabel, () => new State(false))

  def doAddState(stateLabel: String, stateCreator: () => State): this.type =
    if (states.contains(stateLabel))
      throw new IllegalArgumentException("Duplicate state addition: %s".format(stateLabel))
    else {
      states(stateLabel) = stateCreator()
      this
    }

  def addTransition(from: String, to: String, im: InputMatcher[A]): this.type = {
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

  def build(initialStateLabel: String): NFiniteStateMachine[A] = {
    // finish all states
    states.values.foreach(_.finishBuild())
    val initialState = getState(initialStateLabel)
    fsm.setInitialState(initialState)
    // clean the state fields to allow GC do its work (?)
    states.clear()
    fsm
  }

  private def getState(stateLabel: String): State =
    states.get(stateLabel) match {
      case None => unknownState(stateLabel)
      case Some(result) => result
    }

  private def unknownState(stateLabel: String): Nothing =
    throw new IllegalArgumentException("Unknown state: %s".format(stateLabel))
}