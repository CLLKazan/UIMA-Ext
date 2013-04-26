/**
 * 
 */
package org.nlplab.brat.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.nlplab.brat.BratConstants;
import org.nlplab.brat.configuration.EventRole.Cardinality;
import org.nlplab.brat.util.StringParser;

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

	private static final Map<Cardinality, String> cardinalityStrings =
			ImmutableMap.<Cardinality, String> builder()
					.put(Cardinality.ONE, "")
					.put(Cardinality.OPTIONAL, "?")
					.put(Cardinality.ARRAY, "*")
					.put(Cardinality.NON_EMPTY_ARRAY, "+").build();

	private static String toPrint(Cardinality c) {
		String result = cardinalityStrings.get(c);
		if (result == null) {
			throw new UnsupportedOperationException();
		}
		return result;
	}

	private static Cardinality parseCardinality(String str) {
		if (str == null) {
			return Cardinality.ONE;
		}
		for (Cardinality c : cardinalityStrings.keySet()) {
			String cStr = cardinalityStrings.get(c);
			if (cStr.equals(str)) {
				return c;
			}
		}
		throw new IllegalArgumentException("Unknown cardinality string: " + str);
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
			return addEntityType(typeName, null);
		}

		public BratEntityType addEntityType(String typeName, String parentTypeName) {
			BratEntityType parentType;
			if (parentTypeName == null) {
				parentType = null;
			} else {
				parentType = instance.getType(parentTypeName, BratEntityType.class);
			}
			BratEntityType bet = new BratEntityType(typeName, parentType);
			addType(bet);
			return bet;
		}

		/*
		 * Note, that Map entry ordering is crucial!
		 */
		public BratRelationType addRelationType(String typeName,
				Map<String, String> argTypeNames) {
			Iterator<String> argNameIter = argTypeNames.keySet().iterator();
			String arg1Name = argNameIter.next();
			BratEntityType arg1Type = instance.getType(
					argTypeNames.get(arg1Name), BratEntityType.class);
			String arg2Name = argNameIter.next();
			BratEntityType arg2Type = instance.getType(
					argTypeNames.get(arg2Name), BratEntityType.class);
			if (argNameIter.hasNext()) {
				throw new IllegalStateException(String.format(
						"Too much arguments for relation %s: %s",
						typeName, argTypeNames));
			}
			BratRelationType brt = new BratRelationType(typeName,
					ImmutableSet.of(arg1Type), arg1Name,
					ImmutableSet.of(arg2Type), arg2Name);
			addType(brt);
			return brt;
		}

		public BratEventType addEventType(String typeName, Map<String, String> roleTypeNames) {
			Map<String, Cardinality> roleCardinalities = Maps.newHashMapWithExpectedSize(
					roleTypeNames.size());
			for (String roleName : roleTypeNames.keySet()) {
				roleCardinalities.put(roleName, Cardinality.OPTIONAL);
			}
			return addEventType(typeName, roleTypeNames, roleCardinalities);
		}

		public BratEventType addEventType(String typeName,
				Map<String, String> roleTypeNames,
				Map<String, Cardinality> roleCardinalities) {
			Map<String, EventRole> roles = Maps.newHashMapWithExpectedSize(roleTypeNames.size());
			// check that role ranges are either Entity or Event
			for (String roleName : roleTypeNames.keySet()) {
				BratType roleRange = instance.getType(roleTypeNames.get(roleName));
				if (!(roleRange instanceof BratEntityType) && !(roleRange instanceof BratEventType)) {
					throw new IllegalArgumentException(String.format(
							"Illegal event role range type: %s", roleRange));
				}
				Cardinality roleCard = roleCardinalities.get(roleName);
				roles.put(roleName, new EventRole(roleName, roleRange, roleCard));
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

	public static BratTypesConfiguration readFrom(File confFile) throws IOException {
		InputStream is = new FileInputStream(confFile);
		Reader reader = new InputStreamReader(is, BratConstants.ANNOTATION_CONF_ENCODING);
		try {
			return readFrom(reader);
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}

	public static BratTypesConfiguration readFrom(Reader srcReader) throws IOException {
		BufferedReader reader = new BufferedReader(srcReader);
		String line;
		ConfigurationFileSection curSection = null;
		Builder builder = builder();
		// map that memorizes inheritance chain of last parsed entity type
		Map<Integer, BratEntityType> entitiesHierBranch = null;
		while ((line = reader.readLine()) != null) {
			// skip empty lines
			if (StringUtils.isBlank(line)) {
				continue;
			}
			// skip comment lines
			if (line.startsWith("#")) {
				continue;
			}
			ConfigurationFileSection nextSectionHeader = parseSectionHeader(line);
			if (nextSectionHeader != null) {
				curSection = nextSectionHeader;
				// clean prev section state objects
				entitiesHierBranch = Maps.newTreeMap();
			} else if (curSection == null) {
				cantParse(line);
			} else { // here curSection != null & line is not a declaration of next section
				switch (curSection) {
				case ENTITIES:
					parseEntityLine(builder, line, entitiesHierBranch);
					break;
				case RELATIONS:
					parseRelationLine(builder, line);
					break;
				case EVENTS:
					parseEventLine(builder, line);
					break;
				case ATTRIBUTES:
					// TODO LOW: handle [attributes] section line
					// do nothing
					break;
				default:
					throw new UnsupportedOperationException();
				}
			}
		}
		return builder.build();
	}

	private static final Pattern TYPE_NAME_PATTERN = Pattern.compile("[^\\s]+");
	private static final Pattern SPACE_PATTERN = Pattern.compile("\\s+");
	private static final Pattern OPTIONAL_TABS_PATTERN = Pattern.compile("\\t*");
	private static final Pattern ROLE_DEF_PATTERN = Pattern
			.compile("([-_\\p{Alnum}]+)([?+*]?):([^,\\s]+)");
	private static final Pattern ROLE_SEP_PATTERN = Pattern.compile("\\s*,\\s*");

	private static void parseEntityLine(Builder b, String line,
			Map<Integer, BratEntityType> hierBranch) {
		StringParser p = new StringParser(line);
		String tabs = p.consume1(OPTIONAL_TABS_PATTERN);
		int inheritanceDepth = getPrefixCharNum('\t', tabs);
		String parentTypeName;
		if (inheritanceDepth == 0) {
			parentTypeName = null;
		} else {
			int parentDepth = inheritanceDepth - 1;
			BratEntityType parentType = hierBranch.get(parentDepth);
			if (parentType == null) {
				throw new IllegalStateException(String.format(
						"Can't find parent entity type [inheritanceDepth=%s] for line:\n%s",
						parentDepth, line));
			}
			parentTypeName = parentType.getName();
		}
		String typeName = p.consume1(TYPE_NAME_PATTERN);
		p.ensureBlank();
		BratEntityType type = b.addEntityType(typeName, parentTypeName);
		hierBranch.put(inheritanceDepth, type);
	}

	private static void parseRelationLine(Builder b, String line) {
		StringParser p = new StringParser(line);
		String typeName = p.consume1(TYPE_NAME_PATTERN);
		p.skip(SPACE_PATTERN);
		Map<String, String> argTypeNames = Maps.newLinkedHashMap();
		String[] argDef = parseRelationArgDef(p, line);
		argTypeNames.put(argDef[1], argDef[3]);
		p.skip(ROLE_SEP_PATTERN);
		argDef = parseRelationArgDef(p, line);
		argTypeNames.put(argDef[1], argDef[3]);
		p.ensureBlank();
		b.addRelationType(typeName, argTypeNames);
	}

	private static String[] parseRelationArgDef(StringParser p, String line) {
		String[] argDef = p.consume(ROLE_DEF_PATTERN);
		if (!StringUtils.isBlank(argDef[2])) {
			throw new IllegalStateException(String.format(
					"Illegal relation arg quantifier in:\n%s", line));
		}
		return argDef;
	}

	private static void parseEventLine(Builder b, String line) {
		StringParser p = new StringParser(line);
		String typeName = p.consume1(TYPE_NAME_PATTERN);
		p.skip(SPACE_PATTERN);
		Map<String, String> roleTypeNames = Maps.newLinkedHashMap();
		Map<String, Cardinality> roleCardinalities = Maps.newHashMap();
		// parse first role definition
		parseEventRoleDef(p, roleTypeNames, roleCardinalities);
		// parse remaining role definitions
		while (!StringUtils.isBlank(p.getCurrentString())) {
			p.skip(ROLE_SEP_PATTERN);
			parseEventRoleDef(p, roleTypeNames, roleCardinalities);
		}
		b.addEventType(typeName, roleTypeNames, roleCardinalities);
	}

	private static void parseEventRoleDef(StringParser p, Map<String, String> roleTypeNames,
			Map<String, Cardinality> roleCardinalities) {
		String roleDef[] = p.consume(ROLE_DEF_PATTERN);
		String roleName = roleDef[1];
		roleTypeNames.put(roleName, roleDef[3]);
		roleCardinalities.put(roleName, parseCardinality(roleDef[2]));
	}

	private static int getPrefixCharNum(char ch, String str) {
		int result = 0;
		int i = 0;
		while (i < str.length() && str.charAt(i) == ch)
			result++;
		return result;
	}

	private static void cantParse(String line) {
		throw new IllegalStateException(String.format(
				"Can't parse line:\n%s", line));
	}

	private static enum ConfigurationFileSection {
		ENTITIES("entities"), RELATIONS("relations"),
		EVENTS("events"), ATTRIBUTES("attributes");

		private final String sectionName;

		private ConfigurationFileSection(String sectionName) {
			this.sectionName = sectionName;
		}
	}

	private static ConfigurationFileSection parseSectionHeader(String str) {
		str = str.trim();
		if (!str.startsWith("[") || !str.endsWith("]")) {
			return null;
		}
		String sectionName = str.substring(1, str.length() - 1);
		sectionName = sectionName.trim();
		ConfigurationFileSection result = null;
		for (ConfigurationFileSection cfs : ConfigurationFileSection.values()) {
			if (cfs.sectionName.equals(sectionName)) {
				result = cfs;
				break;
			}
		}
		if (result == null) {
			throw new IllegalArgumentException(String.format(
					"Illegal section header: %s", str));
		}
		return result;
	}
}