package ru.ksu.niimm.cll.uima.morph.ml;

import com.beust.jcommander.internal.Lists;
import org.apache.uima.UIMAFramework;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceManager;
import org.cleartk.classifier.SequenceClassifier;
import org.cleartk.classifier.jar.JarClassifierBuilder;
import ru.kfu.itis.cll.uima.io.IoUtils;
import ru.kfu.itis.cll.uima.util.CacheKey;
import ru.kfu.itis.cll.uima.util.CachedResourceTuple;
import ru.kfu.itis.issst.cleartk.JarSequenceClassifierFactory;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionary;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import static java.lang.String.format;
import static ru.kfu.itis.issst.uima.morph.dictionary.MorphDictionaryAPIFactory.getMorphDictionaryAPI;
import static ru.ksu.niimm.cll.uima.morph.ml.TieredSequenceDataWriterResource.FILENAME_FEATURE_EXTRACTION_CONFIG;

/**
 * Factory methods for {@link ru.ksu.niimm.cll.uima.morph.ml.TieredSequenceClassifier}.
 *
 * @author Rinat Gareev
 */
public class TieredSequenceClassifiers {

    public static File resolveModelBaseDir(String modelBasePath, ResourceManager resMgr)
            throws MalformedURLException, URISyntaxException {
        if (resMgr == null) {
            resMgr = UIMAFramework.newDefaultResourceManager();
        }
        if (modelBasePath == null) {
            throw new IllegalStateException("Both modelBasePath & modelBaseDir are not specified");
        }
        URL modelBaseURL = resMgr.resolveRelativePath(modelBasePath);
        if (modelBaseURL == null)
            throw new IllegalStateException(format(
                    "Can't resolve path %s using an UIMA relative path resolver", modelBasePath));
        return new File(modelBaseURL.toURI());
    }

    public static TieredSequenceClassifier<?> fromModelBaseDir(File modelBaseDir)
            throws ResourceInitializationException {
        final CachedResourceTuple<MorphDictionary> morphDictTuple;
        final SimpleTieredFeatureExtractor lFeatureExtractor;
        final List<SequenceClassifier<String>> lClassifiers;
        try {
            morphDictTuple = getMorphDictionaryAPI().getCachedInstance();
            lFeatureExtractor = createFeatureExtractor(
                    modelBaseDir, morphDictTuple.getResource());
            lClassifiers = createUnderlyingClassifiers(
                    modelBaseDir, lFeatureExtractor.getGramTiers());
        } catch (Exception e) {
            throw new ResourceInitializationException(e);
        }
        return new TieredSequenceClassifier() {
            @SuppressWarnings("UnusedDeclaration")
            private CacheKey dictCacheKey = morphDictTuple.getCacheKey();

            // initializer
            {
                this.classifiers = lClassifiers;
                this.featureExtractor = lFeatureExtractor;
            }
        };
    }

    private static SimpleTieredFeatureExtractor createFeatureExtractor(File modelBaseDir, MorphDictionary dict)
            throws IOException {
        File feCfgFile = new File(modelBaseDir, FILENAME_FEATURE_EXTRACTION_CONFIG);
        Properties featExtractionCfg = IoUtils.readProperties(feCfgFile);
        // TODO refactor implementation-specific logic
        SimpleTieredFeatureExtractor featureExtractor = SimpleTieredFeatureExtractor.from(featExtractionCfg);
        featureExtractor.initialize(dict);
        return featureExtractor;
    }

    private static List<SequenceClassifier<String>> createUnderlyingClassifiers(File modelBaseDir, GramTiers gramTiers)
            throws IOException {
        List<SequenceClassifier<String>> resultList = Lists.newArrayList();
        for (int tier = 0; tier < gramTiers.getCount(); tier++) {
            String tierId = gramTiers.getTierId(tier);
            File tierModelDir = new File(modelBaseDir, tierId);
            File tierModelJar = JarClassifierBuilder.getModelJarFile(tierModelDir);
            JarSequenceClassifierFactory<String> clFactory = new JarSequenceClassifierFactory<String>();
            clFactory.setClassifierJarPath(tierModelJar.getPath());
            org.cleartk.classifier.SequenceClassifier<String> cl = clFactory.createClassifier();
            resultList.add(cl);
        }
        return resultList;
    }

    private TieredSequenceClassifiers() {
    }
}
