package ru.kfu.itis.cll.uima.util;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.collect.ImmutableList;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.component.NoOpAnnotator;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import ru.kfu.itis.cll.uima.cpe.XmiCollectionReader;

import java.io.File;
import java.util.List;

/**
 * @author Rinat Gareev
 */
public class PrintAnnotations {

    public static void main(String[] args) throws Exception {
        PrintAnnotations launcher = new PrintAnnotations();
        new JCommander(launcher, args);
        launcher.run();
    }

    @Parameter(names = "-t")
    private List<String> typeSystemNames;
    @Parameter(names = "-i", required = true)
    private File corpusDir;
    @Parameter(names = "--annotation-type", required = true)
    private String annotationTypeName;

    private PrintAnnotations() {
    }

    public void run() throws ResourceInitializationException, ClassNotFoundException {
        @SuppressWarnings("unchecked")
        Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) Class.forName(annotationTypeName);
        if (typeSystemNames == null) {
            typeSystemNames = ImmutableList.of();
        }
        TypeSystemDescription inputTSD = TypeSystemDescriptionFactory.createTypeSystemDescription(
                typeSystemNames.toArray(new String[typeSystemNames.size()]));
        CollectionReaderDescription colReaderDesc = XmiCollectionReader.createDescription(corpusDir, inputTSD);
        JCasIterable corpus = new JCasIterable(colReaderDesc, getNoOpAEDesc());
        for (JCas doc : corpus) {
            for (Annotation anno : JCasUtil.select(doc, annotationClass)) {
                System.out.println(anno.getCoveredText());
            }
        }
    }

    private static AnalysisEngineDescription getNoOpAEDesc() throws ResourceInitializationException {
        return AnalysisEngineFactory.createEngineDescription(NoOpAnnotator.class);
    }
}
