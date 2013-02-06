package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;


import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;

import java.util.Iterator;

public class WordformTSTSearchResult implements Iterable<Wordform>{
    private int matchedLength;
    private Iterator<Wordform> wordformIterator;

    public WordformTSTSearchResult(int matchedLength, Iterator<Wordform> wordformIterator) {
        this.matchedLength = matchedLength;
        this.wordformIterator = wordformIterator;
    }

    public int getMatchedLength() {
        return matchedLength;
    }

    @Override
    public Iterator<Wordform> iterator() {
        return wordformIterator;
    }
}
