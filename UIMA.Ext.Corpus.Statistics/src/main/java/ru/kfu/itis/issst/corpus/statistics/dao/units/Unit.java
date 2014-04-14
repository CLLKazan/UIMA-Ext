package ru.kfu.itis.issst.corpus.statistics.dao.units;

import java.net.URI;
import java.util.Map;
import java.util.SortedMap;

import com.google.common.collect.Maps;

public class Unit {

	private URI documentURI;
	private int begin;
	private int end;
	private SortedMap<String, String> classByAnnotatorId = Maps.newTreeMap();

	public Unit(URI documentURI, int begin, int end) {
		this.documentURI = documentURI;
		this.begin = begin;
		this.end = end;
	}

	public Unit(URI documentURI, int begin, int end, String annotatorId,
			String annotatorClass) {
		this.documentURI = documentURI;
		this.begin = begin;
		this.end = end;
		classByAnnotatorId.put(annotatorId, annotatorClass);
	}

	public URI getDocumentURI() {
		return documentURI;
	}

	public int getBegin() {
		return begin;
	}

	public int getEnd() {
		return end;
	}

	public Map<String, String> getClassesByAnnotatorId() {
		return classByAnnotatorId;
	}

	public String[] getSortedClasses() {
		return classByAnnotatorId.values().toArray(new String[0]);
	}

	public String getClassByAnnotatorId(String annotatorId) {
		return classByAnnotatorId.get(annotatorId);
	}

	public void putClassByAnnotatorId(String annotatorId, String annotatorClass) {
		classByAnnotatorId.put(annotatorId, annotatorClass);
	}

}
