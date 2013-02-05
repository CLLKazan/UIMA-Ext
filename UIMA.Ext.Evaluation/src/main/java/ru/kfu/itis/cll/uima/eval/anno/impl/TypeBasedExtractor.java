/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.anno.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;

import ru.kfu.itis.cll.uima.eval.EvaluationConfig;
import ru.kfu.itis.cll.uima.eval.anno.AnnotationExtractor;

/**
 * @author Rinat Gareev
 * 
 */
public class TypeBasedExtractor implements AnnotationExtractor {

	private TypeSystem typeSystem;

	private Set<Type> annoTypes;

	private void initTypes(EvaluationConfig config) {
		Set<String> annoTypeNames = config.getAnnoTypes();
		annoTypes = new HashSet<Type>();
		for (String curTypeName : annoTypeNames) {
			Type curType = typeSystem.getType(curTypeName);
			if (curType == null) {
				throw new IllegalStateException("Can't find type " + curTypeName);
			}
			annoTypes.add(curType);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FSIterator<AnnotationFS> extract(CAS cas) {
		// TODO Auto-generated method stub
		return null;
	}

}