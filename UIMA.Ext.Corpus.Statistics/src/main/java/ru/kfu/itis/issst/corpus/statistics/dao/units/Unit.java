package ru.kfu.itis.issst.corpus.statistics.dao.units;

import java.net.URI;
import java.util.SortedMap;

import com.google.common.collect.Maps;

public class Unit {

	private UnitLocation location;
	private SortedMap<String, String> classByAnnotatorId = Maps.newTreeMap();

	public Unit(URI documentURI, int begin, int end) {
		location = new UnitLocation(documentURI, begin, end);
	}

	public Unit(URI documentURI, int begin, int end, String annotatorId,
			String annotatorClass) {
		this(documentURI, begin, end);
		classByAnnotatorId.put(annotatorId, annotatorClass);
	}

	public UnitLocation getLocation() {
		return location;
	}

	public URI getDocumentURI() {
		return location.getDocumentURI();
	}

	public int getBegin() {
		return location.getBegin();
	}

	public int getEnd() {
		return location.getEnd();
	}

	public String[] getSortedClasses() {
		return classByAnnotatorId.values().toArray(new String[0]);
	}

	public void putClassByAnnotatorId(String annotatorId, String annotatorClass) {
		classByAnnotatorId.put(annotatorId, annotatorClass);
	}

}
