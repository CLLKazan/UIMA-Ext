/**
 * 
 */
package ru.kfu.itis.cll.uima.util;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class DefaultCorpusSplit implements CorpusSplit {
	private Set<String> trainingSetPaths;
	private Set<String> testingSetPaths;

	public DefaultCorpusSplit(Iterable<String> trainingSetPaths, Iterable<String> testingSetPaths) {
		this.trainingSetPaths = ImmutableSet.copyOf(trainingSetPaths);
		this.testingSetPaths = ImmutableSet.copyOf(testingSetPaths);
	}

	@Override
	public Set<String> getTrainingSetPaths() {
		return trainingSetPaths;
	}

	@Override
	public Set<String> getTestingSetPaths() {
		return testingSetPaths;
	}
}