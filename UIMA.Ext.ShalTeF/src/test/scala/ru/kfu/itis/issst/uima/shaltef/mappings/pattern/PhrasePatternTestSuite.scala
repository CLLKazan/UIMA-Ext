package ru.kfu.itis.issst.uima.shaltef.mappings.pattern

import org.scalatest.FunSuite
import ru.kfu.itis.issst.uima.shaltef.util.NprCasBuilder
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._

class PhrasePatternTestSuite extends FunSuite with MockitoSugar {

  private val text = "foobar"

  test("ConstraintConjunctionPhrasePattern matching") {
    val cb = new NprCasBuilder(text, Nil)
    import cb._
    val trigger = w("trigger", 0, 1)
    val np1 = {
      w("head1", 5, 6)
      np("head1", index = true)
    }
    val emptyPP = new ConstraintConjunctionPhrasePattern(Nil)
    val ctx = MatchingContext(trigger)
    assert(emptyPP.matches(np1, ctx))

    val pc1 = mock[PhraseConstraint]
    val pc2 = mock[PhraseConstraint]
    val pc3 = mock[PhraseConstraint]

    when(pc1.matches(np1, ctx)).thenReturn(true)
    when(pc2.matches(np1, ctx)).thenReturn(false)
    when(pc3.matches(np1, ctx)).thenReturn(true)

    assert(!new ConstraintConjunctionPhrasePattern(pc1 :: pc2 :: pc3 :: Nil).matches(np1, ctx))
    assert(new ConstraintConjunctionPhrasePattern(pc1 :: pc3 :: Nil).matches(np1, ctx))
    assert(new ConstraintConjunctionPhrasePattern(pc3 :: Nil).matches(np1, ctx))
  }

}