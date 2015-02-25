package ru.kfu.itis.issst.uima.test;

import org.apache.uima.cas.text.AnnotationFS;
import org.mockito.ArgumentMatcher;

/**
 * @author Rinat Gareev
 */
public class AnnotationMatcher<T> extends ArgumentMatcher<T> {

    public static AnnotationMatcher<AnnotationFS> coverText(String expectedTxt) {
        return new AnnotationMatcher<AnnotationFS>(expectedTxt);
    }

    public static <T> AnnotationMatcher<T> coverText(String expectedTxt, Class<T> annoClass) {
        return new AnnotationMatcher<T>(expectedTxt);
    }

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
