/**
 * 
 */
package ru.kfu.itis.issst.uima.cleartk;

import java.util.List;

import org.cleartk.ml.Feature;
import org.cleartk.ml.encoder.CleartkEncoderException;
import org.cleartk.ml.encoder.features.FeatureEncoder;

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
		StringBuilder sb = new StringBuilder();
		if (feature.getName() != null) {
			sb.append(feature.getName()).append(VALUE_DELIMITER);
		}
		sb.append(feature.getValue());
		return ImmutableList.of(sb.toString());
	}

	@Override
	public boolean encodes(Feature feature) {
		return feature.getValue() instanceof String;
	}

}
