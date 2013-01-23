/**
 *
 */
package ru.kfu.itis.issst.uima.fsmm.nfsm

import ru.kfu.itis.issst.uima.fsmm
import org.scalatest.FunSuite
import fsmm.pattern._
import org.mockito.Mockito._

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class PatternToNFSMTestSuite extends FunSuite {

  private val fsmBuilder = new NFSMBuilder()

  test("Passing null pattern returns null") {
    assert(fsmBuilder.fromPattern(null) === null)
  }

  test("Passing atomic restriction returns single-transition-machine") {
    val pattern = mock(classOf[AtomicRestriction[_,_]])
    val fsm = fsmBuilder.fromPattern(pattern)
    // TODO implement final states:
    // 1) no outgoing transitions are allowed
    // 2) arriving to final state should fire event
    // ? TODO Event should contain matched input ? How to implement this ?
    assert(fsm === new SimpleNdfsmBuilder().addState("initial").addState("final"))
  }
}