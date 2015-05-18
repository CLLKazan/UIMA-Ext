package ru.kfu.itis.issst.cleartk;

import com.google.common.base.Function;
import org.cleartk.ml.Feature;

/**
 * @author Rinat Gareev
 */
public class FeatureUtils {

    public static Function<Feature, String> featureAsStringFunc(final char valueSep) {
        return new Function<Feature, String>() {
            @Override
            public String apply(Feature f) {
                if (f.getName() == null)
                    return String.valueOf(f.getValue());
                return f.getName() + valueSep + f.getValue();
            }
        };
    }

    private FeatureUtils() {
    }
}
