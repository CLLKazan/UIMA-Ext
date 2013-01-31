/**
 *
 */
package ru.kfu.itis.issst.uima.fsmm.nfsm.builder

import ru.kfu.itis.issst.uima.fsmm.nfsm._
import ru.kfu.itis.issst.uima.fsmm.pattern._
import FSMAdjacencyMatrix.NodeType

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class PatternToNFSM[A] {

  def fromPattern(pattern: PatternTerm, action: Action[A]): NFiniteStateMachine[A] = {
    val adjMatrix = recFromPattern(pattern, action)
    adjMatrix.buildNfsm()
  }

  private def recFromPattern(pattern: PatternTerm, action: Action[A]): FSMAdjacencyMatrix[A] = {
    val adjMatrix = FSMAdjacencyMatrix.create[A]()
    import adjMatrix._
    pattern match {
      case null => null
      case AtomicRestriction(attrEx, matcher, value, label) => {
        val initialState = createInitialNode()
        val finalState = createFinalNode(action)
        val am = new AnnotationMatcher(attrEx.asInstanceOf[AttributeExtractor[A]], matcher, value)
        val trans = addTransition(initialState, finalState, am)
        if (label != null) {
          putLabelBorder(trans, new LabelBorder(label, LabelBorderType.Begin))
          putLabelBorder(trans, new LabelBorder(label, LabelBorderType.End))
        }
      }
      case DisjunctionTerm(left, right, label) => {
        val leftMatrix = recFromPattern(left, null)
        val rightMatrix = recFromPattern(right, null)

        val initialState = createInitialNode()
        val finalState = createFinalNode(action)

        for (subMatrix <- List(leftMatrix, rightMatrix)) {
          val subInitial = subMatrix.getInitial
          val subFinals = subMatrix.getFinal
          subMatrix.makeIntermediate(subInitial)
          subFinals.foreach(subMatrix.makeIntermediate(_))
          insertMatrix(subMatrix)
          addTransition(initialState, subInitial, null)
          subFinals.foreach(addTransition(_, finalState, null))
        }

        if (label != null) {
          // TODO
          throw new UnsupportedOperationException("Label binding is not implemented yet")
        }
      }
      case ConcatenationTerm(left, right, label) => {
        val leftMatrix = recFromPattern(left, null)
        val rightMatrix = recFromPattern(right, null)

        // change finals in left to intermediate
        val leftFinals = leftMatrix.getFinal
        leftFinals.foreach(leftMatrix.makeIntermediate(_))
        insertMatrix(leftMatrix)

        // add epsilons from left finals to right initial
        val rightInitial = rightMatrix.getInitial
        rightMatrix.makeIntermediate(rightInitial)
        insertMatrix(rightMatrix)
        leftFinals.foreach(addTransition(_, rightInitial, null))

        // assign action to right finals
        getFinal.foreach(assignAction(_, action))

        if (label != null) {
          // TODO
          throw new UnsupportedOperationException("Label binding is not implemented yet")
        }
      }
      case QuantifiedTerm(operand, q: KleenePlus, label) =>
        recFromPattern(ConcatenationTerm(operand, QuantifiedTerm(operand, KleeneStar()), label), action)
      case QuantifiedTerm(operand, q: KleeneStar, label) => {
        val operandMatrix = recFromPattern(operand, null)

        val initialState = createInitialNode()
        val finalState = createFinalNode(action)
        val workState = createIntermediateNode()
        addTransition(initialState, workState, null)
        addTransition(workState, finalState, null)

        val operandInitial = operandMatrix.getInitial
        operandMatrix.makeIntermediate(operandInitial)
        val operandFinals = operandMatrix.getFinal
        operandFinals.foreach(operandMatrix.makeIntermediate(_))

        insertMatrix(operandMatrix)

        addTransition(workState, operandInitial, null)
        operandFinals.foreach(addTransition(_, workState, null))

        if (label != null) {
          // TODO
          throw new UnsupportedOperationException("Label binding is not implemented yet")
        }
      }
    }
    adjMatrix
  }

}