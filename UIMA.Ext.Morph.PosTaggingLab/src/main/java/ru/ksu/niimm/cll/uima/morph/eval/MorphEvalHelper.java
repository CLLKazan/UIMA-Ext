/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.eval;

import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toPrettyString;
import static ru.kfu.itis.cll.uima.util.AnnotatorUtils.annotationTypeExist;
import static ru.kfu.itis.cll.uima.util.AnnotatorUtils.featureExist;
import static ru.kfu.itis.cll.uima.util.DocumentUtils.getDocumentUri;

import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.ArrayFS;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.StringArrayFS;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class MorphEvalHelper {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private TypeSystem ts;
	//
	private Feature wordformsFeat;
	private Feature gramsFeat;
	private Feature tagFeat;

	public MorphEvalHelper(TypeSystem ts) throws UIMAException {
		this.ts = ts;
		//
		Type wordType = ts.getType(Word.class.getName());
		annotationTypeExist(Word.class.getName(), wordType);
		Type wfType = ts.getType(Wordform.class.getName());
		annotationTypeExist(Wordform.class.getName(), wfType);
		wordformsFeat = featureExist(wordType, "wordforms");
		gramsFeat = featureExist(wfType, "grammems");
		tagFeat = featureExist(wfType, "pos");
	}

	protected FeatureStructure getWordform(AnnotationFS word) {
		ArrayFS wfs = (ArrayFS) word.getFeatureValue(wordformsFeat);
		if (wfs == null || wfs.size() == 0) {
			return null;
		}
		if (wfs.size() > 1) {
			log.warn(">1 wordforms for {} in {}",
					toPrettyString(word), getDocumentUri(word.getCAS()));
		}
		return wfs.get(0);
	}

	protected Set<String> getGrammems(FeatureStructure wf) {
		if (wf == null) {
			return ImmutableSet.of();
		}
		StringArrayFS grams = (StringArrayFS) wf.getFeatureValue(gramsFeat);
		if (grams == null) {
			return ImmutableSet.of();
		}
		ImmutableSet.Builder<String> resultBuilder = ImmutableSet.builder();
		for (int i = 0; i < grams.size(); i++) {
			resultBuilder.add(grams.get(i));
		}
		return resultBuilder.build();
	}

	protected String getTag(FeatureStructure wf) {
		return wf.getStringValue(tagFeat);
	}
}
