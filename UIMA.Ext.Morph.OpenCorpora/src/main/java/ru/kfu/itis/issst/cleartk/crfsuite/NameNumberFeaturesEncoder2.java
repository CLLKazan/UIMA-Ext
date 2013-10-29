/**
 * 
 */
package ru.kfu.itis.issst.cleartk.crfsuite;

import java.util.ArrayList;
import java.util.List;

import org.cleartk.classifier.Feature;
import org.cleartk.classifier.encoder.CleartkEncoderException;
import org.cleartk.classifier.encoder.FeatureEncoderUtil;
import org.cleartk.classifier.encoder.features.FeaturesEncoder_ImplBase;
import org.cleartk.classifier.encoder.features.NameNumber;

/**
 * This is re-implementation of
 * {@link org.cleartk.classifier.encoder.features.NameNumberFeaturesEncoder}
 * that allow to customize escaping of NameNumber name.
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class NameNumberFeaturesEncoder2 extends
		FeaturesEncoder_ImplBase<List<NameNumber>, NameNumber> {

	private static final long serialVersionUID = 5091699468972266044L;

	private char[] escapeCharacters;

	public NameNumberFeaturesEncoder2(String escapeCharacters) {
		this.escapeCharacters = escapeCharacters == null
				? new char[0]
				: escapeCharacters.toCharArray();
	}

	@Override
	public List<NameNumber> encodeAll(Iterable<Feature> features) throws CleartkEncoderException {
		List<NameNumber> returnValues = new ArrayList<NameNumber>();
		for (Feature feature : features) {
			for (NameNumber nameNumber : this.encode(feature)) {
				nameNumber.name = escape(nameNumber.name);
				if (nameNumber.name != null) {
					returnValues.add(nameNumber);
				}
			}
		}
		return returnValues;
	}

	protected String escape(String string) {
		return FeatureEncoderUtil.escape(string, escapeCharacters);
	}
}
