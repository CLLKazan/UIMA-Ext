/**
 * 
 */
package ru.kfu.itis.cll.uima.util;

import java.util.Comparator;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class Counter<K> {

	public static <K> Counter<K> create(K key, int initialValue) {
		return new Counter<K>(key, initialValue);
	}

	public static <K> Counter<K> create(K key) {
		return create(key, 0);
	}

	public static <K> Comparator<Counter<K>> valueComparator(Class<? extends K> keyClass) {
		return new Comparator<Counter<K>>() {
			@Override
			public int compare(Counter<K> arg1, Counter<K> arg2) {
				if (arg1.value > arg2.value) {
					return 1;
				} else if (arg1.value == arg2.value) {
					return 0;
				} else {
					return -1;
				}
			}
		};
	}

	private final K key;
	private int value;

	private Counter(K key, int initialValue) {
		this.key = key;
		this.value = initialValue;
	}

	public void increment() {
		this.value++;
	}

	public K getKey() {
		return key;
	}

	public int getValue() {
		return value;
	}
}
