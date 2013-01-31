/**
 *
 */
package ru.kfu.itis.issst.uima.fsmm.nfsm

import org.scalatest.FunSuite
import ru.kfu.itis.issst.uima.fsmm
import fsmm.pattern._
import fsmm.nfsm.test._
import fsmm.nfsm.builder.PatternToNFSM

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
class PatternMatchingTestSuite extends FunSuite {

  val NameExtractor = new NameExtractor
  val StringMatcher = new StringMatcher
  val pattern2Nfsm = new PatternToNFSM[DumbAnnotation]

  test("Concatenation test") {
    val pattern = ConcatenationTerm(
      AtomicRestriction(NameExtractor, StringMatcher, "X"),
      AtomicRestriction(NameExtractor, StringMatcher, "Y"))
    val nfsm = pattern2Nfsm.fromPattern(pattern, (matchedAnnos, _) => {
      println(matchedAnnos)
    })
    println("NFSM for pattern %s:".format(pattern))
    println(nfsm)
    val input = new DumbInputGraph()
    import input._
    addAnno("X", 0, 1)
    addAnno("X", 2, 3)
    addAnno("Y", 4, 5)
    addAnno("Y", 6, 7)
    println("Matches of X Y pattern:")
    nfsm.process(input)
  }

  test("Initial test") {
    val patternY = AtomicRestriction(NameExtractor, StringMatcher, "Y")
    val patternX = AtomicRestriction(NameExtractor, StringMatcher, "X")
    val nfsmY = pattern2Nfsm.fromPattern(patternY, (matchedAnnos, _) => {
      println(matchedAnnos)
    })
    val nfsmX = pattern2Nfsm.fromPattern(patternX, (matchedAnnos, _) => {
      println(matchedAnnos)
    })
    val input = new DumbInputGraph()
    import input._
    addAnno("X", 0, 1)
    addAnno("X", 2, 3)
    addAnno("Y", 4, 5)
    addAnno("Y", 6, 7)
    println("Matches of Y pattern:")
    nfsmY.process(input)
    println("Matches of X pattern:")
    nfsmX.process(input)
  }

}