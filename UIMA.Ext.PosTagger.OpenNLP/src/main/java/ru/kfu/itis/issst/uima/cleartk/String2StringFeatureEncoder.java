/**
 * 
 */
package ru.kfu.itis.issst.uima.cleartk;

import java.util.List;

import org.cleartk.classifier.Feature;
import org.cleartk.classifier.encoder.CleartkEncoderException;
import org.cleartk.classifier.encoder.features.FeatureEncoder;

import com.google.common.collect.ImmutableList;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class String2StringFeatureEncoder implements FeatureEncoder<String> {

	private static final long serialVersionUID = 7109677262312932616L;

	public static final char VALUE_DELIMITER = '=';

	@Override
	public List<String> encode(Feature feature) throws CleartkEncoderException {
		String result = new StringBuilder(feature.getName())
				.append(VALUE_DELIMITER)
				.append(feature.getValue())
				.toString();
		return ImmutableList.of(result);
	}

	@Override
	public boolean encodes(Feature feature) {
		return feature.getValue() instanceof String;
	}

}
