/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.matching;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * Dispatch matching to a submatcher on the basis of reference FS type.
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class TypeBasedMatcherDispatcher<FST extends FeatureStructure> extends MatcherBase<FST> {

	public static <FST extends FeatureStructure> Builder<FST> builder() {
		return new Builder<FST>();
	}

	public static class Builder<FST extends FeatureStructure> {
		private TypeBasedMatcherDispatcher<FST> instance = new TypeBasedMatcherDispatcher<FST>();

		public Builder<FST> addSubmatcher(Type t, Matcher<FST> m) {
			instance.type2matcher.put(t, m);
			return this;
		}

		Builder() {
			instance.type2matcher = Maps.newHashMap();
		}

		public TypeBasedMatcherDispatcher<FST> build() {
			instance.type2matcher = ImmutableMap.copyOf(instance.type2matcher);
			return instance;
		}
	}

	private Map<Type, Matcher<FST>> type2matcher;

	private TypeBasedMatcherDispatcher() {
	}

	public Set<Type> getRegisteredTypes() {
		return ImmutableSet.copyOf(type2matcher.keySet());
	}

	@Override
	public boolean match(FST ref, FST cand) {
		Matcher<FST> submatcher = getSubmatcher(ref);
		return submatcher.match(ref, cand);
	}

	@Override
	public void print(StringBuilder out, FST value) {
		getSubmatcher(value).print(out, value);
	}

	private Matcher<FST> getSubmatcher(FST ref) {
		Type refType = ref.getType();
		Matcher<FST> submatcher = type2matcher.get(refType);
		if (submatcher == null) {
			throw new IllegalStateException(String.format(
					"There is no submatcher for type %s", refType));
		}
		return submatcher;
	}

	@Override
	protected String toString(IdentityHashMap<Matcher<?>, Integer> idMap) {
		if (type2matcher == null) {
			return "EmptyTypeBasedMatcherDispatcher";
		} else {
			idMap.put(this, getNextId(idMap));
			StringBuilder sb = new StringBuilder("[");
			Iterator<Type> typesIter = type2matcher.keySet().iterator();
			while (typesIter.hasNext()) {
				Type t = typesIter.next();
				sb.append(t).append(" => ");
				sb.append(getToString(idMap, type2matcher.get(t)));
				if (typesIter.hasNext())
					sb.append(" || ");
			}
			sb.append("]");
			return sb.toString();
		}
	}

	@Override
	protected Collection<Matcher<?>> getSubMatchers() {
		Collection<Matcher<?>> result = new LinkedList<Matcher<?>>();
		result.addAll(type2matcher.values());
		return result;
	}
}
