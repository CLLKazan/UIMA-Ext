/**
 *
 */
package ru.kfu.itis.issst.uima.fsmm.nfsm.builder

import ru.kfu.itis.issst.uima.fsmm.nfsm._

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
trait FSMAdjacencyMatrix[A] {

  import FSMAdjacencyMatrix._

  def createInitialNode(): Int

  def createIntermediateNode(): Int

  def createFinalNode(action: Action[A]): Int

  def assignAction(node: Int, action: Action[A])

  def getInitial: Int

  def getFinal: Set[Int]

  def makeIntermediate(node: Int)

  def addTransition(from: Int, to: Int, im: AnnotationMatcher[A]): Int

  /*
   * MUST NOT be allowed on eps-transitions 
   */
  def putLabelBorder(transitionId: Int, border: LabelBorder)

  //  def addLabelBorder(node: Int, label: String, begin: Boolean)

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

  // TODO FIX
  private val idCounter = new IdCounter

  def create[A](): FSMAdjacencyMatrix[A] =
    new TableFSMAdjacencyMatrix(idCounter)
}