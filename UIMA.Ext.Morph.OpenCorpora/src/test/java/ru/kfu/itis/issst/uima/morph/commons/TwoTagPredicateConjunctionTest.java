/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.commons;

import java.util.BitSet;

import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class TwoTagPredicateConjunctionTest {

	private final BitSet bs1;
	private final BitSet bs2;
	{
		bs1 = new BitSet();
		bs1.set(1);
		bs2 = new BitSet();
		bs2.set(2);
	}

	@Test
	public void testEmptyConjunction() {
		TwoTagPredicateConjunction conj = TwoTagPredicateConjunction.and();
		Assert.assertEquals(true, conj.apply(bs1, bs2));
	}

	@Test
	public void test1True() {
		// stub
		TwoTagPredicate predMock = mock(TwoTagPredicate.class);
		when(predMock.apply(bs1, bs2)).thenReturn(true);
		// use
		TwoTagPredicateConjunction conj = TwoTagPredicateConjunction.and(predMock);
		Assert.assertEquals(true, conj.apply(bs1, bs2));
	}

	@Test
	public void test2True() {
		// stub
		TwoTagPredicate predMock1 = mock(TwoTagPredicate.class);
		when(predMock1.apply(bs1, bs2)).thenReturn(true);
		TwoTagPredicate predMock2 = mock(TwoTagPredicate.class);
		when(predMock2.apply(bs1, bs2)).thenReturn(true);
		// use
		TwoTagPredicateConjunction conj = TwoTagPredicateConjunction.and(predMock1, predMock2);
		Assert.assertEquals(true, conj.apply(bs1, bs2));
		// verify that second predicate is also checked
		verify(predMock2).apply(bs1, bs2);
	}

	@Test
	public void test1False() {
		// stub
		TwoTagPredicate predMock1 = mock(TwoTagPredicate.class);
		when(predMock1.apply(bs1, bs2)).thenReturn(false);
		// use
		TwoTagPredicateConjunction conj = TwoTagPredicateConjunction.and(predMock1);
		Assert.assertEquals(false, conj.apply(bs1, bs2));
	}

	@Test
	public void test2False() {
		// stub
		TwoTagPredicate predMock1 = mock(TwoTagPredicate.class);
		when(predMock1.apply(bs1, bs2)).thenReturn(false);
		TwoTagPredicate predMock2 = mock(TwoTagPredicate.class);
		// short-circuit
		when(predMock2.apply(any(BitSet.class), any(BitSet.class))).thenThrow(
				new IllegalStateException());
		// use
		TwoTagPredicateConjunction conj = TwoTagPredicateConjunction.and(predMock1, predMock2);
		Assert.assertEquals(false, conj.apply(bs1, bs2));
	}

	@Test
	public void testTrueFalse() {
		// stub
		TwoTagPredicate predMock1 = mock(TwoTagPredicate.class);
		when(predMock1.apply(bs1, bs2)).thenReturn(true);
		TwoTagPredicate predMock2 = mock(TwoTagPredicate.class);
		when(predMock2.apply(bs1, bs2)).thenReturn(false);
		// use
		TwoTagPredicateConjunction conj = TwoTagPredicateConjunction.and(predMock1, predMock2);
		Assert.assertEquals(false, conj.apply(bs1, bs2));
		// verify
		verify(predMock2).apply(bs1, bs2);
	}
}
