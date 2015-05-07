/**
 * 
 */
package ru.kfu.itis.cll.uima.io.axml;

import com.beust.jcommander.internal.Maps;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
class Annotation {

	private String type;
	private int begin;
	private int end;
    private Map<String, String> featureStringValuesMap;

	Annotation() {
        featureStringValuesMap = Maps.newHashMap();
    }

	public int getBegin() {
		return begin;
	}

	public int getEnd() {
		return end;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public void setEnd(int end) {
		this.end = end;
	}

    public void setFeatureStringValue(String name, String val) {
        featureStringValuesMap.put(name, val);
    }

    public Set<String> getFeatureNames() {
        return Collections.unmodifiableSet(featureStringValuesMap.keySet());
    }

    public String getFeatureStringValue(String name) {
        return featureStringValuesMap.get(name);
    }
}
