package ru.kfu.itis.issst.uima.ml.fe;

import com.google.common.collect.ImmutableList;
import org.apache.uima.fit.util.ContainmentIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.ml.Feature;
import org.cleartk.ml.feature.extractor.CleartkExtractorException;
import org.cleartk.ml.feature.extractor.FeatureExtractor1;

import java.util.List;

/**
 * @param <T> a token type
 * @author Rinat Gareev
 */
public class AnnotationInsideFeatureExtractor<T extends Annotation, C extends Annotation>
        implements FeatureExtractor1<T>, IndexesJCas {

    public static final String FEATURE_PREFIX = "I_";

    private final Class<C> coveringAnnoClass;
    private final Class<T> tokenClass;
    // derived
    private final String positiveFeatureValue;
    // state fields
    private ContainmentIndex<C, T> contIndex;

    public AnnotationInsideFeatureExtractor(Class<T> tokenClass,
                                            String featureNameSuffix,
                                            Class<C> coveringAnnoClass) {
        this.coveringAnnoClass = coveringAnnoClass;
        this.tokenClass = tokenClass;
        //
        positiveFeatureValue = FEATURE_PREFIX + featureNameSuffix;
    }

    @Override
    public List<Feature> extract(JCas view, T focusAnnotation) {
        if (contIndex.isContainedInAny(focusAnnotation)) {
            return ImmutableList.of(new Feature(positiveFeatureValue));
        } else {
            return ImmutableList.of();
        }
    }

    @Override
    public void onCASChange(JCas newCas) {
        contIndex = null;
        if (newCas != null) {
            contIndex = ContainmentIndex.create(newCas, coveringAnnoClass, tokenClass, ContainmentIndex.Type.REVERSE);
        }
    }
}
