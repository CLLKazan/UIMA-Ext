/**
 *
 */
package ru.kfu.itis.issst.uima.fsmm.nfsm.builder

import ru.kfu.itis.issst.uima.fsmm.nfsm
import nfsm._
import nfsm.builder.FSMAdjacencyMatrix._
import com.google.common.collect.HashBasedTable
import scala.collection.mutable.{ Map => MutaMap, Set => MutaSet }
import TableFSMAdjacencyMatrix._
import scala.collection.mutable.HashMap
import scala.collection.mutable.MultiMap

/**
 * Guava Table-based implementation.
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class TableFSMAdjacencyMatrix[A](private val idCounter: IdCounter) extends FSMAdjacencyMatrix[A] {

  private val transitionsTable = HashBasedTable.create[Int, Int, Set[Int]]()
  private val transitionMatchers = MutaMap.empty[Int, AnnotationMatcher[A]]
  private val transitionLabels = MutaMap.empty[Int, Set[LabelBorder]]
  private val nodeTypes = MutaMap.empty[Int, NodeType.Value]
  private val finalNodes = MutaMap.empty[Int, Action[A]]
  private var initialNode: Int = NO_NODE

  private def createNode(nodeType: NodeType.Value): Int = {
    val nodeId = idCounter.nextId()
    nodeTypes(nodeId) = nodeType
    nodeId
  }

  override def createIntermediateNode(): Int =
    createNode(NodeType.Intermediate)

  override def createInitialNode(): Int = {
    require(notExist(initialNode), "Can't create > than 1 initial nodes")
    initialNode = createNode(NodeType.Initial)
    initialNode
  }

  override def createFinalNode(action: Action[A]): Int = {
    val nodeId = createNode(NodeType.Final)
    finalNodes(nodeId) = action
    nodeId
  }

  override def assignAction(node: Int, action: Action[A]) {
    checkNodeExistence(node)
    require(isFinalNode(node), "Can't assign action to non-final node")
    finalNodes(node) = action
  }

  override def getInitial(): Int = {
    require(exist(initialNode), "There is no initial node yet")
    initialNode
  }

  override def getFinal(): Set[Int] = finalNodes.keySet.toSet

  override def makeIntermediate(node: Int) {
    checkNodeExistence(node)
    if (initialNode == node) {
      initialNode = NO_NODE
    } else // require action is null 
      finalNodes.get(node) match {
        case None => throw new IllegalArgumentException("Node %s is already intermediate".format(node))
        case Some(action) => {
          require(action == null, "Can't convert FINAL node with action to INTERMEDIATE")
          finalNodes.remove(node)
        }
      }
  }

  override def addTransition(from: Int, to: Int, im: AnnotationMatcher[A]): Int = {
    checkNodeExistence(from)
    checkNodeExistence(to)
    val transId = idCounter.nextId()
    transitionMatchers(transId) = im
    val oldSet = transitionsTable.get(from, to)
    val newSet: Set[Int] = (
      if (oldSet == null) Set(transId)
      else oldSet + transId
    )
    transitionsTable.put(from, to, newSet)
    transId
  }

  /*
   * MUST NOT be allowed on eps-transitions 
   */
  override def putLabelBorder(transitionId: Int, border: LabelBorder) {
    // TODO re-implement
    checkTransitionExistence(transitionId)
    require(transitionMatchers(transitionId) != null, "Can't put LabelBorder on eps-transition")
    transitionLabels(transitionId) = transitionLabels.get(transitionId) match {
      case None => Set(border)
      case Some(oldSet) => oldSet + border
    }
  }

  override def insertMatrix(_other: FSMAdjacencyMatrix[A]) {
    if (!_other.isInstanceOf[TableFSMAdjacencyMatrix[A]])
      throw new UnsupportedOperationException(
        "Can't insert matrix of type: %s".format(_other.getClass.getName))
    val other = _other.asInstanceOf[TableFSMAdjacencyMatrix[A]]
    require(this.idCounter == other.idCounter, "Can't insert matrix with different id counters")
    // merge nodes
    nodeTypes ++= other.nodeTypes
    // merge final nodes
    finalNodes ++= other.finalNodes
    // check initial nodes conflict
    if (exist(this.initialNode) && exist(other.initialNode))
      throw new IllegalStateException("Can't insert other matrix because it has initial state")
    if (exist(other.initialNode))
      this.initialNode = other.initialNode
    // merge transitions table
    transitionsTable.putAll(other.transitionsTable)
    // merge transition matchers
    transitionMatchers ++= other.transitionMatchers
    // merge transition labels
    transitionLabels ++= other.transitionLabels
  }

  override def buildNfsm(): NFiniteStateMachine[A] = {
    // sanity checks
    require(exist(initialNode), "There is no initial node")
    require(!finalNodes.isEmpty, "There is no final node")
    // 1. replace labels in node with LabelAcceptors in transitions

    // 2. calc eps-closures; keep them in nodes
    val epsClosures = MutaMap.empty[Int, MutaSet[Int]]
    traverseNodes(node => {
      epsClosures(node) = calculateEpsClosure(node, epsClosures)
    })

    // pre-3. check is ndfsm allow an empty input, i.e., eps-closure of the initial node contains a final state
    if (!epsClosures(initialNode).intersect(finalNodes.keySet).isEmpty)
      throw new IllegalStateException("Result NDFSM matches empty input sequences")
    // pre-3. preserve closure of initial noode
    var initialNodeClosure = epsClosures(initialNode).toSet

    // 3. remove nodes that contain only eps-transitions
    val epsNodes = getEpsilonNodes()

    // 3a. update eps-closures from (2)
    // epsClosures --= epsNodes
    for (closure <- epsClosures.values)
      closure --= epsNodes
    initialNodeClosure = initialNodeClosure -- epsNodes
    if (initialNodeClosure.isEmpty)
      throw new IllegalStateException("the initial node closure became empty after epsilon nodes removal")

    // 4. build 1-to-many transition objects (and states)
    val nfsm = new NFiniteStateMachine[A]
    import nfsm._

    // make state objects
    val nodesMap = MutaMap.empty[Int, State]
    // do not instantiate eps-nodes
    for (curNode <- getAllNodes; if !epsNodes.contains(curNode)) {
      val state =
        if (initialNodeClosure.contains(curNode))
          new State(true)
        else if (finalNodes.contains(curNode))
          new FinalState(finalNodes(curNode))
        else new State(false)
      nodesMap(curNode) = state
    }

    // make transition objects
    {
      import scala.collection.JavaConversions._
      for (
        fromId <- transitionsTable.rowKeySet();
        if !epsNodes.contains(fromId);
        fromState = nodesMap(fromId);
        (targetId, transitionIds) <- transitionsTable.row(fromId);
        transitionId <- transitionIds;
        inputMatcher = transitionMatchers(transitionId);
        if inputMatcher != null
      ) {
        val targetStateSet = epsClosures(targetId).map(nodesMap(_)).toSet
        // TODO assign acceptor
        val trans = new Transition(targetStateSet, inputMatcher, null)
        fromState.addTransition(trans)
      }
    }

    // aggregate nfsm
    nfsm.setInitialStates(initialNodeClosure.map(nodesMap(_)))
    nfsm.finishBuild()
    nfsm
  }

  private def getEpsilonNodes(): Set[Int] = {
    val allNodes = getAllNodes
    allNodes.filter(isEpsNode _)
    /* remove
    removedSet.foreach(removeNode _)
    removedSet
    */
  }

  /*
  private def removeNode(nodeId: Int) {
    require(!isFinalNode(nodeId), "Removal of a final node is not allowed")
    nodeTypes -= nodeId
    if (initialNode == nodeId)
      initialNode = NO_NODE

    import scala.collection.JavaConversions._

    val inTransitions =
      for (transSet <- transitionsTable.column(nodeId).values(); trans <- transSet)
        yield trans
    val outTransitions =
      for (transSet <- transitionsTable.row(nodeId).values(); trans <- transSet)
        yield trans
    // remove row
    transitionsTable.row(nodeId).clear()
    // remove column
    transitionsTable.column(nodeId).clear()
    // remove transitions
    val transitionsToRemove = inTransitions.toSet ++ outTransitions
    transitionsToRemove.foreach(removeTransition _)
    // TODO check whether predecessor become an eps-node after removal of this?
    // remember that it should be checked using derived eps-closures!
  }
  */

  private def removeTransition(transId: Int) {
    transitionMatchers.remove(transId)
    transitionLabels.remove(transId)
  }

  private def getAllNodes: Set[Int] = {
    import scala.collection.JavaConversions._
    transitionsTable.rowKeySet.toSet ++ transitionsTable.columnKeySet.toSet
  }

  private def traverseNodes(handler: Int => Unit) {
    val traversed = MutaSet.empty[Int]
    recTraverseNodes(initialNode, traversed, _ => true)(handler)
  }

  private def recTraverseNodes(node: Int, traversed: MutaSet[Int], transitionFilter: Int => Boolean)(handler: Int => Unit) {
    handler(node)
    traversed += node
    import scala.collection.JavaConversions._
    for (
      (linkedNode, transIds) <- transitionsTable.row(node);
      transId <- transIds; if transitionFilter(transId)
    ) if (!traversed.contains(linkedNode))
      recTraverseNodes(linkedNode, traversed, transitionFilter)(handler)
  }

  private def calculateEpsClosure(node: Int, closures: MutaMap[Int, MutaSet[Int]]): MutaSet[Int] = {
    val resultClosure = MutaSet.empty[Int]
    val traversed = MutaSet.empty[Int]
    recTraverseNodes(node, traversed, isEpsTransition)(node => {
      resultClosure += node
    })
    resultClosure
  }

  private def isEpsTransition(transId: Int): Boolean =
    transitionMatchers.get(transId) match {
      case None => throw new IllegalArgumentException("Transition %s does not exist".format(transId))
      case Some(am) => am == null
    }

  private def isEpsNode(nodeId: Int): Boolean = {
    if (isFinalNode(nodeId))
      false
    else {
      import scala.collection.JavaConversions._
      transitionsTable.row(nodeId).values().forall(_.forall(isEpsTransition(_)))
    }
  }

  private def isFinalNode(nodeId: Int): Boolean =
    finalNodes.contains(nodeId)

  private def checkNodeExistence(node: Int) =
    if (!nodeTypes.contains(node))
      throw new IllegalArgumentException("There is no node with id = %s".format(node))

  private def checkTransitionExistence(trans: Int) =
    if (!transitionMatchers.contains(trans))
      throw new IllegalArgumentException("There is no transition with id = %s".format(trans))
}

object TableFSMAdjacencyMatrix {
  private val NO_NODE = -1

  private def notExist(node: Int) = node < 0
  private def exist(node: Int) = node >= 0
}