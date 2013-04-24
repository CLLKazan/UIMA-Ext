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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.nlplab.brat.configuration.BratEventType;
import org.nlplab.brat.configuration.BratRelationType;
import org.nlplab.brat.configuration.BratTypesConfiguration;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class BratAnnotationContainer {

	public static final String ENTITY_ID_PREFIX = "T";
	public static final String RELATION_ID_PREFIX = "R";
	public static final String EVENT_ID_PREFIX = "E";

	private MutableInt entityIdCounter = new MutableInt(0);
	private MutableInt relationIdCounter = new MutableInt(0);
	private MutableInt eventIdCounter = new MutableInt(0);
	private Map<String, BratAnnotation<?>> id2Anno = Maps.newHashMap();

	public <B extends BratAnnotation<?>> B add(B anno) {
		MutableInt counter = getCounterForTypeOf(anno);
		String idPrefix = getPrefixForTypeOf(anno);
		counter.increment();
		// assign id
		anno.setNumId(counter.longValue());
		anno.setId(idPrefix + counter);
		// memorize
		id2Anno.put(anno.getId(), anno);
		return anno;
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
	}

	public void readFrom(BratTypesConfiguration typesCfg, Reader srcReader) throws IOException {
		BufferedReader reader = new BufferedReader(srcReader);
		String line;
		Map<String, BratEventTrigger> eventTriggers = Maps.newHashMap();
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.isEmpty()) {
				continue;
			}
			if (line.startsWith(ENTITY_ID_PREFIX)) {
				BratTextBoundAnnotation<?> textSpan = parseTextBoundAnnotation(line, typesCfg);
				if (textSpan instanceof BratEntity) {
					add(textSpan);
				} else if (textSpan instanceof BratEventTrigger) {
					eventTriggers.put(textSpan.getId(), (BratEventTrigger) textSpan);
				} else {
					throw new UnsupportedOperationException(String.format(
							"Unknown BratTextBoundAnnotation subtype: %s", textSpan));
				}
			} else if (line.startsWith(RELATION_ID_PREFIX)) {
				BratRelation rel = parseRelation(line, typesCfg);
				add(rel);
			} else if (line.startsWith(EVENT_ID_PREFIX)) {
				BratEvent event = parseEvent(line, typesCfg, eventTriggers);
				add(event);
			} else {
				throw new UnsupportedOperationException(String.format(
						"Can't parse line:\n%s", line));
			}
		}
	}

	private BratTextBoundAnnotation<?> parseTextBoundAnnotation(String str,
			BratTypesConfiguration typesCfg) {
		
	}

	private void appendRoleValues(StringBuilder target,
			Map<String, ? extends BratAnnotation<?>> roleAnnotations) {
		Iterator<String> roleNamesIter = roleAnnotations.keySet().iterator();
		while (roleNamesIter.hasNext()) {
			String roleName = roleNamesIter.next();
			BratAnnotation<?> rv = roleAnnotations.get(roleName);
			target.append(roleName).append(':').append(rv.getId());
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