package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import java.io.Serializable;
import java.util.Iterator;

import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;

public class NodeData implements Serializable, Iterable<Wordform> {

	private static final long serialVersionUID = 113653440079164833L;
	
	private Wordform[] data;
    
    public NodeData(Wordform wf) {
		this.addData(wf);
	}

	void addData(Wordform wf) {
        if (data == null) {
            data = new Wordform[1];
        } else {
            Wordform[] temp = data;
            data = new Wordform[data.length + 1];
            System.arraycopy(temp, 0, data, 0, temp.length);
        }
        data[data.length - 1] = wf;
    }

    @Override
    public Iterator<Wordform> iterator() {
        return new Iterator<Wordform>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return data != null && i < data.length;
            }

            @Override
            public Wordform next() {
                return data[i++];
            }

            @Override
            public void remove() {
                Wordform[] n = new Wordform[data.length - 1];
                System.arraycopy(data, 0, n, 0, i );
                System.arraycopy(data, i + 1, n, i, data.length - i - 1);
                data = n;
            }
        };
    }

}
