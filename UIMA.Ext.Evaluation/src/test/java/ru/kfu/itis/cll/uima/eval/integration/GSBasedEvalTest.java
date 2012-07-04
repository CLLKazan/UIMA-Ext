/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.integration;

import org.junit.Test;

import com.google.common.collect.Sets;

import ru.kfu.itis.cll.uima.eval.EvaluationConfig;

/**
 * @author Rinat Gareev
 *
 */
public class GSBasedEvalTest {
	
	@Test
	public void test(){
		EvaluationConfig cfg = new EvaluationConfig();
		cfg.setAnnoTypes(Sets.newHashSet("test.Company", "test.Person"));
	}

}