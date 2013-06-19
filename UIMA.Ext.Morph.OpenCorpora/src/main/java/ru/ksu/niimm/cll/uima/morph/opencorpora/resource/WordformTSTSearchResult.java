package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;


import com.google.common.collect.Iterators;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;

import java.util.Iterator;

public class WordformTSTSearchResult implements Iterable<Wordform>{
    private boolean isMatchExact;
    private WordformTST.Node resultNode;

    public WordformTSTSearchResult(boolean matchExact, WordformTST.Node resultNode) {
        isMatchExact = matchExact;
        this.resultNode = resultNode;
    }

    public boolean isMatchExact() {
        return isMatchExact;
    }

    @Override
    public Iterator<Wordform> iterator() {
	    if (isMatchExact) {
		    return resultNode.iterator();
	    } else {
		    if (resultNode == null)
			    return Iterators.emptyIterator();
		    else
			    return new WordformTST.SubtreeIterator(resultNode);
	    }
    }
}
