package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import com.google.common.collect.Lists;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;

import java.util.ArrayList;
import java.util.List;

public class StrictWordformPredictor implements WordformPredictor{
    @Override
    public List<Wordform> predict(String str, WordformTSTSearchResult result) {
        List<Wordform> wordforms = new ArrayList<Wordform>();
        if (result.getMatchedLength() < str.length())
            return wordforms;
        return Lists.newArrayList(result);
    }
}
