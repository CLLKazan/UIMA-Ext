/**
 * 
 */
package org.nlplab.brat.configuration;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.nlplab.brat.configuration.EventRole.Cardinality;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * @author Rinat Gareev
 * 
 */
public class BratTypesConfiguration {

	private Map<String, BratType> name2Type;

	private BratTypesConfiguration() {
	}

	public BratType getType(String name) {
		return getType(name, BratType.class);
	}

	@SuppressWarnings("unchecked")
	public <T extends BratType> T getType(String name, Class<T> expectedTypeClass) {
		BratType t = name2Type.get(name);
		if (t == null) {
			throw new IllegalStateException(String.format(
					"There is no type with name '%s'", name));
		}
		if (!expectedTypeClass.isInstance(t)) {
			throw new IllegalStateException(String.format(
					"Type %s is not of expected class %s", t, expectedTypeClass.getSimpleName()));
		}
		return (T) t;
	}

	public void writeTo(Writer writer) {
		PrintWriter out = new PrintWriter(writer, true);
		out.println("[entities]");

		// build entity hierarchy
		// parent-to-children
		Multimap<BratEntityType, BratEntityType> hier = HashMultimap.create();
		for (BratEntityType bet : getEntityTypes()) {
			hier.put(bet.getParentType(), bet);
		}
		// write entities hierarchy using recursive method
		writeChildrenEntities(out, null, hier, -1);
		// write relations
		out.println();
		out.println("[relations]");
		Joiner argTypesJoiner = Joiner.on('|');
		for (BratRelationType brt : getRelationTypes()) {
			StringBuilder sb = new StringBuilder(brt.getName());
			// first arg
			sb.append('\t');
			sb.append(brt.getArg1Name()).append(':');
			argTypesJoiner.appendTo(sb, Collections2.transform(brt.getArg1Types(), bratTypeToName));
			// arg sep
			sb.append(", ");
			// second arg
			sb.append('\t');
			sb.append(brt.getArg2Name()).append(':');
			argTypesJoiner.appendTo(sb, Collections2.transform(brt.getArg2Types(), bratTypeToName));
			out.println(sb);
		}

		// write events
		out.println();
		out.println("[events]");
		Joiner rolesJoiner = Joiner.on(", ");
		for (BratEventType bet : getEventTypes()) {
			StringBuilder sb = new StringBuilder(bet.getName());
			sb.append("\t");
			rolesJoiner.appendTo(sb,
					Collections2.transform(bet.getRoles().values(), eventRoleToPrint));
			out.println(sb);
		}
	}

	private Collection<BratEntityType> getEntityTypes() {
		return getTypes(BratEntityType.class);
	}

	private Collection<BratRelationType> getRelationTypes() {
		return getTypes(BratRelationType.class);
	}

	private Collection<BratEventType> getEventTypes() {
		return getTypes(BratEventType.class);
	}

	@SuppressWarnings("unchecked")
	private <T extends BratType> Collection<T> getTypes(Class<T> typeClass) {
		return (Collection<T>) Collections2.filter(
				name2Type.values(), Predicates.instanceOf(typeClass));
	}

	private void writeChildrenEntities(PrintWriter out, BratEntityType parent,
			Multimap<BratEntityType, BratEntityType> hier, int parentDepth) {
		int depth = parentDepth + 1;
		for (BratEntityType bet : hier.get(parent)) {
			for (int i = 0; i < depth; i++) {
				out.print('\t');
			}
			out.println(bet.getName());
			writeChildrenEntities(out, bet, hier, depth);
		}
	}

	private static final Function<BratType, String> bratTypeToName = new Function<BratType, String>() {
		@Override
		public String apply(BratType input) {
			return input.getName();
		}
	};

	private static final Function<EventRole, String> eventRoleToPrint = new Function<EventRole, String>() {
		@Override
		public String apply(EventRole input) {
			return String.format("%s%s:%s",
					input.getRole(), toPrint(input.getCardinality()),
					input.getRange().getName());
		}
	};

	private static String toPrint(Cardinality c) {
		switch (c) {
		case ONE:
			return "";
		case OPTIONAL:
			return "?";
		case ARRAY:
			return "*";
		case NON_EMPTY_ARRAY:
			return "+";
		default:
			throw new UnsupportedOperationException(String.valueOf(c));
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private BratTypesConfiguration instance = new BratTypesConfiguration();

		private Builder() {
			instance.name2Type = Maps.newLinkedHashMap();
		}

		public BratEntityType addEntityType(String typeName) {
			BratEntityType bet = new BratEntityType(typeName, null);
			addType(bet);
			return bet;
		}

		/*
		 * Note, that Map entry ordering is crucial!
		 */
		public BratRelationType addRelationType(String typeName,
				Map<String, BratEntityType> argTypes) {
			Iterator<String> argNameIter = argTypes.keySet().iterator();
			String arg1Name = argNameIter.next();
			String arg2Name = argNameIter.next();
			if (argNameIter.hasNext()) {
				throw new IllegalStateException(String.format(
						"Too much arguments for relation %s: %s",
						typeName, argTypes));
			}
			BratRelationType brt = new BratRelationType(typeName,
					ImmutableSet.of(argTypes.get(arg1Name)), arg1Name,
					ImmutableSet.of(argTypes.get(arg2Name)), arg2Name);
			addType(brt);
			return brt;
		}

		public BratEventType addEventType(String typeName, Map<String, BratType> roleRanges) {
			Map<String, EventRole> roles = Maps.newHashMapWithExpectedSize(roleRanges.size());
			// check that role ranges are either Entity or Event
			for (String roleName : roleRanges.keySet()) {
				BratType roleRange = roleRanges.get(roleName);
				if (!(roleRange instanceof BratEntityType) && !(roleRange instanceof BratEventType)) {
					throw new IllegalArgumentException(String.format(
							"Illegal event role range type: %s", roleRange));
				}
				roles.put(roleName, new EventRole(roleName, roleRange, Cardinality.OPTIONAL));
			}
			BratEventType bet = new BratEventType(typeName, roles);
			addType(bet);
			return bet;
		}

		public BratTypesConfiguration build() {
			instance.name2Type = ImmutableMap.copyOf(instance.name2Type);
			return instance;
		}

		private void addType(BratType type) {
			String typeName = type.getName();
			if (instance.name2Type.containsKey(typeName)) {
				throw new IllegalStateException(String.format(
						"Duplicate type name '%s'. Second type: %s", typeName, type));
			}
			instance.name2Type.put(typeName, type);
		}
	}
}