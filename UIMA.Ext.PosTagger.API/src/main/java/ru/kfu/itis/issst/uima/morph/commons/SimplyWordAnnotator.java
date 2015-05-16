package ru.kfu.itis.issst.uima.morph.commons;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import ru.kfu.itis.issst.uima.postagger.MorphCasUtils;

/**
 * @author Rinat Gareev
 */
public class SimplyWordAnnotator extends JCasAnnotator_ImplBase {

    public static AnalysisEngineDescription createDescription() throws ResourceInitializationException {
        return AnalysisEngineFactory.createEngineDescription(SimplyWordAnnotator.class);
    }

    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
        MorphCasUtils.makeSimplyWords(jCas);
    }
}
