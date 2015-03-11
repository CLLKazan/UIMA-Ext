package ru.kfu.itis.issst.uima.ml;

import com.google.common.collect.Lists;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.ml.SequenceClassifier;
import org.cleartk.ml.jar.JarClassifierBuilder;
import ru.kfu.itis.cll.uima.io.IoUtils;
import ru.kfu.itis.issst.cleartk.JarSequenceClassifierFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Factory methods for {@link ru.kfu.itis.issst.uima.ml.TieredSequenceClassifier}.
 *
 * @author Rinat Gareev
 */
public class TieredSequenceClassifiers {

    public static <I extends AnnotationFS> TieredSequenceClassifier<I> fromModelBaseDir(File modelBaseDir)
            throws ResourceInitializationException {
        final TieredFeatureExtractor<I, String> lFeatureExtractor;
        final List<SequenceClassifier<String>> lClassifiers;
        try {
            File feCfgFile = new File(modelBaseDir, TieredFeatureExtractors.FILENAME_FEATURE_EXTRACTION_CONFIG);
            Properties featExtractionCfg = IoUtils.readProperties(feCfgFile);
            List<String> tiers = TieredFeatureExtractors.getTiers(featExtractionCfg);
            lFeatureExtractor = TieredFeatureExtractors.from(featExtractionCfg);
            lClassifiers = createUnderlyingClassifiers(modelBaseDir, tiers);
        } catch (Exception e) {
            throw new ResourceInitializationException(e);
        }
        return new TieredSequenceClassifier<I>() {
            // initializer
            {
                this.classifiers = lClassifiers;
                this.featureExtractor = lFeatureExtractor;
            }
        };
    }


    private static List<SequenceClassifier<String>> createUnderlyingClassifiers(
            File modelBaseDir, Iterable<String> tierIds)
            throws IOException {
        List<SequenceClassifier<String>> resultList = Lists.newArrayList();
        for (String tierId : tierIds) {
            File tierModelDir = new File(modelBaseDir, tierId);
            File tierModelJar = JarClassifierBuilder.getModelJarFile(tierModelDir);
            JarSequenceClassifierFactory<String> clFactory = new JarSequenceClassifierFactory<>();
            clFactory.setClassifierJarPath(tierModelJar.getPath());
            org.cleartk.ml.SequenceClassifier<String> cl = clFactory.createClassifier();
            resultList.add(cl);
        }
        return resultList;
    }

    private TieredSequenceClassifiers() {
    }
}
