/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.seman;

import static org.junit.Assert.assertEquals;
import static ru.ksu.niimm.cll.uima.morph.seman.SemanMorphologyAnnotator.isRussianWord;
import static ru.ksu.niimm.cll.uima.morph.seman.SemanMorphologyAnnotator.normalize;

import org.junit.Test;

/**
 * @author Rinat Gareev
 * 
 */
public class StringNormalizingTest {

	@Test
	public void testNormalize() {
		assertEquals("Астрахань , елка", normalize("А́страха\u0488нь , ёл\u093Fка\u0310"));
		assertEquals("Ежик-foobar", normalize("Ёжик-foobar"));
	}

	@Test
	public void testRussianWordChecking() {
		assertEquals(false, isRussianWord("ёлкаHGF"));
		assertEquals(true, isRussianWord("123вы"));
		assertEquals(false, isRussianWord("2131-313141-31"));
		assertEquals(true, isRussianWord("елка"));
		assertEquals(true, isRussianWord("скатерть-самобранка"));
	}
}
