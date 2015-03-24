/**
 *
 */
package ru.kfu.itis.issst.cleartk.crfsuite;

import com.google.common.collect.Lists;
import org.cleartk.ml.Feature;
import org.cleartk.ml.encoder.CleartkEncoderException;
import org.cleartk.ml.encoder.FeatureEncoderUtil;
import org.cleartk.ml.encoder.features.FeaturesEncoder_ImplBase;
import ru.kfu.itis.issst.cleartk.SerializableNameNumber;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This is re-implementation of
 * {@link org.cleartk.classifier.encoder.features.NameNumberFeaturesEncoder}
 * that allow to customize escaping of NameNumber name.
 *
 * @author Rinat Gareev (Kazan Federal University)
 */
public class NameNumberFeaturesEncoder2 extends
        FeaturesEncoder_ImplBase<ArrayList<SerializableNameNumber>, SerializableNameNumber> {

    private static final long serialVersionUID = 5091699468972266044L;

    private char[] escapeCharacters;

    public NameNumberFeaturesEncoder2(String escapeCharacters) {
        this.escapeCharacters = escapeCharacters == null
                ? new char[0]
                : escapeCharacters.toCharArray();
    }

    @Override
    public ArrayList<SerializableNameNumber> encodeAll(Iterable<Feature> features) throws CleartkEncoderException {
        ArrayList<SerializableNameNumber> returnValues;
        if (features instanceof Collection) {
            returnValues = Lists.newArrayListWithExpectedSize(((Collection) features).size());
        } else {
            returnValues = Lists.newArrayList();
        }
        for (Feature feature : features) {
            for (SerializableNameNumber nameNumber : this.encode(feature)) {
                nameNumber.name = escape(nameNumber.name);
                if (nameNumber.name != null) {
                    returnValues.add(nameNumber);
                }
            }
        }
        return returnValues;
    }

    protected String escape(String string) {
        if (escapeCharacters.length == 0) {
            return string;
        }
        return FeatureEncoderUtil.escape(string, escapeCharacters);
    }
}
