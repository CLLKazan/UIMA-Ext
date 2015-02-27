package ru.kfu.itis.issst.uima.test;

import org.apache.uima.cas.text.AnnotationFS;
import org.mockito.ArgumentMatcher;

/**
 * @author Rinat Gareev
 */
class AnnotationMatcher<T> extends ArgumentMatcher<T> {

    private String expectedTxt;

    public AnnotationMatcher(String expectedTxt) {
        this.expectedTxt = expectedTxt;
    }

    @Override
    public boolean matches(Object arg) {
        if(arg == null) return false;
        AnnotationFS argAnno = (AnnotationFS) arg;
        return expectedTxt.equals(argAnno.getCoveredText());
    }
}
