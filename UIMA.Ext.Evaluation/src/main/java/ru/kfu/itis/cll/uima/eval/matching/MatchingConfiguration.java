/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.matching;

import java.util.List;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * @author Rinat Gareev
 * 
 */
public class MatchingConfiguration {

	private List<Matcher<AnnotationFS>> topMatchers;

	private MatchingConfiguration() {
	}

	public List<Matcher<AnnotationFS>> getMatchers() {
		return topMatchers;
	}

	public static Builder builder(Type targetType) {
		return new Builder(targetType);
	}

	public static class Builder {
		private Type targetType;
		private MatchingConfiguration instance = new MatchingConfiguration();

		private Builder(Type targetType) {
			this.targetType = targetType;
			instance.topMatchers = Lists.newLinkedList();
		}

		public Builder addBoundaryMatcher() {
			instance.topMatchers.add(new BoundaryMatcher());
			return this;
		}

		public <FVT extends FeatureStructure> Builder addFSFeatureMatcher(String featName,
				Matcher<FVT> valueMatcher) {
			Feature feat = targetType.getFeatureByBaseName(featName);
			if (feat == null) {
				throw new IllegalArgumentException(String.format(
						"No feature '%s' in type %s", featName, targetType));
			}
			instance.topMatchers.add(new FSFeatureMatcher<AnnotationFS, FVT>(feat, valueMatcher));
			return this;
		}

		public MatchingConfiguration build() {
			instance.topMatchers = ImmutableList.copyOf(instance.topMatchers);
			return instance;
		}
	}
}