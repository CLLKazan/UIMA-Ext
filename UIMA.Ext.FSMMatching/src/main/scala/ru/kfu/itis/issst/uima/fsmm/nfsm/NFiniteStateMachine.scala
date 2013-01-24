/**
 *
 */
package ru.kfu.itis.issst.uima.fsmm.nfsm

import ru.kfu.itis.issst.uima.fsmm.input
import ru.kfu.itis.issst.uima.fsmm.util
import input.InputGraph
import scala.collection.mutable.{ Queue => MutableQueue }
import scala.collection.mutable.{ Map => MutableMap }
import scala.collection.mutable.ListBuffer
import util.Buildable
import ru.kfu.itis.issst.uima.fsmm.util.Buildable
import scala.collection.mutable.MultiMap

/**
 * Represents a non-deterministic finite state machine
 * @tparam A input element type
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class NFiniteStateMachine[A] extends Buildable {
  private type StateTuple = (State, Track, VariableContext)
  private type StateTupleReplacer = StateTuple => TraversableOnce[StateTuple]

  private[this] var initialState: State = _

  def setInitialState(initialState: State) {
    require(!isBuilt, "Can't change initial state in built NDFSM")
    require(initialState.isBuilt, "Can't construct NDFSM from non-finished initial state")
    require(initialState.initial, "Given state is not initial")
    this.initialState = initialState
    finishBuild()
  }

  def process(input: InputGraph[A]) {
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
    // accept initial state
    val track = accept(initialState, new Track())
    val varCtx = new VariableContext()
    val currentStates = MutableQueue.empty[StateTuple]

    currentStates += ((initialState, track, varCtx))
    val finishedMachines = new MutableMap[FinalState, Set[Track]] with MultiMap[FinalState, Track]
    // consume epsilons
    handleStateTuples(currentStates, proceedEpsilon(finishedMachines))
    // recursively consume input
    process(input, anno, currentStates, finishedMachines)
    // TODO implement 'match all' strategy, i.e. not only the longest
    for((fs, matchedTracks) <- finishedMachines; if fs.action != null) {
      val maxLength = matchedTracks.maxBy(_.length).length
      val maxAll = matchedTracks.filter(_.length == maxLength)
      for(maxTrack <- matchedTracks; (aList, labelBindings) = maxTrack.toExternalRepresentation)
        fs.action(aList, labelBindings)
    }
  }

  /*
   * PRECONDITION: epsilon transition should be proceeded
   */
  private def process(input: InputGraph[A], anno: A, currentStates: MutableQueue[StateTuple], 
      finishedMachines: MultiMap[FinalState, Track]) {
    // consume anno
    handleStateTuples(currentStates, proceedAnnotation(anno, finishedMachines))
    // final states are checked immediately after transitions
    if (!currentStates.isEmpty) {
      // consume epsilons
      handleStateTuples(currentStates, proceedEpsilon(finishedMachines))
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
    }
    recHandleStateTuples(states, handler)
  }

  // TODO handle chains of eps-transitions - all eps-chains MUST be optimized after NDFSM construction!
  private def proceedEpsilon(finishedMachines: MultiMap[FinalState, Track])(stateTuple: StateTuple): TraversableOnce[StateTuple] = {
    val (state, _track, varContext) = stateTuple
    val resultStateTuples = new ListBuffer[StateTuple]
    // add itself
    resultStateTuples += stateTuple
    // add states linked by epsilon-transitions
    for (targetState <- state.matchEpsilonTransitions) {
      val track = accept(targetState, _track)
      targetState match {
        case fs:FinalState => finishedMachines.addBinding(fs, track)
        case os => resultStateTuples += ((targetState, track, varContext)) 
      }
    }
    resultStateTuples
  }

  private def proceedAnnotation(anno: A, finishedMachines: MultiMap[FinalState, Track])(stateTuple: StateTuple): TraversableOnce[StateTuple] = {
    val (state, _track, varContext) = stateTuple
    val resultStateTuples = new ListBuffer[StateTuple]
    for ((targetState, newVarCtx) <- state.matchTransitions(anno, varContext)) {
      val track = accept(targetState, _track.prepend(new ConsumedInputElement(anno)))
      targetState match {
        case fs:FinalState => finishedMachines.addBinding(fs, track)
        case os => resultStateTuples += ((
          targetState,
          track,
          newVarCtx)) 
      }
    }
    resultStateTuples
  }

  /**
   * @param state the state that we moved into
   * @param track track
   */
  private def accept(state: State, track: Track): Track = {
    var newTrack = track
    for (curBorder <- state.getLabelBorders)
      newTrack = newTrack.prepend(curBorder)
    newTrack
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
            resultList += a
            openLabelBindings.values.foreach(_ += a)
          }
          case LabelBorder(label, true) => openLabelBindings(label) = ListBuffer.empty[A]
          case LabelBorder(label, false) =>
            openLabelBindings.remove(label) match {
              case None => new IllegalStateException("Can't find the beginning of the label binding '%s'".format(label))
              case Some(list) => closedLabelBindings += (label -> list.toList)
            }
        }
      // sanity check
      if (!openLabelBindings.isEmpty)
        throw new IllegalStateException("Label bindings are not closed: %s".format(openLabelBindings.keys))
      (resultList.toList, closedLabelBindings)
    }
  }

  private[nfsm] sealed abstract class TrackElement
  private case class ConsumedInputElement(input: A) extends TrackElement
  private[nfsm] case class LabelBorder(label: String, begin: Boolean) extends TrackElement

  /*
 * Immutable State implementation seems to be not feasible as there may be cyclic paths  
 */
  private[nfsm] class State(val initial: Boolean) extends Buildable {

    private var transitions = List.empty[Transition]
    private var epsilonTransitions = Set.empty[State]
    private var labelBorders = List.empty[LabelBorder]

    // build methods
    private[nfsm] def addTransition(to: State, input: InputMatcher[A]) {
      checkTransitionSanity(to)
      transitions ::= new Transition(this, to, input)
    }

    private[nfsm] def addEpsilonTransition(to: State) {
      checkTransitionSanity(to)
      epsilonTransitions += to
    }

    private[nfsm] def addLabelBorder(lb: LabelBorder) {
      require(!isBuilt, "Modification of a built state is not allowed")
      labelBorders ::= lb
    }

    private def checkTransitionSanity(to: State) {
      require(!isBuilt, "Modification of a built state is not allowed")
      require(!isFinal, "Can't add transition from final state")
      require(!to.initial, "Can't add transition to initial state")
    }
    // end of build methods

    protected[nfsm] def isFinal = false

    private[nfsm] def matchEpsilonTransitions = epsilonTransitions

    private[nfsm] def matchTransitions(anno: A, varCtx: VariableContext): TraversableOnce[(State, VariableContext)] = {
      // TODO optimization point
      for (curTr <- transitions; (matched, newVarCtx) = curTr.matches(anno, varCtx); if matched)
        yield (curTr.to, newVarCtx)
    }

    private[nfsm] def getLabelBorders = labelBorders
  }

  private[nfsm] class FinalState(val action: (List[A], Map[String, List[A]]) => Unit) extends State(false) {
    override def isFinal = true
  }

  private[nfsm] class Transition(val from: State, val to: State, val inputMatcher: InputMatcher[A]) {
    def matches(anno: A, varCtx: VariableContext): (Boolean, VariableContext) =
      inputMatcher.matches(anno, varCtx)
  }
}

class VariableContext