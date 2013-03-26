package ru.kfu.itis.cll.uima.eval.matching;

import org.apache.uima.cas.Type;

class MatchingUtils {

	public static boolean isCollectionType(Type type) {
		// TODO what about FSList ?
		return type.isArray();
	}

	public static Type getComponentType(Type colType) {
		if (!isCollectionType(colType)) {
			throw new IllegalArgumentException(String.format(
					"Type %s is not a collection type", colType));
		}
		return colType.getComponentType();
	}

	private MatchingUtils() {
	}
}