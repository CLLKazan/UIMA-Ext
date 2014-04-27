package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.Lists;
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.RadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.SmartArrayBasedNodeFactory;

public class WordformTrie implements Serializable {
	private static final long serialVersionUID = 6643426248422366315L;

	private transient RadixTree<NodeData> trieImpl = new ConcurrentRadixTree<NodeData>(
			new SmartArrayBasedNodeFactory());

	public void put(String text, Wordform wf) {
		text = new StringBuilder(text).reverse().toString();
		NodeData nd;
		if ((nd = trieImpl.putIfAbsent(text, new NodeData(wf))) != null) {
			nd.addData(wf);
		}
	}

	public List<Wordform> get(String key) {
		key = new StringBuilder(key).reverse().toString();
		NodeData exactResult = trieImpl.getValueForExactKey(key);
		if (exactResult != null) {
			return Lists.newArrayList(exactResult);
		} else {
			// TODO: predict
			return Lists.newArrayList();
		}
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		Kryo kryo = new Kryo();
		kryo.writeObject(new Output(out), trieImpl);
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		Kryo kryo = new Kryo();
		trieImpl = kryo.readObject(new Input(in), ConcurrentRadixTree.class);
	}

}
