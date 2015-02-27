package ru.kfu.itis.issst.uima.test;

import org.apache.uima.cas.text.AnnotationFS;
import org.mockito.ArgumentMatcher;
import org.opencorpora.cas.Wordform;
import ru.kfu.cll.uima.tokenizer.fstype.Token;

import java.util.List;

/**
 * @author Rinat Gareev
 */
public class AnnotationMatchers {

    public static ArgumentMatcher<AnnotationFS> coverText(String expectedTxt) {
        return new AnnotationMatcher<AnnotationFS>(expectedTxt);
    }

    public static <T> ArgumentMatcher<T> coverText(String expectedTxt, Class<T> annoClass) {
        return new AnnotationMatcher<T>(expectedTxt);
    }

    public static <A extends AnnotationFS> ArgumentMatcher<A> fromTo(String prefix, String suffix) {
        return new FromToAnnoMatcher<A>(prefix, suffix);
    }

    public static ArgumentMatcher<List<AnnotationFS>> coverTextList(final String... expectedTxts) {
        return new ArgumentMatcher<List<AnnotationFS>>() {
            @Override
            public boolean matches(Object arg) {
                if (arg == null) return false;
                List<AnnotationFS> list = (List<AnnotationFS>) arg;
                if (list.size() != expectedTxts.length)
                    return false;
                for (int i = 0; i < expectedTxts.length; i++) {
                    String txt = expectedTxts[i];
                    String actual;
                    if (list.get(i) instanceof Token) {
                        actual = ((Token) list.get(i)).getCoveredText();
                    } else {
                        actual = ((Wordform) list.get(i)).getWord().getCoveredText();
                    }
                    if (!txt.equals(actual))
                        return false;
                }
                return true;
            }
        };
    }

    private AnnotationMatchers() {
    }
}
