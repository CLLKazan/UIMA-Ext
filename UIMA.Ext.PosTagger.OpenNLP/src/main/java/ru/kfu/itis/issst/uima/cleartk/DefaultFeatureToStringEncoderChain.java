/**
 * 
 */
package ru.kfu.itis.issst.uima.cleartk;

import org.cleartk.classifier.encoder.features.FeatureEncoderChain;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class DefaultFeatureToStringEncoderChain extends FeatureEncoderChain<String> {

	private static final long serialVersionUID = 5915214180458567772L;

	public DefaultFeatureToStringEncoderChain() {
		addEncoder(new Boolean2StringFeatureEncoder());
		addEncoder(new String2StringFeatureEncoder());
	}

}
