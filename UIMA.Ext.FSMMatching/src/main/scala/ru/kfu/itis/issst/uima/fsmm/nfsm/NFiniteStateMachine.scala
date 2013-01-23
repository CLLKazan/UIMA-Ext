/**
 *
 */
package ru.kfu.itis.issst.uima.fsmm.nfsm

/**
 * Represents a non-deterministic finite state machine
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class NFiniteStateMachine(val initialState: State) {
  require(initialState.isBuilt, "Can't construct NDFSM from non-finished initial state")
}

/*
 * Immutable State implementation seems to be not feasible as there may be cyclic paths  
 */
private[nfsm] class State {
  private var built = false
  private var transitions = List.empty[Transition]
  private var epsilonTransitions = Set.empty[State]

  private[nfsm] def addTransition(to: State, input: InputMatcher) {
    require(!built, "Modification of a built state is not allowed")
    transitions ::= new Transition(this, to, input)
  }

  private[nfsm] def addEpsilonTransition(to: State) {
    require(!built, "Modification of a built state is not allowed")
    epsilonTransitions += to
  }

  private[nfsm] def finishBuild() { built = true }
  private[nfsm] def isBuilt = built
}

private[nfsm] class Transition(from: State, to: State, input: InputMatcher)