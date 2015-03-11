package ru.kfu.itis.issst.uima.ml;

import com.google.common.collect.Lists;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.component.Resource_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.factory.initializable.InitializableFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.cleartk.ml.CleartkProcessingException;
import org.cleartk.ml.SequenceDataWriterFactory;
import org.cleartk.ml.jar.DirectoryDataWriterFactory;
import ru.kfu.itis.cll.uima.io.IoUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.lang.String.format;

/**
 * @author Rinat Gareev
 */
public class TieredSequenceDataWriterResource<I extends AnnotationFS> extends Resource_ImplBase
        implements SequenceDataWriter<I, String[]> {

    public static ExternalResourceDescription createDescription(
            File outputBaseDir, Class<? extends SequenceDataWriterFactory<String>> dataWriterFactoryClass) {
        return ExternalResourceFactory.createExternalResourceDescription(TieredSequenceDataWriterResource.class,
                PARAM_OUTPUT_BASE_DIR, outputBaseDir.getPath(),
                PARAM_DATA_WRITER_FACTORY_CLASS, dataWriterFactoryClass.getName());
    }

    public static final String PARAM_OUTPUT_BASE_DIR = "outputBaseDir";
    public static final String PARAM_DATA_WRITER_FACTORY_CLASS = "dataWriterFactoryClass";

    @ConfigurationParameter(name = PARAM_OUTPUT_BASE_DIR, mandatory = true)
    private File outputBaseDir;
    private Properties featureExtractionCfg;
    @ConfigurationParameter(name = PARAM_DATA_WRITER_FACTORY_CLASS, mandatory = true)
    private String dataWriteFactoryClassName;
    private Class<? extends SequenceDataWriterFactory> dataWriterFactoryClass;
    // aggregate
    private TieredFeatureExtractor<I, String> featureExtractor;
    private List<org.cleartk.ml.SequenceDataWriter<String>> dataWriters;
    // delegate
    private TieredSequenceDataWriter<I> delegate;

    @Override
    public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
            throws ResourceInitializationException {
        if (!super.initialize(aSpecifier, aAdditionalParams))
            return false;
        //
        dataWriterFactoryClass = InitializableFactory.getClass(dataWriteFactoryClassName,
                SequenceDataWriterFactory.class);
        //
        if (outputBaseDir.exists() && !outputBaseDir.isDirectory()) {
            throw new IllegalStateException(format("%s exists but it is not a directory", outputBaseDir));
        }
        File feCfgFile = new File(outputBaseDir, TieredFeatureExtractors.FILENAME_FEATURE_EXTRACTION_CONFIG);
        try {
            featureExtractionCfg = IoUtils.readProperties(feCfgFile);
        } catch (IOException e) {
            throw new ResourceInitializationException(e);
        }
        initialize();
        return true;
    }

    private void initialize() throws ResourceInitializationException {
        initFeatureExtractor();
        initUnderlyingDataWriters();
        delegate = new TieredSequenceDataWriter<I>() {
            {
                this.dataWriters = TieredSequenceDataWriterResource.this.dataWriters;
                this.featureExtractor = TieredSequenceDataWriterResource.this.featureExtractor;
            }
        };
    }

    private void initFeatureExtractor() throws ResourceInitializationException {
        this.featureExtractor = TieredFeatureExtractors.from(featureExtractionCfg);
    }

    private void initUnderlyingDataWriters() throws ResourceInitializationException {
        List<String> tiers = TieredFeatureExtractors.getTiers(featureExtractionCfg);
        List<org.cleartk.ml.SequenceDataWriter<String>> dataWriters = Lists.newArrayList();
        for (String tierId : tiers) {
            File tierOutDir = new File(outputBaseDir, tierId);
            try {
                dataWriters.add(createUnderlyingDataWriter(tierOutDir));
            } catch (IOException e) {
                throw new ResourceInitializationException(e);
            }
        }
        this.dataWriters = dataWriters;
    }

    private org.cleartk.ml.SequenceDataWriter<String> createUnderlyingDataWriter(File outputDir) throws IOException {
        SequenceDataWriterFactory<String> factory;
        try {
            //noinspection unchecked
            factory = dataWriterFactoryClass.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
        // TODO unchecked hidden cast
        ((DirectoryDataWriterFactory) factory).setOutputDirectory(outputDir);
        return factory.createDataWriter();
    }

    @Override
    public void write(JCas jCas, Annotation spanAnno, List<? extends I> seq, List<String[]> seqLabels)
            throws CleartkProcessingException {
        delegate.write(jCas, spanAnno, seq, seqLabels);
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }
}
