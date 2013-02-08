package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;


import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;

import java.util.Iterator;

public class WordformTSTSearchResult implements Iterable<Wordform>{
    private boolean isMatchExact;
    private Iterator<Wordform> wordformIterator;

    public WordformTSTSearchResult(boolean isMatchExact, Iterator<Wordform> wordformIterator) {
        this.isMatchExact = isMatchExact;
        this.wordformIterator = wordformIterator;
    }

    public boolean isMatchExact() {
        return isMatchExact;
    }

    @Override
    public Iterator<Wordform> iterator() {
        return wordformIterator;
    }
}
