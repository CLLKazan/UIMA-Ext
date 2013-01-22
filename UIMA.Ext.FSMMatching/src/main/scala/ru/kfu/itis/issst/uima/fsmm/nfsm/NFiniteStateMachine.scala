/**
 *
 */
package ru.kfu.itis.issst.uima.fsmm.nfsm

/**
 * Represents a non-deterministic finite state machine
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class NFiniteStateMachine {

  private[nfsm] class State {
    private var transitions = List.empty[Transition]
  }

  private[nfsm] class Transition(from: State, to: State)

}