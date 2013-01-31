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

  /*private val fsmBuilder = new PatternToNFSM()

  test("Passing null pattern returns null") {
    assert(fsmBuilder.fromPattern(null) === null)
  }

  test("Passing atomic restriction returns single-transition-machine") {
    val pattern = mock(classOf[AtomicRestriction[_,_]])
    val fsm = fsmBuilder.fromPattern(pattern)
    assert(fsm === new SimpleNdfsmBuilder().addState("initial").addState("final"))
  }*/
}