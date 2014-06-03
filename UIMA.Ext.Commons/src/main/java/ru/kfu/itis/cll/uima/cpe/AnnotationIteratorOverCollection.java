/**
 * 
 */
package ru.kfu.itis.cll.uima.cpe;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import java.io.IOException;
import java.util.Iterator;

import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.component.NoOpAnnotator;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.pipeline.JCasIterable;
import org.uimafit.util.JCasUtil;

import com.google.common.collect.AbstractIterator;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class AnnotationIteratorOverCollection<AT extends Annotation> extends AbstractIterator<AT> {

	public static <A extends Annotation> Iterator<A> createIterator(
			Class<A> annotationClass,
			CollectionReaderDescription colReaderDesc,
			AnalysisEngineDescription aeDesc) throws UIMAException, IOException {
		CollectionReader colReader = CollectionReaderFactory.createCollectionReader(colReaderDesc);
		if (aeDesc == null) {
			aeDesc = createPrimitiveDescription(NoOpAnnotator.class);
		}
		AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(aeDesc);
		return new AnnotationIteratorOverCollection<A>(new JCasIterable(colReader, ae),
				annotationClass);
	}

	private final Class<AT> annotationClass;
	//
	private Iterator<JCas> jCasIter;
	private Iterator<AT> curJCasAnnotationIter;

	public AnnotationIteratorOverCollection(Iterable<JCas> jCasIterable, Class<AT> annotationClass) {
		this.annotationClass = annotationClass;
		jCasIter = jCasIterable.iterator();
		// init
		if (jCasIter.hasNext()) {
			JCas curJCas = jCasIter.next();
			curJCasAnnotationIter = getIterator(curJCas);
		}
	}

	@Override
	protected AT computeNext() {
		if (curJCasAnnotationIter == null) {
			return endOfData();
		}
		while (curJCasAnnotationIter != null && !curJCasAnnotationIter.hasNext()) {
			if (jCasIter.hasNext()) {
				JCas curJCas = jCasIter.next();
				curJCasAnnotationIter = getIterator(curJCas);
			} else {
				curJCasAnnotationIter = null;
			}
		}
		if (curJCasAnnotationIter == null) {
			return endOfData();
		}
		return curJCasAnnotationIter.next();
	}

	private Iterator<AT> getIterator(JCas jCas) {
		return JCasUtil.select(jCas, annotationClass).iterator();
	}
}
