/**
 *
 */
package ru.kfu.itis.issst.uima.fsmm.nfsm.builder

import ru.kfu.itis.issst.uima.fsmm.nfsm._
import scala.collection.{ mutable => cm }

/**
 * @tparam A type of input annotations
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class SimpleNdfsmBuilder[A] {

  private val fsm = new NFiniteStateMachine[A]
  import fsm.{ State, FinalState, TransitionAcceptor }

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

  /*def addTransition(from: String, to: String,
    am: AnnotationMatcher[A], acceptor: TransitionAcceptor): this.type = {
    val fromState = getState(from)
    val toState = getState(to)
    fromState.addTransition(toState, am, acceptor)
    this
  }*/

  def build(): NFiniteStateMachine[A] = {
    // finish all states
    states.values.foreach(_.finishBuild())
    fsm.setInitialStates(Set(initialState))
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