/**
 *
 */
package ru.kfu.itis.issst.uima.fsmm.nfsm
import ru.kfu.itis.issst.uima.fsmm.pattern._

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class PatternToNFSM[A] {

  def fromPattern(pattern: PatternTerm, action: Action[A]): NFiniteStateMachine[A] = {
    val nfsm = recFromPattern(new NFiniteStateMachine[A], pattern, action)
    nfsm.finishBuild()
    nfsm
  }

  private def recFromPattern(nfsm: NFiniteStateMachine[A], pattern: PatternTerm, action: Action[A]): NFiniteStateMachine[A] = {
    import nfsm.{ State, FinalState, LabelBorder }
    pattern match {
      case null => null
      case AtomicRestriction(attrEx, matcher, value, label) => {
        val initialState = new State(true)
        val finalState = new FinalState(action)
        initialState.addTransition(finalState,
          new AnnotationMatcher(attrEx.asInstanceOf[AttributeExtractor[A]], matcher, value))
        if (label != null) {
          initialState.addLabelBorder(LabelBorder(label, true))
          finalState.addLabelBorder(LabelBorder(label, false))
        }
        nfsm.setInitialState(initialState)
        nfsm
      }
      case DisjunctionTerm(left, right, label) => {
        val leftNfsm = recFromPattern(left, null)
        val rightNfsm = recFromPattern(right, null)
        new SimpleNdfsmBuilder[A].addInitialState("initial").addFinalState("final")
      }
    }
  }

}