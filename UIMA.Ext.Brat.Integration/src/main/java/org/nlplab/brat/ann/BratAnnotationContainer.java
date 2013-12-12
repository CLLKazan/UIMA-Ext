/**
 * 
 */
package org.nlplab.brat.ann;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.nlplab.brat.configuration.BratEntityType;
import org.nlplab.brat.configuration.BratEventType;
import org.nlplab.brat.configuration.BratNoteType;
import org.nlplab.brat.configuration.BratRelationType;
import org.nlplab.brat.configuration.BratType;
import org.nlplab.brat.configuration.BratTypesConfiguration;
import org.nlplab.brat.util.StringParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class BratAnnotationContainer {

	public static final String ENTITY_ID_PREFIX = "T";
	public static final String RELATION_ID_PREFIX = "R";
	public static final String EVENT_ID_PREFIX = "E";
	public static final String NOTE_ID_PREFIX = "#";
	public static final String ATTRIBUTE_ID_PREFIX = "A";
	public static final String NORMALIZATION_ID_PREFIX = "N";
	public static final String EQUIV_RELATION_PREFIX = "*";

	// configuration fields
	private BratTypesConfiguration typesCfg;
	private Logger log = LoggerFactory.getLogger(getClass());
	// state fields
	private MutableInt entityIdCounter = new MutableInt(0);
	private MutableInt relationIdCounter = new MutableInt(0);
	private MutableInt eventIdCounter = new MutableInt(0);
	private MutableInt noteIdCounter = new MutableInt(0);
	// indexes
	private Map<String, BratAnnotation<?>> id2Anno = Maps.newHashMap();
	private Multimap<String, BratAnnotation<?>> type2Anno = HashMultimap.create();
	// map a note's target annotation id to the note itself
	private Multimap<String, BratNoteAnnotation> targetId2Note = HashMultimap.create();

	public BratAnnotationContainer(BratTypesConfiguration typesCfg) {
		this.typesCfg = typesCfg;
	}

	@SuppressWarnings("unchecked")
	public <T extends BratType, A extends BratAnnotation<T>> Collection<A> getAnnotations(T type) {
		// sanity check
		checkTypeRegistration(type);
		ImmutableList.Builder<A> resultBuilder = ImmutableList.builder();
		for (BratAnnotation<?> bAnno : type2Anno.get(type.getName())) {
			resultBuilder.add((A) bAnno);
		}
		return resultBuilder.build();
	}

	public Collection<BratEntity> getEntities(BratEntityType type) {
		return getAnnotations(type);
	}

	public Collection<BratRelation> getRelations(BratRelationType type) {
		return getAnnotations(type);
	}

	public Collection<BratEvent> getEvents(BratEventType type) {
		return getAnnotations(type);
	}

	public BratAnnotation<?> getAnnotation(String id) {
		return getAnnotation(id, true);
	}

	public BratAnnotation<?> getAnnotation(String id, boolean mustExist) {
		BratAnnotation<?> result = id2Anno.get(id);
		if (result == null && mustExist) {
			throw new IllegalStateException(String.format(
					"There is no annotation with id=%s", id));
		}
		return result;
	}

	public Collection<BratNoteAnnotation> getNotes(BratAnnotation<?> targetAnno) {
		return targetId2Note.get(targetAnno.getId());
	}

	/**
	 * Assign id to given annotation, add it to this container and return it.
	 * 
	 * @param anno
	 * @return given anno with assigned id.
	 */
	public <B extends BratAnnotation<?>> B register(B anno) {
		// sanity check 1
		if (anno.getId() != null) {
			throw new IllegalArgumentException(String.format(
					"Can not register anno %s with non-null id", anno.getId()));
		}
		// sanity check 2
		checkTypeRegistration(anno.getType());
		// calc id
		MutableInt counter = getCounterForTypeOf(anno);
		String idPrefix = getPrefixForTypeOf(anno);
		counter.increment();
		// assign id
		anno.setNumId(counter.longValue());
		anno.setId(idPrefix + counter);
		// memorize
		add(anno);
		return anno;
	}

	private void checkTypeRegistration(BratType t) {
		BratType registeredType = typesCfg.getType(t.getName());
		if (registeredType != t) {
			throw new IllegalStateException(String.format(
					"Type %s is different from registered one: %s",
					t, registeredType));
		}
	}

	/**
	 * Add given anno to indexes. Assume that annotation has been assigned ID.
	 * 
	 * @param anno
	 */
	private void add(BratAnnotation<?> anno) {
		String annoId = anno.getId();
		if (annoId == null) {
			throw new NullPointerException();
		}
		if (id2Anno.containsKey(anno.getId())) {
			throw new IllegalStateException(String.format(
					"Duplicate annotation id: %s", anno.getId()));
		}
		id2Anno.put(anno.getId(), anno);
		// TODO what about parent types if any?
		type2Anno.put(anno.getType().getName(), anno);
		// modify type-specific indexes
		if (anno instanceof BratNoteAnnotation) {
			BratNoteAnnotation note = (BratNoteAnnotation) anno;
			targetId2Note.put(note.getTargetAnnotation().getId(), note);
		}
	}

	private MutableInt getCounterForTypeOf(BratAnnotation<?> anno) {
		if (anno instanceof BratEntity || anno instanceof BratEventTrigger) {
			return entityIdCounter;
		}
		if (anno instanceof BratRelation) {
			return relationIdCounter;
		}
		if (anno instanceof BratEvent) {
			return eventIdCounter;
		}
		if (anno instanceof BratNoteAnnotation) {
			return noteIdCounter;
		}
		throw new IllegalArgumentException("Unknown type of instance: " + anno);
	}

	private String getPrefixForTypeOf(BratAnnotation<?> anno) {
		if (anno instanceof BratEntity || anno instanceof BratEventTrigger) {
			return ENTITY_ID_PREFIX;
		}
		if (anno instanceof BratRelation) {
			return RELATION_ID_PREFIX;
		}
		if (anno instanceof BratEvent) {
			return EVENT_ID_PREFIX;
		}
		if (anno instanceof BratNoteAnnotation) {
			return NOTE_ID_PREFIX;
		}
		throw new IllegalArgumentException("Unknown type of instance: " + anno);
	}

	public void writeTo(Writer writer) {
		PrintWriter out = new PrintWriter(writer, true);
		// write entities
		List<BratEntity> entities = getSortedByType(BratEntity.class);
		for (BratEntity e : entities) {
			String eStr = makeTextBoundAnnotationString(e);
			out.println(eStr);
		}
		// write relations
		List<BratRelation> relations = getSortedByType(BratRelation.class);
		for (BratRelation r : relations) {
			StringBuilder sb = new StringBuilder(r.getId());
			sb.append('\t');
			BratRelationType rt = r.getType();
			sb.append(rt.getName()).append(' ');
			appendRoleValues(sb, r.getRoleAnnotations());
			out.println(sb);
		}
		// write events
		List<BratEvent> events = getSortedByType(BratEvent.class);
		for (BratEvent e : events) {
			BratEventType et = e.getType();
			// write trigger line
			BratEventTrigger trig = e.getTrigger();
			String trigStr = makeTextBoundAnnotationString(trig);
			out.println(trigStr);
			// write roles line
			StringBuilder sb = new StringBuilder(e.getId());
			sb.append('\t');
			sb.append(et.getName()).append(':').append(trig.getId()).append(' ');
			appendRoleValues(sb, e.getRoleAnnotations());
			out.println(sb);
		}
		// write notes
		List<BratNoteAnnotation> notes = getSortedByType(BratNoteAnnotation.class);
		for (BratNoteAnnotation n : notes) {
			BratNoteType nt = n.getType();
			StringBuilder sb = new StringBuilder(n.getId());
			sb.append('\t');
			sb.append(nt.getName()).append(' ').append(n.getTargetAnnotation().getId());
			sb.append('\t');
			sb.append(escapeAnnotationSpannedText(n.getContent()));
			out.println(sb);
		}
	}

	public void readFrom(Reader srcReader) throws IOException {
		BufferedReader reader = new BufferedReader(srcReader);
		String line;
		Map<String, BratEventTrigger> eventTriggers = Maps.newHashMap();
		// because a brat structure annotation may refer to entity (or trigger)
		// described in next lines reading is done in two passes
		// first pass - read entity lines
		List<String> unreadLines = Lists.newLinkedList();
		while ((line = reader.readLine()) != null) {
			if (StringUtils.isBlank(line)) {
				continue;
			}
			if (line.startsWith(ENTITY_ID_PREFIX)) {
				BratTextBoundAnnotation<?> textSpan = parseTextBoundAnnotation(line);
				if (textSpan instanceof BratEntity) {
					add(textSpan);
				} else if (textSpan instanceof BratEventTrigger) {
					eventTriggers.put(textSpan.getId(), (BratEventTrigger) textSpan);
				} else {
					throw new UnsupportedOperationException(String.format(
							"Unknown BratTextBoundAnnotation subtype: %s", textSpan));
				}
			} else {
				unreadLines.add(line);
			}
		}
		// second pass - read other lines
		Iterator<String> lineIter = unreadLines.iterator();
		while (lineIter.hasNext()) {
			line = lineIter.next();
			if (line.startsWith(RELATION_ID_PREFIX)) {
				BratRelation rel = parseRelation(line);
				add(rel);
			} else if (line.startsWith(EVENT_ID_PREFIX)) {
				BratEvent event = parseEvent(line, eventTriggers);
				add(event);
			} else if (line.startsWith(NOTE_ID_PREFIX)) {
				BratNoteAnnotation note = parseNote(line);
				add(note);
			} else if (line.startsWith(ATTRIBUTE_ID_PREFIX)) {
				// TODO parse attribute lines when attributes are supported
				// just ignore now
			} else if (line.startsWith(NORMALIZATION_ID_PREFIX)) {
				// TODO parse normalization line
				// just ignore now
			} else if (line.startsWith(EQUIV_RELATION_PREFIX)) {
				// TODO parse relation instances
				log.warn("The following line contains relation instances " +
						"but its handling is not implemented yet:\n{}",
						line);
				// just ignore now
			} else {
				throw new UnsupportedOperationException(String.format(
						"Can't parse line:\n%s", line));
			}
		}
	}

	private static final Pattern ID_PATTERN = Pattern.compile("[#\\w]\\d+");
	private static final Pattern TAB_PATTERN = Pattern.compile("\\t");
	private static final Pattern SPACE_PATTERN = Pattern.compile("\\s+");
	private static final Pattern TYPE_NAME_PATTERN = Pattern.compile("[^\\s]+");
	private static final Pattern OFFSETS_PATTERN = Pattern.compile("(\\d+)\\s+(\\d+)");
	private static final Pattern ROLE_VALUE_PATTERN = Pattern.compile("([^:]+):(\\w\\d+)");

	private BratTextBoundAnnotation<?> parseTextBoundAnnotation(String str) {
		StringParser p = new StringParser(str);
		String id = p.consume1(ID_PATTERN);
		assert id.startsWith(ENTITY_ID_PREFIX);
		p.skip(TAB_PATTERN);
		String typeName = p.consume1(TYPE_NAME_PATTERN);
		p.skip(SPACE_PATTERN);
		String[] offsetStrings = p.consume(OFFSETS_PATTERN);
		Integer begin = Integer.valueOf(offsetStrings[1]);
		Integer end = Integer.valueOf(offsetStrings[2]);
		p.skip(TAB_PATTERN);
		String refText = p.getCurrentString();

		BratType type = typesCfg.getType(typeName);
		BratTextBoundAnnotation<?> result;
		if (type instanceof BratEntityType) {
			result = new BratEntity((BratEntityType) type, begin, end, refText);
		} else if (type instanceof BratEventType) {
			result = new BratEventTrigger((BratEventType) type, begin, end, refText);
		} else {
			throw new IllegalStateException(String.format(
					"Unexpected type of annotation %s", str));
		}
		result.setId(id);
		return result;
	}

	private BratRelation parseRelation(String str) {
		StringParser p = new StringParser(str);
		String id = p.consume1(ID_PATTERN);
		assert id.startsWith(RELATION_ID_PREFIX);
		p.skip(TAB_PATTERN);
		String typeName = p.consume1(TYPE_NAME_PATTERN);
		p.skip(SPACE_PATTERN);
		String[] arg1rv = p.consume(ROLE_VALUE_PATTERN);
		p.skip(SPACE_PATTERN);
		String[] arg2rv = p.consume(ROLE_VALUE_PATTERN);
		p.ensureBlank();

		BratRelationType type = typesCfg.getType(typeName, BratRelationType.class);
		Map<String, BratAnnotation<?>> argMap = Maps.newHashMap();
		argMap.put(arg1rv[1].trim(), getAnnotation(arg1rv[2]));
		argMap.put(arg2rv[1].trim(), getAnnotation(arg2rv[2]));
		BratRelation relation = new BratRelation(type, argMap);
		relation.setId(id);
		return relation;
	}

	private BratEvent parseEvent(final String str, Map<String, BratEventTrigger> eventTriggers) {
		StringParser p = new StringParser(str);
		String id = p.consume1(ID_PATTERN);
		assert id.startsWith(EVENT_ID_PREFIX);
		p.skip(TAB_PATTERN);
		String[] triggerRef = p.consume(ROLE_VALUE_PATTERN);
		String typeName = triggerRef[1];
		BratEventType type = typesCfg.getType(typeName, BratEventType.class);
		String triggerId = triggerRef[2];
		p.skip(SPACE_PATTERN);
		Multimap<String, BratAnnotation<?>> roleMap = LinkedHashMultimap.create();
		while (!StringUtils.isBlank(p.getCurrentString())) {
			String rv[] = p.consume(ROLE_VALUE_PATTERN);
			String roleName = rv[1].trim();
			int rvIndex;
			Object[] roleIndex = parseRoleIndex(roleName);
			if (roleIndex != null) {
				roleName = (String) roleIndex[0];
				rvIndex = (Integer) roleIndex[1];
			} else {
				rvIndex = 1;
			}
			// validate rvIndex
			if (roleMap.get(roleName).size() != rvIndex - 1) {
				throw new IllegalStateException(String.format(
						"Illegal value indices for role '%s' in:\n%s",
						roleName, str));
			}
			roleMap.put(roleName, getAnnotation(rv[2]));
		}
		BratEventTrigger trigger = eventTriggers.get(triggerId);
		if (trigger == null) {
			throw new IllegalStateException(String.format(
					"Can't find trigger with id '%s' for event line:\n%s",
					triggerId, str));
		}
		BratEvent event = new BratEvent(type, trigger, roleMap);
		event.setId(id);
		return event;
	}

	private static final Pattern digitsPattern = Pattern.compile("\\d+$");

	static Object[] parseRoleIndex(String str) {
		Matcher m = digitsPattern.matcher(str);
		if (m.find()) {
			String roleName = str.substring(0, m.start());
			String indexStr = m.group();
			Integer index = Integer.valueOf(indexStr);
			return new Object[] { roleName, index };
		} else {
			return null;
		}
	}

	private BratNoteAnnotation parseNote(String str) {
		StringParser p = new StringParser(str);
		String id = p.consume1(ID_PATTERN);
		assert id.startsWith(NOTE_ID_PREFIX);
		p.skip(TAB_PATTERN);
		String typeName = p.consume1(TYPE_NAME_PATTERN);
		p.skip(SPACE_PATTERN);
		String targetId = p.consume1(ID_PATTERN);
		p.skip(TAB_PATTERN);
		String content = p.getCurrentString();

		BratAnnotation<?> targetAnno = getAnnotation(targetId);
		BratNoteType type = typesCfg.getType(typeName, BratNoteType.class);
		BratNoteAnnotation note = new BratNoteAnnotation(type, targetAnno, content);
		note.setId(id);
		return note;
	}

	private void appendRoleValues(StringBuilder target,
			Multimap<String, ? extends BratAnnotation<?>> roleAnnotations) {
		Iterator<String> roleNamesIter = roleAnnotations.keySet().iterator();
		while (roleNamesIter.hasNext()) {
			String roleName = roleNamesIter.next();
			Collection<? extends BratAnnotation<?>> rvs = roleAnnotations.get(roleName);
			int i = 1;
			for (BratAnnotation<?> rv : rvs) {
				if (i != 1) {
					target.append(' ');
				}
				target.append(roleName);
				if (i > 1) {
					target.append(i);
				}
				target.append(':').append(rv.getId());
				i++;
			}
			if (roleNamesIter.hasNext()) {
				target.append(' ');
			}
		}
	}

	private String makeTextBoundAnnotationString(BratTextBoundAnnotation<?> e) {
		StringBuilder sb = new StringBuilder(e.getId());
		sb.append('\t');
		sb.append(e.getType().getName())
				.append(' ').append(e.getBegin())
				.append(' ').append(e.getEnd());
		sb.append('\t');
		sb.append(escapeAnnotationSpannedText(e.getSpannedText()));
		return sb.toString();
	}

	private static String escapeAnnotationSpannedText(String txt) {
		return StringUtils.replaceChars(txt, "\n\r\t", "   ");
	}

	private <B extends BratAnnotation<?>> List<B> getSortedByType(Class<B> annoClass) {
		ArrayList<B> result = filterByType(annoClass);
		Collections.sort(result, numIdComparator);
		return result;
	}

	@SuppressWarnings("unchecked")
	private <B extends BratAnnotation<?>> ArrayList<B> filterByType(Class<B> annoClass) {
		ArrayList<B> result = Lists.newArrayList();
		for (BratAnnotation<?> anno : id2Anno.values()) {
			if (annoClass.isInstance(anno)) {
				result.add((B) anno);
			}
		}
		return result;
	}

	private static Comparator<BratAnnotation<?>> numIdComparator = new Comparator<BratAnnotation<?>>() {
		@Override
		public int compare(BratAnnotation<?> o1, BratAnnotation<?> o2) {
			return Long.valueOf(o1.getNumId()).compareTo(o2.getNumId());
		}
	};
}