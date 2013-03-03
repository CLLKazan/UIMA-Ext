package ru.kfu.cll.uima.segmentation;

import static ru.kfu.itis.cll.uima.util.AnnotatorUtils.annotationTypeExist;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.uimafit.component.CasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import ru.kfu.cll.uima.segmentation.fstype.PMSegment;
import ru.kfu.cll.uima.segmentation.fstype.QSegment;
import ru.kfu.cll.uima.segmentation.fstype.Sentence;
import ru.kfu.cll.uima.tokenizer.fstype.PM;

public class PunctuationSegmentAnnotator extends CasAnnotator_ImplBase {

	@ConfigurationParameter(name = "spanAnnotationType", defaultValue = "ru.kfu.cll.uima.segmentation.fstype.Sentence")
	private String spanAnnotationTypeName;
	// derived
	private Type spanAnnotationType;
	private Logger log;
	
        
	@Override
	public void process(CAS cas) throws AnalysisEngineProcessException {
		try {
			process(cas.getJCas());
		} catch (CASException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}
	
	

	private void process(JCas cas) throws AnalysisEngineProcessException {
		AnnotationIndex<Annotation> pmIndex = cas.getAnnotationIndex(PM.type);
		AnnotationIndex<Annotation> spanIdx = cas.getAnnotationIndex(spanAnnotationType);
		
		FSIterator<Annotation> sentenceIterator = spanIdx.iterator();
		sentenceIterator.moveToFirst();
		while (sentenceIterator.isValid()) 
		{
			Annotation span = sentenceIterator.get();
			process(cas, pmIndex, span);			
			sentenceIterator.moveToNext();
		}
		
		
		
		/*// get first sentence start
				tokensIter.moveToFirst();
				Annotation lastSentenceStart = tokensIter.get();
				tokensIter.moveToNext();

				while (tokensIter.isValid()) {
					Annotation token = tokensIter.get();*/
	}

	private void process(JCas cas, AnnotationIndex<Annotation> pmIndex,
			Annotation span) {
	
		FSIterator<Annotation> pmIter = pmIndex.subiterator(span);
	
		List<Annotation> pmList = Lists.newLinkedList();
		info("Listing punctuation marks in the sentence:\n%s",
				span.getCoveredText());
		
		while (pmIter.hasNext()) {
			Annotation pm = pmIter.next();			
			if (pm != null && pm.getCoveredText() != null) {
				if (pm.getCoveredText().matches("[:;,]")) 
				{
					pmList.add(pm);
					info("PM instance:\n%s", pm.getCoveredText());
				}
			}
			else
			{
				// sentence = PMSegment ?
				info("sentence = PMSegment :\n%s", pm.getCoveredText());
				PMSegment seg1 = new PMSegment(cas);
				seg1.setBegin(span.getBegin());
				seg1.setEnd(span.getEnd());
				seg1.addToIndexes();
				return;
			}
			
		}
		
		if (pmList.isEmpty()) 
		{
			// sentence = PMSegment
			PMSegment seg1 = new PMSegment(cas);
			seg1.setBegin(span.getBegin());
			seg1.setEnd(span.getEnd());
			seg1.addToIndexes();
			return;
		}

		if (pmList.size() == 1) {
			// 2 seg
			info("Following span contains single punctuation mark:\n%s",
					span.getCoveredText());
			
			PMSegment seg1 = new PMSegment(cas);
			seg1.setBegin(span.getBegin());
			seg1.setEnd(pmList.get(0).getBegin());
			seg1.addToIndexes();

			PMSegment seg2 = new PMSegment(cas);
			seg2.setBegin(pmList.get(0).getEnd());
			seg2.setEnd(span.getEnd());
			seg2.addToIndexes();

			return;
		}

		Annotation cur = pmList.get(0);
		Annotation end = pmList.get(pmList.size() - 1);
		
		
		info("main loop:\n%s",
				span.getCoveredText());
				
		for(int i=0; i < pmList.size() - 1; i++)
		{
			createPMSegment(cas, cur, pmList.get(i + 1));
			cur = pmList.get(i + 1);
		
		}

		// 2 boundary segments
		info("postprocessing:\n%s",
				span.getCoveredText());
		
		
		PMSegment seg1 = new PMSegment(cas);
		seg1.setBegin(span.getBegin());
		seg1.setEnd(pmList.get(0).getBegin());
		seg1.addToIndexes();

		PMSegment seg2 = new PMSegment(cas);
		seg2.setBegin(end.getEnd());
		seg2.setEnd(span.getEnd());
		seg2.addToIndexes();

	}

	private Annotation createPMSegment(JCas cas, Annotation pm1, Annotation pm2) {
		// sanity check
		/*if (pm2.getBegin() - pm1.getEnd() < 0) 
		{			
			throw new IllegalStateException();
		}*/

		PMSegment seg = new PMSegment(cas);
		seg.setBegin(pm1.getEnd());
		seg.setEnd(pm2.getBegin());
		seg.addToIndexes();
		info("new Seg :\n%s", pm1.getEnd() + " " + pm1.getCoveredText()   + " - " + pm2.getBegin()+ " " + pm2.getCoveredText());
		return seg;
	}
	
	private void info(String msg, Object... args) {
		log.log(Level.INFO, String.format(msg, args));
	}
	
	@Override
	public void typeSystemInit(TypeSystem ts) throws AnalysisEngineProcessException {
		super.typeSystemInit(ts);
		spanAnnotationType = ts.getType(spanAnnotationTypeName);
		annotationTypeExist(spanAnnotationTypeName, spanAnnotationType);
	}

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		log = context.getLogger();
		log.setLevel(Level.INFO);
	   
	    info("3333333333");

	}
}
