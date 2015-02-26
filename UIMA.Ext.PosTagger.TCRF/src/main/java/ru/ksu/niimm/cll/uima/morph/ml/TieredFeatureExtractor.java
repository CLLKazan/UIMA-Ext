package ru.ksu.niimm.cll.uima.morph.ml;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.classifier.feature.extractor.CleartkExtractorException;
import ru.kfu.cll.uima.tokenizer.fstype.Token;

import java.util.List;

/**
 * @author Rinat Gareev
 */
public interface TieredFeatureExtractor {
    void onBeforeTier(List<FeatureSet> featSets, int tier,
                      JCas jCas, Annotation spanAnno, List<Token> tokens)
            throws CleartkExtractorException;

    void onAfterTier(List<FeatureSet> featSets, List<String> tierOutLabels, int tier,
                     JCas jCas, Annotation spanAnno, List<Token> tokens)
            throws CleartkExtractorException;

    List<FeatureSet> extractCommonFeatures(JCas jCas, Annotation spanAnno, List<Token> tokens)
            throws CleartkExtractorException;
}
