/**
 *
 */
package ru.kfu.itis.issst.uima.fsmm.nfsm

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
trait FSMAdjacencyMatrix[A] {

  import FSMAdjacencyMatrix._

  def createNode(): Int =
    createNode(NodeType.Intermediate)

  def createNode(nodeType: NodeType.Value): Int

  def createFinalNode(action: Action[A]): Int

  def getInitial: Int

  def getFinal: Set[Int]

  def setNodeType(node: Int, newType: NodeType.Value)

  def addTransition(from: Int, to: Int, im: AnnotationMatcher[A])

  def addLabelBorder(node: Int, label: String, begin: Boolean)

  /*
   * Ensure that there are no node id conflicts
   */
  def insertMatrix(other: FSMAdjacencyMatrix[A])

  def buildNfsm(): NFiniteStateMachine[A]
}

object FSMAdjacencyMatrix {
  object NodeType extends Enumeration {
    val Final = Value
    val Intermediate = Value
    val Initial = Value
  }

  def create[A](): FSMAdjacencyMatrix[A] =
    // TODO
    throw new UnsupportedOperationException("TODO")
}