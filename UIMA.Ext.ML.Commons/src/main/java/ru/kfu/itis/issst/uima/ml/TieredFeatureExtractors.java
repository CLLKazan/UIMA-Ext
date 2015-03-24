package ru.kfu.itis.issst.uima.ml;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.resource.ResourceInitializationException;
import ru.kfu.itis.cll.uima.io.IoUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static ru.kfu.itis.cll.uima.util.ConfigPropertiesUtils.getStringProperty;

/**
 * @author Rinat Gareev
 */
public class TieredFeatureExtractors {

    public static final String FILENAME_FEATURE_EXTRACTION_CONFIG = "fec.properties";
    public static final String CFG_FEATURE_EXTRACTOR_CLASSNAME = "class";
    public static final String CFG_TIERS = "tiers";
    // split tier definitions in a single line definition string
    public static final char tierSplitterChar = '|';
    public static final Splitter tierSplitter = Splitter.on(tierSplitterChar).trimResults();

    public static <I extends AnnotationFS, O> TieredFeatureExtractor<I, O> from(Properties cfg) {
        String className = getStringProperty(cfg, CFG_FEATURE_EXTRACTOR_CLASSNAME);
        TieredFeatureExtractor<I, O> fe;
        try {
            Class<? extends TieredFeatureExtractor> clazz =
                    Class.forName(className).asSubclass(TieredFeatureExtractor.class);
            //noinspection unchecked
            fe = clazz.newInstance();
            fe.initialize(cfg);
        } catch (ReflectiveOperationException | ResourceInitializationException e) {
            throw new IllegalStateException(e);
        }
        return fe;
    }

    public static List<String> getTiers(Properties cfg) {
        String tiersDef = getStringProperty(cfg, CFG_TIERS);
        return Lists.newArrayList(tierSplitter.split(tiersDef));
    }

    public static Properties parseConfig(File modelDir) throws IOException {
        File feCfgFile = new File(modelDir, FILENAME_FEATURE_EXTRACTION_CONFIG);
        return IoUtils.readProperties(feCfgFile);
    }

    private TieredFeatureExtractors() {
    }
}
