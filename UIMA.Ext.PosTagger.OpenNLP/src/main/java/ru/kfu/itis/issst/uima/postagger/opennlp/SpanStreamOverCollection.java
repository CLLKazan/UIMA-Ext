/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger.opennlp;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

import org.apache.uima.jcas.tcas.Annotation;

import opennlp.tools.util.ObjectStream;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class SpanStreamOverCollection<ST extends Annotation> implements ObjectStream<ST> {

	private final Iterator<ST> spanIter;

	public SpanStreamOverCollection(Iterator<ST> spanIter) {
		this.spanIter = spanIter;
	}

	@Override
	public ST read() throws IOException {
		if (spanIter.hasNext()) {
			return spanIter.next();
		}
		return null;
	}

	@Override
	public void reset() throws IOException, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() throws IOException {
		if (spanIter instanceof Closeable) {
			((Closeable) spanIter).close();
		}
	}

}
