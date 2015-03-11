package ru.kfu.itis.issst.uima.ml;

import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.ml.feature.extractor.CleartkExtractorException;

import java.util.List;
import java.util.Properties;

/**
 * @param <ITEM> sequence item type
 * @param <OUT>  output label type of a tier classifier
 * @author Rinat Gareev
 */
public interface TieredFeatureExtractor<ITEM extends AnnotationFS, OUT> {

    /**
     * Initialize itself from the given configuration properties.
     *
     * @param cfg a configuration
     */
    void initialize(Properties cfg) throws ResourceInitializationException;

    /**
     * @param featSets
     * @param labels   output labels (one for each token) from each tier before the given one
     * @param tier
     * @param jCas
     * @param spanAnno
     * @param tokens
     * @throws CleartkExtractorException
     */
    void onBeforeTier(List<FeatureSet> featSets, List<List<OUT>> labels, int tier,
                      JCas jCas, Annotation spanAnno, List<? extends ITEM> tokens)
            throws CleartkExtractorException;

    /**
     * @param featSets
     * @param labels   output labels (one for each token) from each tier including the given one
     * @param tier
     * @param jCas
     * @param spanAnno
     * @param tokens
     * @throws CleartkExtractorException
     */
    void onAfterTier(List<FeatureSet> featSets, List<List<OUT>> labels, int tier,
                     JCas jCas, Annotation spanAnno, List<? extends ITEM> tokens)
            throws CleartkExtractorException;

    List<FeatureSet> extractCommonFeatures(JCas jCas, Annotation spanAnno, List<? extends ITEM> tokens)
            throws CleartkExtractorException;
}
