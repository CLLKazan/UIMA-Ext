/**
 * 
 */
package ru.kfu.itis.issst.cleartk;

import org.cleartk.ml.SequenceClassifier;
import org.cleartk.ml.SequenceClassifierFactory;

/**
 * Re-implementation of ClearTK-ML {@link SequenceJarClassifierFactory} based on
 * own {@link GenericJarClassifierFactory}
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class JarSequenceClassifierFactory<OUTCOME_TYPE>
		extends GenericJarClassifierFactory<SequenceClassifier<OUTCOME_TYPE>>
		implements SequenceClassifierFactory<OUTCOME_TYPE> {

	@Override
	@SuppressWarnings("unchecked")
	protected Class<SequenceClassifier<OUTCOME_TYPE>> getClassifierClass() {
		return (Class<SequenceClassifier<OUTCOME_TYPE>>) (Class<?>) SequenceClassifier.class;
	}

}
