package ru.kfu.itis.issst.cleartk;

import org.cleartk.ml.Feature;
import org.cleartk.ml.encoder.features.FeatureEncoder;

import java.util.Collections;
import java.util.List;

/**
 * @author Rinat Gareev
 */
public class StringEncoder implements FeatureEncoder<SerializableNameNumber> {
    public List<SerializableNameNumber> encode(Feature feature) {
        StringBuilder buffer = new StringBuilder();
        String name = feature.getName();
        Object value = feature.getValue();
        if (name != null) {
            buffer.append(name);
        }
        if (value != null) {
            if (name != null) {
                buffer.append("_");
            }
            buffer.append(value.toString());
        }

        SerializableNameNumber fve = new SerializableNameNumber(buffer.toString(), 1.0d);
        return Collections.singletonList(fve);
    }

    public boolean encodes(Feature feature) {
        return true;
    }
}
