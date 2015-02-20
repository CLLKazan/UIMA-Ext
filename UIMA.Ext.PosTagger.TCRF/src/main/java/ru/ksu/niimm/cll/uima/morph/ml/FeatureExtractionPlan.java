package ru.ksu.niimm.cll.uima.morph.ml;

import com.google.common.collect.*;
import org.cleartk.classifier.feature.extractor.simple.SimpleFeatureExtractor;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
* @author Rinat Gareev
*/
interface FeatureExtractionPlan {
    Set<SimpleFeatureExtractor> getExtractorsToRemoveOn(int step);

    Set<SimpleFeatureExtractor> getNewFeatureExtractors(int step);
}

class FeatureExtractionPlanBuilder {
    private SetMultimap<Integer, SimpleFeatureExtractor> extractionAgenda
            = LinkedHashMultimap.create();

    void addExtractors(Set<Integer> steps, SimpleFeatureExtractor fe) {
        for (Integer s : steps) {
            addExtractors(s, fe);
        }
    }

    void addExtractors(int step, SimpleFeatureExtractor fe) {
        extractionAgenda.put(step, fe);
    }

    public FeatureExtractionPlan build() {
        final List<Set<SimpleFeatureExtractor>> stepExtractorsList = Lists.newArrayList();
        final List<Set<SimpleFeatureExtractor>> stepRemovalsList = Lists.newArrayList();
        //
        final int maxStep = Collections.max(extractionAgenda.keys());
        Set<SimpleFeatureExtractor> prevStepFEs = ImmutableSet.of();
        //
        for (int step = 0; step < maxStep; step++) {
            Set<SimpleFeatureExtractor> stepFEs = ImmutableSet.copyOf(extractionAgenda.get(step));
            stepExtractorsList.add(Sets.difference(stepFEs, prevStepFEs));
            stepRemovalsList.add(Sets.difference(prevStepFEs, stepFEs));
            prevStepFEs = stepFEs;
        }
        //
        return new FeatureExtractionPlan() {
            @Override
            public Set<SimpleFeatureExtractor> getExtractorsToRemoveOn(int step) {
                validateStep(step);
                return stepRemovalsList.get(step);
            }

            @Override
            public Set<SimpleFeatureExtractor> getNewFeatureExtractors(int step) {
                validateStep(step);
                return stepExtractorsList.get(step);
            }

            private void validateStep(int step) {
                if (step < 0 || step > maxStep)
                    throw new IllegalArgumentException("Too big step: " + step);
            }
        };
    }
}