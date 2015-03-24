package ru.kfu.itis.issst.uima.test;

import org.apache.uima.cas.text.AnnotationFS;
import org.mockito.ArgumentMatcher;

/**
 * @author Rinat Gareev
 */
class FromToAnnoMatcher<A extends AnnotationFS> extends ArgumentMatcher<A> {
    private String prefix;
    private String suffix;

    FromToAnnoMatcher(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public boolean matches(Object arg) {
        AnnotationFS anno = (AnnotationFS) arg;
        return anno.getCoveredText().startsWith(prefix) && anno.getCoveredText().endsWith(suffix);
    }
}
