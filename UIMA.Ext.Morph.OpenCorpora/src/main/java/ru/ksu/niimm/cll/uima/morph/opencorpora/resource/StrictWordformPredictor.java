package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import com.google.common.collect.Lists;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;

import java.util.ArrayList;
import java.util.List;

// Predictor without prediction.
public class StrictWordformPredictor implements WordformPredictor{
    @Override
    public List<Wordform> predict(String str, WordformTSTSearchResult result) {
        if (result.getMatchedLength() < str.length())
            return new ArrayList<Wordform>();
        return Lists.newArrayList(result);
    }
}
