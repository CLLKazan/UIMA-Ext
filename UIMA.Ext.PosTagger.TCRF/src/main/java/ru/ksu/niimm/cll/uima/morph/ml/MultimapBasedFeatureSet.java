package ru.ksu.niimm.cll.uima.morph.ml;

import com.google.common.base.Supplier;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import org.cleartk.classifier.Feature;
import org.cleartk.classifier.feature.extractor.simple.SimpleFeatureExtractor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Rinat Gareev
 */
class MultimapBasedFeatureSet implements FeatureSet {

    private ListMultimap<SimpleFeatureExtractor, Feature> mm = Multimaps.newListMultimap(
            Maps.<SimpleFeatureExtractor, Collection<Feature>>newIdentityHashMap(),
            new Supplier<List<Feature>>() {
                @Override
                public List<Feature> get() {
                    return Lists.newLinkedList();
                }
            }
    );

    @Override
    public void add(List<Feature> newVals, SimpleFeatureExtractor src) {
        mm.putAll(src, newVals);
    }

    @Override
    public List<Feature> toList() {
        return new ArrayList<Feature>(mm.values());
    }

    @Override
    public void removeFeaturesBySource(Set<SimpleFeatureExtractor> aSources) {
        for (SimpleFeatureExtractor src : aSources) {
            removeFeaturesBySource(src);
        }
    }

    @Override
    public void removeFeaturesBySource(SimpleFeatureExtractor aSource) {
        mm.removeAll(aSource);
    }
}
