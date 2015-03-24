package ru.kfu.itis.issst.uima.ml;

import com.google.common.base.Supplier;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import org.cleartk.ml.Feature;
import org.cleartk.ml.feature.extractor.FeatureExtractor1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Rinat Gareev
 */
class MultimapBasedFeatureSet implements FeatureSet {

    private ListMultimap<FeatureExtractor1, Feature> mm = Multimaps.newListMultimap(
            Maps.<FeatureExtractor1, Collection<Feature>>newIdentityHashMap(),
            new Supplier<List<Feature>>() {
                @Override
                public List<Feature> get() {
                    return Lists.newLinkedList();
                }
            }
    );

    @Override
    public void add(List<Feature> newVals, FeatureExtractor1 src) {
        mm.putAll(src, newVals);
    }

    @Override
    public List<Feature> toList() {
        return new ArrayList<Feature>(mm.values());
    }

    @Override
    public void removeFeaturesBySource(Set<FeatureExtractor1> aSources) {
        for (FeatureExtractor1 src : aSources) {
            removeFeaturesBySource(src);
        }
    }

    @Override
    public void removeFeaturesBySource(FeatureExtractor1 aSource) {
        mm.removeAll(aSource);
    }

    @Override
    public String toString() {
        return getClass().getName() + '(' + mm.toString() + ')';
    }
}
