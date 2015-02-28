package ru.ksu.niimm.cll.uima.morph.ml;

import org.hamcrest.Matcher;
import org.mockito.ArgumentMatcher;

import java.util.List;

/**
 * Package-private test utilities.
 *
 * @author Rinat Gareev
 */
class PTestUtils {

    // TODO move to TEST utils module
    public static <T> ArgumentMatcher<List<T>> list(final Matcher<? super T>... subMatchers) {
        return new ArgumentMatcher<List<T>>() {
            @Override
            public boolean matches(Object arg) {
                @SuppressWarnings("unchecked") List<T> list = (List<T>) arg;
                if (list.size() != subMatchers.length) return false;
                for (int i = 0; i < list.size(); i++) {
                    if (!subMatchers[i].matches(list.get(i)))
                        return false;
                }
                return true;
            }
        };
    }

    private PTestUtils() {
    }
}
