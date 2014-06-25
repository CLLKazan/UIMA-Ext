/**
 * 
 */
package org.nlplab.brat.configuration;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;
import org.junit.Test;
import org.nlplab.brat.configuration.EventRole.Cardinality;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class BratTypesConfigurationTest {

	@Test
	public void testParsing() throws IOException {
		BratTypesConfiguration btConf = BratTypesConfiguration.readFrom(
				new File("data/test-annotation.conf"));
		btConf.getType("GPE", BratEntityType.class);
		BratEntityType orgType = btConf.getType("Organization", BratEntityType.class);
		BratEventType startOrgType = btConf.getType("Start-org", BratEventType.class);
		assertEquals("Start-org", startOrgType.getName());
		assertEquals(2, startOrgType.getRoles().size());
		assertEquals(Cardinality.NON_EMPTY_ARRAY,
				startOrgType.getRole("Agent-Arg").getCardinality());
		assertEquals(3, startOrgType.getRole("Agent-Arg").getRangeTypes().size());
		assertEquals(Cardinality.ONE, startOrgType.getRole("Org-Arg").getCardinality());
		assertEquals(1, startOrgType.getRole("Org-Arg").getRangeTypes().size());
		assertEquals(orgType, startOrgType.getRole("Org-Arg").getRangeTypes().iterator().next());
		// check empty event parsing
		BratEventType shType = btConf.getType("Something-Happened", BratEventType.class);
		assertEquals("Something-Happened", shType.getName());
		assertEquals(0, shType.getRoles().size());
	}

}
