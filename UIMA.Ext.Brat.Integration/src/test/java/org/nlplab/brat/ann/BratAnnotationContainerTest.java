/**
 * 
 */
package org.nlplab.brat.ann;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class BratAnnotationContainerTest {

	@Test
	public void testIndexedRoleParsing() {
		assertArrayEquals(null, BratAnnotationContainer.parseRoleIndex("someRole"));
		assertArrayEquals(new Object[] { "someRole", 2 },
				BratAnnotationContainer.parseRoleIndex("someRole2"));
		assertArrayEquals(new Object[] { "someRole", 30 },
				BratAnnotationContainer.parseRoleIndex("someRole030"));
	}

}
