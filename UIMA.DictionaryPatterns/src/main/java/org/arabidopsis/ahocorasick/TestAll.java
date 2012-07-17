package org.arabidopsis.ahocorasick;

import junit.framework.*;

public class TestAll extends TestCase {
    public static Test suite() {
	TestSuite suite = new TestSuite();
	suite.addTestSuite(TestState.class);
	suite.addTestSuite(TestAhoCorasick.class);
	return suite;
    }
}
