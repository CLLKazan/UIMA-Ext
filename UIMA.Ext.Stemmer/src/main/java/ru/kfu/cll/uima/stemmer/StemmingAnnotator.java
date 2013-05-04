package ru.kfu.cll.uima.stemmer;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

public class StemmingAnnotator extends JCasAnnotator_ImplBase {
    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        AnnotationIndex<Annotation> ai = aJCas.getAnnotationIndex(Letters.typeIndexID);
        FSIterator<Annotation> iterator = ai.iterator();
        while (iterator.hasNext()) {
            Annotation currentAnnotation = iterator.next();
            StemID currentStemID = new StemID(cas);
            currentStemID.setText(currentAnnotation.getCoveredText());
            currentStemID.setBegin(currentAnnotation.getBegin());
            currentStemID.setEnd(currentAnnotation.getEnd());
            currentStemID.addToIndexes();
        }
    }
}
