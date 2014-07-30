package org.arabidopsis.ahocorasick;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.split;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import junit.framework.TestCase;

/**
 * Junit test cases for AhoCorasick.
 */

public class TestAhoCorasick extends TestCase {

	private AhoCorasick<String> tree;

	public void setUp() {
		this.tree = new AhoCorasick<String>();
	}

	public void testConstruction() {
		tree.add(split("h e l l o"), "hello");
		tree.add(split("h i"), "hi");
		tree.prepare();

		State<String> s0 = tree.getRoot();
		State<String> s1 = s0.get("h");
		State<String> s2 = s1.get("e");
		State<String> s3 = s2.get("l");
		State<String> s4 = s3.get("l");
		State<String> s5 = s4.get("o");
		State<String> s6 = s1.get("i");

		assertEquals(s0, s1.getFail());
		assertEquals(s0, s2.getFail());
		assertEquals(s0, s3.getFail());
		assertEquals(s0, s4.getFail());
		assertEquals(s0, s5.getFail());
		assertEquals(s0, s6.getFail());

		assertEquals(0, s0.getOutputs().size());
		assertEquals(0, s1.getOutputs().size());
		assertEquals(0, s2.getOutputs().size());
		assertEquals(0, s3.getOutputs().size());
		assertEquals(0, s4.getOutputs().size());
		assertEquals(1, s5.getOutputs().size());
		assertEquals(1, s6.getOutputs().size());

		assertTrue(s6 != null);
	}

	public void testExample() {
		tree.add(split("h e"), "he");
		tree.add(split("s h e"), "she");
		tree.add(split("h i s"), "his");
		tree.add(split("h e r s"), "hers");
		assertEquals(10, tree.getRoot().size());
		tree.prepare(); // after prepare, we can't call size()
		State<String> s0 = tree.getRoot();
		State<String> s1 = s0.get("h");
		State<String> s2 = s1.get("e");

		State<String> s3 = s0.get("s");
		State<String> s4 = s3.get("h");
		State<String> s5 = s4.get("e");

		State<String> s6 = s1.get("i");
		State<String> s7 = s6.get("s");

		State<String> s8 = s2.get("r");
		State<String> s9 = s8.get("s");

		assertEquals(s0, s1.getFail());
		assertEquals(s0, s2.getFail());
		assertEquals(s0, s3.getFail());
		assertEquals(s0, s6.getFail());
		assertEquals(s0, s8.getFail());

		assertEquals(s1, s4.getFail());
		assertEquals(s2, s5.getFail());
		assertEquals(s3, s7.getFail());
		assertEquals(s3, s9.getFail());

		assertEquals(0, s1.getOutputs().size());
		assertEquals(0, s3.getOutputs().size());
		assertEquals(0, s4.getOutputs().size());
		assertEquals(0, s6.getOutputs().size());
		assertEquals(0, s8.getOutputs().size());
		assertEquals(1, s2.getOutputs().size());
		assertEquals(1, s7.getOutputs().size());
		assertEquals(1, s9.getOutputs().size());
		assertEquals(2, s5.getOutputs().size());
	}

	public void testStartSearchWithSingleResult() {
		tree.add(split("a p p l e"), "apple");
		tree.prepare();
		SearchResult<String> result =
				tree.startSearch(split("w a s h i n g t o n   c u t   t h e   a p p l e   t r e e"));
		assertEquals(1, result.getOutputs().size());
		assertEquals("apple",
				new String(
						result.getOutputs().iterator().next()));
		assertEquals(21, result.getLastIndex());
		assertEquals(null, tree.continueSearch(result));
	}

	public void testStartSearchWithAdjacentResults() {
		tree.add(split("j o h n"), "john");
		tree.add(split("j a n e"), "jane");
		tree.prepare();
		SearchResult<String> firstResult =
				tree.startSearch(split("j o h n j a n e"));
		SearchResult<String> secondResult =
				tree.continueSearch(firstResult);
		assertEquals(null, tree.continueSearch(secondResult));
	}

	public void testStartSearchOnEmpty() {
		AhoCorasick<Integer> tree = new AhoCorasick<Integer>();
		tree.add(split("c i p h e r"), new Integer(0));
		tree.add(split("z i p"), new Integer(1));
		tree.add(split("n o u g h t"), new Integer(2));
		tree.prepare();
		SearchResult<Integer> result = tree.startSearch(new String[0]);
		assertEquals(null, result);
	}

	public void testMultipleOutputs() {
		tree.add(split("x"), "x");
		tree.add(split("x x"), "xx");
		tree.add(split("x x x"), "xxx");
		tree.prepare();

		SearchResult<String> result = tree.startSearch(split("x x x"));
		assertEquals(1, result.getLastIndex());
		assertEquals(new HashSet<String>(asList("x")),
				result.getOutputs());

		result = tree.continueSearch(result);
		assertEquals(2, result.getLastIndex());
		assertEquals(new HashSet<String>(asList("xx", "x")),
				result.getOutputs());

		result = tree.continueSearch(result);
		assertEquals(3, result.getLastIndex());
		assertEquals(new HashSet<String>(asList("xxx", "xx", "x")),
				result.getOutputs());

		assertEquals(null, tree.continueSearch(result));
	}

	public void testIteratorInterface() {
		tree.add(split("m o o"), "moo");
		tree.add(split("o n e"), "one");
		tree.add(split("o n"), "on");
		tree.add(split("n e"), "ne");
		tree.prepare();
		Iterator<SearchResult<String>> iter = tree.search(split(
				"o n e   m o o n   a g o"));

		assertTrue(iter.hasNext());
		SearchResult<String> r = iter.next();
		assertEquals(newHashSet("on"), r.getOutputs());
		assertEquals(2, r.getLastIndex());

		assertTrue(iter.hasNext());
		r = iter.next();
		assertEquals(newHashSet("one", "ne"),
				r.getOutputs());
		assertEquals(3, r.getLastIndex());

		assertTrue(iter.hasNext());
		r = iter.next();
		assertEquals(newHashSet("moo"),
				r.getOutputs());
		assertEquals(6, r.getLastIndex());

		assertTrue(iter.hasNext());
		r = iter.next();
		assertEquals(newHashSet("on"),
				r.getOutputs());
		assertEquals(7, r.getLastIndex());

		assertFalse(iter.hasNext());

		try {
			iter.next();
			fail();
		} catch (NoSuchElementException e) {
		}

	}

	public void largerTextExample() {
		String text = "T h e g a 3 m u t a n t o f A r a b i d o p s i s i s a " +
				"g i b b e r e l l i n - r e s p o n s i v e d w a r f . " +
				"W e p r e s e n t d a t a s h o w i n g t h a t t h e g a 3 - 1 " +
				"m u t a n t i s d e f i c i e n t i n e n t - k a u r e n e o x i d a s e " +
				"a c t i v i t y , t h e f i r s t c y t o c h r o m e P 4 5 0 - " +
				"m e d i a t e d s t e p i n t h e g i b b e r e l l i n b i o s y n t h " +
				"e t i c " +
				"p a t h w a y . B y u s i n g a c o m b i n a t i o n o f " +
				"c o n v e n t i o n a l " +
				"m a p - b a s e d c l o n i n g a n d r a n d o m s e q u e n c i n g w e " +
				"i d e n t i f i e d a p u t a t i v e c y t o c h r o m e P 4 5 0 g e n e " +
				"m a p p i n g t o t h e s a m e l o c a t i o n a s G A 3 . R e l a t i v e " +
				"t o t h e p r o g e n i t o r l i n e , t w o g a 3 m u t a n t a l l e l e s " +
				" c o n t a i n e d s i n g l e b a s e c h a n g e s " +
				"g e n e r a t i n g i n - " +
				"f r a m e  s t o p  c o d o n s  i n  t h e  p r e d i c t e d  a m i n o  " +
				"a c i d  " +
				"s e q u e n c e  o f  t h e  P 4 5 0 . A  g e n o m i c  c l o n e  " +
				"s p a n n i n g  " +
				"t h e  P 4 5 0  l o c u s  c o m p l e m e n t e d  t h e  g a 3 -2  " +
				"m u t a n t . " +
				"T h e  d e d u c e d  G A 3  p r o t e i n  d e f i n e s  a n  " +
				"a d d i t i o n a l  " +
				"c l a s s  o f  c y t o c h r o m e  P 4 5 0  e n z y m e s . T h e  G A 3  " +
				"g e n e  " +
				"w a s  e x p r e s s e d  i n  a l l  t i s s u e s  e x a m i n e d , R N A  " +
				"a b u n d a n c e  b e i n g  h i g h e s t  i n  i n f l o r e s c e n c e  " +
				"t i s s u e .";
		String[] terms = {
				"m i c r o s o m e",
				"c  y  t  o  c  h  r  o  m  e  ",
				"c  y t o c h r o m e  P 4 5 0  a c t i v i t y ",
				"g i b b e r e l l i c  a c i d  b i o s y n t h e s i s ",
				"G A 3 ",
				"c y t o c h r o m e  P 4 5 0 ",
				"o x y g e n  b i n d i n g ",
				"A T 5 G 2 5 9 0 0 .1 ",
				"p r o t e i n ",
				"R N A ",
				"g i b b e r e l l i n ",
				"A r a b i d o p s i s ",
				"e n t -k a u r e n e  o x i d a s e  a c t i v i t y ",
				"i n f l o r e s c e n c e ",
				"t i s s u e ",
		};
		String[] labels = {
				"cytochrome",
				"GA3",
				"cytochrome P450",
				"protein",
				"RNA",
				"gibberellin",
				"Arabidopsis",
				"ent-kaurene oxidase activity",
				"inflorescence",
				"tissue",
		};
		for (int i = 0; i < terms.length; i++) {
			tree.add(split(terms[i]), labels[i]);
		}
		tree.prepare();

		Set<String> termsThatHit = new HashSet<String>();
		for (Iterator<SearchResult<String>> iter = tree.search(split(text)); iter.hasNext();) {
			SearchResult<String> result = iter.next();
			termsThatHit.addAll(result.getOutputs());
		}
		assertEquals(newHashSet(
				"cytochrome",
				"GA3",
				"cytochrome P450",
				"protein",
				"RNA",
				"gibberellin",
				"Arabidopsis",
				"ent-kaurene oxidase activity",
				"inflorescence",
				"tissue"),
				termsThatHit);

	}
}
