package ru.kfu.itis.issst.uima.ml.fe;

import org.apache.uima.jcas.JCas;

/**
 * @author Rinat Gareev
 */
public interface IndexesJCas {
    /**
     * Annotator should invoke this method when a new CAS arrived and after the end of processing of this CAS.
     *
     * @param cas a CAS instance or NULL if annotator finishes CAS processing
     */
    void onCASChange(JCas cas);
}
