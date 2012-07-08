/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.integration;

import org.junit.Test;

import ru.kfu.itis.cll.uima.eval.EvaluationLauncher;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GSBasedEvalLauncherTest {

	@Test
	public void testLauncherUsingPropertiesFile() throws Exception {
		EvaluationLauncher.main(new String[] { "src/test/resources/eval-launch.properties" });
	}

}