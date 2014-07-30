package org.arabidopsis.ahocorasick;

import static org.apache.commons.lang3.StringUtils.split;
import junit.framework.TestCase;

public class TestState extends TestCase {
	public void testSimpleExtension() {
		State<String> s = new State<String>(0);
		State<String> s2 = s.extend("a");
		assertTrue(s2 != s && s2 != null);
		assertEquals(2, s.size());
	}

	public void testSimpleExtensionSparse() {
		State<String> s = new State<String>(50);
		State<String> s2 = s.extend("3");
		assertTrue(s2 != s && s2 != null);
		assertEquals(2, s.size());
	}

	public void testSingleState() {
		State<String> s = new State<String>(0);
		assertEquals(1, s.size());
	}

	public void testExtendAll() {
		State<String> s = new State<String>(0);
		s.extendAll(split("h e l l o w o r l d"));
		assertEquals(11, s.size());
	}

	public void testExtendAllTwiceDoesntAddMoreStates() {
		State<String> s = new State<String>(0);
		State<String> s2 = s.extendAll(split("h e l l o w o r l d"));
		State<String> s3 = s.extendAll(split("h e l l o w o r l d"));
		assertEquals(11, s.size());
		assertTrue(s2 == s3);
	}

	public void testAddingALotOfStatesIsOk() {
		State<String> s = new State<String>(0);
		for (int i = 0; i < 256; i++)
			s.extend(String.valueOf(i));
		assertEquals(257, s.size());
	}
}