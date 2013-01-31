/**
 *
 */
package ru.kfu.itis.issst.uima.fsmm.nfsm

import ru.kfu.itis.issst.uima.fsmm.input
import ru.kfu.itis.issst.uima.fsmm.util
import input.InputGraph
import scala.collection.mutable.{ Queue => MutableQueue }
import scala.collection.mutable.{ Map => MutableMap }
import scala.collection.mutable.{ Set => MutableSet }
import scala.collection.mutable.ListBuffer
import util.Buildable
import ru.kfu.itis.issst.uima.fsmm
import fsmm.util.Buildable
import scala.collection.mutable.MultiMap
import scala.collection.mutable.HashMap

/**
 * Represents a non-deterministic finite state machine
 * @tparam A input element type
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class NFiniteStateMachine[A] extends Buildable {
  private type StateTuple = (State, Track, VariableContext)
  private type StateTupleReplacer = StateTuple => TraversableOnce[StateTuple]

  private[this] var initialStates: Set[State] = _

  private[this] val idCounter = new IdCounter

  def setInitialStates(initialStates: Set[State]) {
    require(!isBuilt, "Can't change initial state in built NDFSM")
    require(initialStates.forall(_.initial), "Given state is not initial")
    this.initialStates = initialStates
  }

  override def finishBuild() {
    traverse(_.finishBuild())
    super.finishBuild()
  }

  def process(input: InputGraph[A]) {
    require(isBuilt, "NFSM is not built")
    process(input, -1)
  }

  private def process(input: InputGraph[A], from: Int) {
    val (annoSet, annoSetBegin) = input.next(from)
    for (curAnno <- annoSet)
      process(input, curAnno)
    if (!annoSet.isEmpty)
      process(input, annoSetBegin)
  }

  private def process(input: InputGraph[A], anno: A) {
    val track = new Track()
    val varCtx = new VariableContext()
    val currentStates = MutableQueue.empty[StateTuple]

    for (iState <- initialStates)
      currentStates += ((iState, track, varCtx))
    val finishedMachines = new HashMap[FinalState, MutableSet[Track]] with MultiMap[FinalState, Track]
    // recursively consume input
    process(input, anno, currentStates, finishedMachines)
    // trigger actions
    triggerActions(finishedMachines)
  }

  private def process(input: InputGraph[A], anno: A,
    currentStates: MutableQueue[StateTuple], finishedMachines: MultiMap[FinalState, Track]) {
    // consume anno
    handleStateTuples(currentStates, proceedAnnotation(anno, finishedMachines))
    // final states are checked immediately after transition pass
    if (!currentStates.isEmpty) {
      val (nextAnnos, nextAnnosBegin) = input.next(anno)
      // TODO optimization point
      for (nextAnno <- nextAnnos)
        process(input, nextAnno, currentStates.clone, finishedMachines)
    }
  }

  private def handleStateTuples(states: MutableQueue[StateTuple], handler: StateTupleReplacer) {
    // null is an end marker
    states += null
    recHandleStateTuples(states, handler)
  }

  private def recHandleStateTuples(states: MutableQueue[StateTuple], handler: StateTupleReplacer) {
    val curState = states.dequeue()
    if (curState != null) {
      states ++= handler(curState)
      recHandleStateTuples(states, handler)
    }
  }

  private def proceedAnnotation(anno: A, finishedMachines: MultiMap[FinalState, Track])(stateTuple: StateTuple): TraversableOnce[StateTuple] = {
    val (state, track, varContext) = stateTuple
    val resultStateTuples = new ListBuffer[StateTuple]
    for ((matchedTrans, newVarCtx) <- state.matchTransitions(anno, varContext)) {
      val newTrack = matchedTrans.pass(track, anno)
      for (targetState <- matchedTrans.to) {
        targetState match {
          case fs: FinalState => finishedMachines.addBinding(fs, newTrack)
          case os => resultStateTuples += ((targetState, newTrack, newVarCtx))
        }
        // looks like following - https://issues.scala-lang.org/browse/SI-4938
        doNothing()
      }
    }
    resultStateTuples
  }

  private def triggerActions(finishedMachines: MultiMap[FinalState, Track]) {
    // TODO implement 'match all' strategy, i.e. not only the longest
    for ((fs, matchedTracks) <- finishedMachines; if fs.action != null) {
      val maxLength = matchedTracks.maxBy(_.length).length
      val maxAll = matchedTracks.filter(_.length == maxLength)
      matchedTracks.foreach(maxTrack => {
        val (aList, labelBindings) = maxTrack.toExternalRepresentation
        fs.action(aList, labelBindings)
      })
    }
  }

  private def doNothing() {}

  private def traverse(handler: State => Unit) {
    val traversed = MutableSet.empty[State]
    initialStates.foreach(recTraverse(_, handler, traversed))
  }

  private def recTraverse(state: State, handler: State => Unit, traversed: MutableSet[State]): Unit =
    if (!traversed.contains(state)) {
      handler(state)
      traversed += state
      for (transition <- state.transitions; linkedState <- transition.to)
        recTraverse(linkedState, handler, traversed)
    }

  override def toString: String = {
    val sb = new StringBuilder
    // print states
    val initialStates = MutableSet.empty[State]
    val finalStates = MutableSet.empty[State]
    val otherStates = MutableSet.empty[State]
    traverse(state => {
      if (state.isFinal)
        finalStates += state
      else if (state.initial)
        initialStates += state
      else otherStates += state
    })
    sb append "Initial states: " append initialStates append "\n"
    sb append "Final states: " append finalStates append "\n"
    sb append "Other states: " append otherStates append "\n"
    // print transitions
    sb append "Transitions:\n"
    traverse(state => {
      for (tr <- state.transitions)
        sb append state append " " append tr append "\n"
    })
    sb.toString
  }

  /**
   * Immutable state of matching
   */
  private class Track(val consumedInput: List[TrackElement]) {
    def this() = this(Nil)

    lazy val length = consumedInput.length

    def prepend(head: TrackElement): Track = new Track(head :: consumedInput)

    def toExternalRepresentation: (List[A], Map[String, List[A]]) = {
      val resultList = ListBuffer.empty[A]
      var closedLabelBindings = Map.empty[String, List[A]]
      val openLabelBindings = MutableMap.empty[String, ListBuffer[A]]
      for (curTE <- consumedInput)
        curTE match {
          case ConsumedInputElement(a) => {
            // prepend as a track is constructed in reverse direction 
            a +=: resultList
            openLabelBindings.values.foreach(_ += a)
          }
          // TODO
          case _ => throw new UnsupportedOperationException("LabelBorder handling is not implemented yet")
          /*
          case LabelBorder(label, true) => openLabelBindings(label) = ListBuffer.empty[A]
          case LabelBorder(label, false) =>
            openLabelBindings.remove(label) match {
              case None => new IllegalStateException("Can't find the beginning of the label binding '%s'".format(label))
              case Some(list) => closedLabelBindings += (label -> list.toList)
            }
            */
        }
      // sanity check
      if (!openLabelBindings.isEmpty)
        throw new IllegalStateException("Label bindings are not closed: %s".format(openLabelBindings.keys))
      (resultList.toList, closedLabelBindings)
    }
  }

  private case class ConsumedInputElement(input: A) extends TrackElement

  /*
 * Immutable State implementation seems to be not feasible as there may be cyclic paths  
 */
  private[nfsm] class State(val initial: Boolean) extends Buildable {

    private[NFiniteStateMachine] val id = idCounter.nextId()
    private[NFiniteStateMachine] var transitions = List.empty[Transition]

    // build methods
    private[nfsm] def addTransition(tr: Transition) {
      tr.to.foreach(checkTransitionSanity(_))
      transitions ::= tr
    }

    private def checkTransitionSanity(to: State) {
      require(!isBuilt, "Modification of a built state is not allowed")
      require(!isFinal, "Can't add transition from final state")
      require(!to.initial, "Can't add transition to initial state")
    }
    // end of build methods

    protected[nfsm] def isFinal = false

    private[nfsm] def matchTransitions(anno: A, varCtx: VariableContext): TraversableOnce[(Transition, VariableContext)] = {
      // TODO optimization point
      for (
        curTrans <- transitions;
        (matched, newVarCtx) = curTrans.matches(anno, varCtx);
        if matched
      ) yield (curTrans, newVarCtx)
    }

    override def toString = id.toString
  }

  private[nfsm] class FinalState(val action: Action[A]) extends State(false) {
    override def isFinal = true

    override def toString = new StringBuilder(super.toString)
      .append("{").append(action).append("}").toString
  }

  private[nfsm] class Transition(val to: Set[State],
    val inputMatcher: AnnotationMatcher[A], _acceptor: TransitionAcceptor) {

    private[NFiniteStateMachine] val id = idCounter.nextId()
    private val acceptor = if (_acceptor != null) _acceptor else noOpAcceptor

    def matches(anno: A, varCtx: VariableContext): (Boolean, VariableContext) =
      inputMatcher.matches(anno, varCtx)
    private[NFiniteStateMachine] def pass(_track: Track, anno: A): Track = {
      var track = _track
      acceptor.beforeConsumption match {
        case None =>
        case Some(te) => track = track.prepend(te)
      }
      track = track.prepend(new ConsumedInputElement(anno))
      acceptor.afterConsumption match {
        case None =>
        case Some(te) => track = track.prepend(te)
      }
      track
    }

    override def toString: String =
      new StringBuilder().append("-> ").append(to).append(":").append(inputMatcher).toString
  }

  private[nfsm] trait TransitionAcceptor {
    def beforeConsumption: Option[TrackElement]
    def afterConsumption: Option[TrackElement]
  }

  private val noOpAcceptor = new TransitionAcceptor {
    override val beforeConsumption = None
    override val afterConsumption = None
  }
}

/**
 * Immutable (!) state of variables
 */
class VariableContext

private[nfsm] sealed abstract class TrackElement

private[nfsm] case class LabelBorder(label: String, borderType: LabelBorderType.Value)
  extends TrackElement

private[nfsm] object LabelBorderType extends Enumeration {
  val Begin = Value
  val End = Value
  val RepeatableBegin = Value
  val RepeatableEnd = Value
}