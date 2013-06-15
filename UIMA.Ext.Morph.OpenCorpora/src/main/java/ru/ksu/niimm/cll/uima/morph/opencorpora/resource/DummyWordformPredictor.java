package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Lemma;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DummyWordformPredictor implements WordformPredictor{
    private MorphDictionary dict;
    private int pseudoLemmaIdCounter = -1;
    private Map<Lemma, Lemma> uniqPseudoLemmaMap = Maps.newHashMap();

    private synchronized Lemma addPseudoLemma(Lemma l) {
        if (uniqPseudoLemmaMap.containsKey(l)) {
            return uniqPseudoLemmaMap.get(l);
        } else {
            l.setId(pseudoLemmaIdCounter--);
            uniqPseudoLemmaMap.put(l, l);
            dict.addLemma(l);
            return l;
        }
    }

    public DummyWordformPredictor(MorphDictionary dict) {
        this.dict = dict;

    }

    @Override
    public List<Wordform> predict(String str, WordformTSTSearchResult result) {
        Set<Wordform> wfSet = new HashSet<Wordform>();
        for (Wordform wf : result) {
            Lemma lemma = dict.getLemma(wf.getLemmaId()).cloneWithoutIdAndString();
            lemma = addPseudoLemma(lemma);

            wf = wf.cloneWithLemmaId(lemma.getId());
            wfSet.add(wf);
        }
        return Lists.newArrayList(wfSet);
    }
}
