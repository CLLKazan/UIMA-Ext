package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import com.google.common.collect.Lists;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DummyWordformPredictor implements WordformPredictor{
    @Override
    public List<Wordform> predict(String str, WordformTSTSearchResult result) {
        // if match exact
        if (result.getMatchedLength() == str.length())
            return Lists.newArrayList(result);

        // otherwise
        Set<Wordform> wfSet = new HashSet<Wordform>();
        for (Wordform wf : result) {
            wf = wf.cloneWithoutLemmaId();
            wfSet.add(wf);
        }
        return Lists.newArrayList(wfSet);
    }
}
