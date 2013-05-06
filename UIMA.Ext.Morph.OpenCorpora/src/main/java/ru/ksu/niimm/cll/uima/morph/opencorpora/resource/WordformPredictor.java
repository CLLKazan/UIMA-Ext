package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;

import java.util.List;

public interface WordformPredictor {
    List<Wordform> predict(String str, WordformTSTSearchResult result);
}
