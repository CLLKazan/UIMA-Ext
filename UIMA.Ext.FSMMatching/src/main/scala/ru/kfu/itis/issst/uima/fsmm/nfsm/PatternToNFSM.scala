/**
 *
 */
package ru.kfu.itis.issst.uima.fsmm.nfsm
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
        val initialState = createNode(NodeType.Initial)
        val finalState = createFinalNode(action)
        val am = new AnnotationMatcher(attrEx.asInstanceOf[AttributeExtractor[A]], matcher, value)
        addTransition(initialState, finalState, am)
        if (label != null) {
          addLabelBorder(initialState, label, true)
          addLabelBorder(finalState, label, false)
        }
      }
      case DisjunctionTerm(left, right, label) => {
        val leftMatrix = recFromPattern(left, null)
        val rightMatrix = recFromPattern(right, null)

        val initialState = createNode(NodeType.Initial)
        val finalState = createFinalNode(action)

        for (subMatrix <- List(leftMatrix, rightMatrix)) {
          val subInitial = subMatrix.getInitial
          val subFinals = subMatrix.getFinal
          subMatrix.setNodeType(subInitial, NodeType.Intermediate)
          subFinals.foreach(subMatrix.setNodeType(_, NodeType.Intermediate))
          insertMatrix(subMatrix)
          addTransition(initialState, subInitial, null)
          subFinals.foreach(addTransition(_, finalState, null))
        }

        if (label != null) {
          addLabelBorder(initialState, label, true)
          addLabelBorder(finalState, label, false)
        }
      }
      case ConcatenationTerm(left, right, label) => {
        val leftMatrix = recFromPattern(left, null)
        val rightMatrix = recFromPattern(right, null)

        // change finals in left to intermediate
        val leftFinals = leftMatrix.getFinal
        leftFinals.foreach(leftMatrix.setNodeType(_, NodeType.Intermediate))
        insertMatrix(leftMatrix)

        // add epsilons from left finals to right initial
        val rightInitial = rightMatrix.getInitial
        rightMatrix.setNodeType(rightInitial, NodeType.Intermediate)
        insertMatrix(rightMatrix)
        leftFinals.foreach(addTransition(_, rightInitial, null))

        if (label != null) {
          addLabelBorder(getInitial, label, true)
          getFinal.foreach(addLabelBorder(_, label, false))
        }
      }
      case QuantifiedTerm(operand, q: KleenePlus, label) =>
        recFromPattern(ConcatenationTerm(operand, QuantifiedTerm(operand, KleeneStar()), label), action)
      case QuantifiedTerm(operand, q: KleeneStar, label) => {
        val operandMatrix = recFromPattern(operand, null)
        // TODO
      }
    }
    adjMatrix
  }

}