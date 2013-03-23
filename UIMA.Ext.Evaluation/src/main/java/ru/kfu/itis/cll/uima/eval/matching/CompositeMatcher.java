/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.matching;

import static ru.kfu.itis.cll.uima.cas.FSTypeUtils.getFeature;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * @author Rinat Gareev
 * 
 */
public class CompositeMatcher<FST extends FeatureStructure> implements Matcher<FST> {

	private List<Matcher<FST>> topMatchers;

	private CompositeMatcher() {
	}

	@Override
	public boolean match(FST ref, FST cand) {
		for (Matcher<FST> curMatcher : topMatchers) {
			if (!curMatcher.match(ref, cand)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(topMatchers).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CompositeMatcher)) {
			return false;
		}
		CompositeMatcher<?> that = (CompositeMatcher<?>) obj;
		return Objects.equal(this.topMatchers, that.topMatchers);
	}

	@Override
	public String toString() {
		return topMatchers == null ? "null-list" : topMatchers.toString();
	}

	@Override
	public void print(StringBuilder out, FST value) {
		boolean printExtBrackets = out.length() > 0;
		if (printExtBrackets)
			out.append("[");
		Iterator<Matcher<FST>> iter = topMatchers.iterator();
		if (iter.hasNext()) {
			iter.next().print(out, value);
		}
		while (iter.hasNext()) {
			out.append("|");
			iter.next().print(out, value);
		}
		if (printExtBrackets)
			out.append("]");
	}

	public static <FST extends FeatureStructure> Builder<FST> builderForFS(Type targetType) {
		return new Builder<FST>(targetType);
	}

	public static AnnotationMatcherBuilder builderForAnnotation(Type targetType) {
		return new AnnotationMatcherBuilder(targetType);
	}

	public static class Builder<FST extends FeatureStructure> {
		protected Type targetType;
		protected CompositeMatcher<FST> instance = new CompositeMatcher<FST>();

		protected Builder(Type targetType) {
			this.targetType = targetType;
			instance.topMatchers = Lists.newLinkedList();
		}

		public Builder<FST> addTypeChecker() {
			instance.topMatchers.add(new FSTypeMatcher<FST>(true));
			return this;
		}

		public Builder<FST> addPrimitiveFeatureMatcher(String featName) {
			Feature feature = getFeature(targetType, featName, true);
			instance.topMatchers.add(new PrimitiveFeatureMatcher<FST>(feature));
			return this;
		}

		public <FVT extends FeatureStructure> Builder<FST> addFSFeatureMatcher(String featName,
				Matcher<FVT> valueMatcher) {
			Feature feat = getFeature(targetType, featName, true);
			instance.topMatchers.add(new FSFeatureMatcher<FST, FVT>(feat, valueMatcher));
			return this;
		}

		/*
		 * this signature is required to allow build cyclic matcher references
		 * without exposing builder 'instance' field outside
		 */
		public <FVT extends FeatureStructure> Builder<FST> addFSFeatureMatcher(String featName,
				Builder<FVT> valueMatcherBuilder) {
			Feature feat = getFeature(targetType, featName, true);
			instance.topMatchers.add(new FSFeatureMatcher<FST, FVT>(
					feat, valueMatcherBuilder.instance));
			return this;
		}

		public <FET extends FeatureStructure> Builder<FST> addFSCollectionFeatureMatcher(
				String featName,
				Matcher<FET> elementMatcher, boolean ignoreOrder) {
			Feature feat = getFeature(targetType, featName, true);
			instance.topMatchers.add(new FSCollectionFeatureMatcher<FST, FET>(feat, elementMatcher,
					ignoreOrder));
			return this;
		}

		/*
		 * this signature is required to allow build cyclic matcher references
		 * without exposing builder 'instance' field outside
		 */
		public <FET extends FeatureStructure> Builder<FST> addFSCollectionFeatureMatcher(
				String featName,
				Builder<FET> elemMatcherBuilder, boolean ignoreOrder) {
			Feature feat = getFeature(targetType, featName, true);
			instance.topMatchers.add(new FSCollectionFeatureMatcher<FST, FET>(
					feat, elemMatcherBuilder.instance, ignoreOrder));
			return this;
		}

		public Type getTargetType() {
			return targetType;
		}

		public CompositeMatcher<FST> build() {
			instance.topMatchers = ImmutableList.copyOf(instance.topMatchers);
			return instance;
		}
	}

	public static class AnnotationMatcherBuilder extends Builder<AnnotationFS> {

		protected AnnotationMatcherBuilder(Type targetType) {
			super(targetType);
		}

		public Builder<AnnotationFS> addBoundaryMatcher() {
			instance.topMatchers.add(BoundaryMatcher.INSTANCE);
			return this;
		}

	}
}