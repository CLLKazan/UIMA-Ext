/**
 * 
 */
package ru.kfu.itis.cll.uima.io.axml;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class AXML2XMITest {

	@Test
	public void testLauncher() throws UIMAException, IOException, SAXException {
		AXML2XMI.main(new String[] {
				"-i", "test-data",
				"-o", "target",
				"-ts", "test.entities-ts"
		});
	}

}
