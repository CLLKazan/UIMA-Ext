/**
 * 
 */
package ru.kfu.itis.issst.uima.brat;

import static org.junit.Assert.assertEquals;
import static ru.kfu.itis.issst.uima.brat.PUtils.toProperJavaName;

import org.junit.Test;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class UtilTest {

	@Test
	public void testBratToJavaNameConversions() {
		assertEquals("TransferOwnership", toProperJavaName("Transfer-ownership"));
		assertEquals("TransferOwnership", toProperJavaName("Transfer--ownership"));
		assertEquals("TransferOwnership", toProperJavaName("Transfer-Ownership"));
		assertEquals("TransferOwnership", toProperJavaName("Transfer--Ownership"));
		assertEquals("Ownership", toProperJavaName("-Ownership"));
		assertEquals("Ownership", toProperJavaName("--ownership"));
	}

}
