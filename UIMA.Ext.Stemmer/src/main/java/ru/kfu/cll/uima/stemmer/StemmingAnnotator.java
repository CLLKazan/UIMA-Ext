package ru.kfu.cll.uima.stemmer;


import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import ru.kfu.cll.uima.stemmer.types.StemID;
import ru.kfu.cll.uima.tokenizer.types.RussianWord;


public class StemmingAnnotator extends JCasAnnotator_ImplBase {
    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        AnnotationIndex<Annotation> ai = aJCas.getAnnotationIndex(RussianWord.typeIndexID);
        FSIterator<Annotation> iterator = ai.iterator();
        while (iterator.hasNext()) {
            RussianWord currentWord = (RussianWord)iterator.next();
            StemID currentStemID = new StemID(aJCas);
            currentStemID.setIndex(currentWord.getText());
            currentStemID.setBegin(currentWord.getBegin());
            currentStemID.setEnd(currentWord.getEnd());
            currentStemID.addToIndexes();
        }
    }
}
