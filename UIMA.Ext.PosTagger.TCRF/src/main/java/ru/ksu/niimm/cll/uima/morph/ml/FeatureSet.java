package ru.ksu.niimm.cll.uima.morph.ml;

import org.cleartk.classifier.Feature;
import org.cleartk.classifier.feature.extractor.simple.SimpleFeatureExtractor;

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
    void add(List<Feature> newVals, SimpleFeatureExtractor src);

    /**
     * @return a fresh list instance that represents current values of this feature set.
     */
    List<Feature> toList();

    /**
     * Remove feature values that has been extracted by the specified features extractors.
     * @param aSources
     */
    void removeFeaturesBySource(Set<SimpleFeatureExtractor> aSources);
}
