package ru.kfu.itis.issst.uima.ml;

import org.cleartk.ml.Feature;
import org.cleartk.ml.feature.extractor.FeatureExtractor1;

import java.util.List;
import java.util.Set;

/**
 * @author Rinat Gareev
 */
public interface FeatureSet {
    /**
     * @param newVals
     * @param src source of newVals
     */
    void add(List<Feature> newVals, FeatureExtractor1 src);

    /**
     * @return a fresh list instance that represents current values of this feature set.
     */
    List<Feature> toList();

    /**
     * Remove feature values that has been extracted by the specified features extractors.
     * @param aSources
     */
    void removeFeaturesBySource(Set<FeatureExtractor1> aSources);

    /**
     * Remove feature values that has been extracted by the specified feature extractor.
     * @param aSource
     */
    void removeFeaturesBySource(FeatureExtractor1 aSource);
}
