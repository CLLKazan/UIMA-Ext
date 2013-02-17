/**
 * 
 */
package ru.kfu.itis.cll.uima.cas;

import java.util.Collection;
import java.util.Set;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.StringArrayFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;

import com.google.common.collect.ImmutableSet;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class FSUtils {

	public static final FSArray toFSArray(JCas cas, Collection<? extends FeatureStructure> srcCol) {
		FSArray result = new FSArray(cas, srcCol.size());
		int i = 0;
		for (FeatureStructure fs : srcCol) {
			result.set(i, fs);
			i++;
		}
		return result;
	}

	public static final StringArray toStringArray(JCas cas, Collection<String> srcCol) {
		StringArray result = new StringArray(cas, srcCol.size());
		int i = 0;
		for (String gr : srcCol) {
			result.set(i, gr);
			i++;
		}
		return result;
	}

	public static final Set<String> toSet(StringArrayFS fsArr) {
		if (fsArr == null)
			return ImmutableSet.of();
		ImmutableSet.Builder<String> resultBuilder = ImmutableSet.builder();
		for (int i = 0; i < fsArr.size(); i++) {
			resultBuilder.add(fsArr.get(i));
		}
		return resultBuilder.build();
	}

	private FSUtils() {
	}

}