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
public class Boolean2StringFeatureEncoder implements FeatureEncoder<String> {

	private static final long serialVersionUID = -2489930447901347937L;

	@Override
	public List<String> encode(Feature feature) throws CleartkEncoderException {
		if ((Boolean) feature.getValue()) {
			return ImmutableList.of(feature.getName());
		} else {
			return ImmutableList.of();
		}
	}

	@Override
	public boolean encodes(Feature feature) {
		return feature.getValue() instanceof Boolean;
	}

}
