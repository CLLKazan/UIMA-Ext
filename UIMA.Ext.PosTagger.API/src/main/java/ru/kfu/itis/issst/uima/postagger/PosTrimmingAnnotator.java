/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.ExternalResourceFactory.createDependency;

import java.util.Collection;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.FSCollectionFactory;
import org.apache.uima.fit.util.JCasUtil;

import ru.kfu.itis.issst.uima.morph.dictionary.resource.GramModelHolder;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class PosTrimmingAnnotator extends JCasAnnotator_ImplBase {

	public static final String RESOURCE_GRAM_MODEL = "gramModel";
	public static final String PARAM_TARGET_POS_CATEGORIES = "targetPosCategories";

	/**
	 * Create description with the given parameter values. The result declares
	 * mandatory dependency on an external resource with {@link GramModelHolder}
	 * API on resource key {@value #RESOURCE_GRAM_MODEL}.
	 * 
	 * @param targetPosCategories
	 * @return description instance
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngineDescription createDescription(String[] targetPosCategories)
			throws ResourceInitializationException {
		AnalysisEngineDescription aeDesc = createEngineDescription(PosTrimmingAnnotator.class,
				PARAM_TARGET_POS_CATEGORIES, targetPosCategories);
        createDependency(aeDesc, RESOURCE_GRAM_MODEL, GramModelHolder.class);
        return aeDesc;
	}

	@ExternalResource(key = RESOURCE_GRAM_MODEL, mandatory = true)
	private GramModelHolder gramModelHolder;
	@ConfigurationParameter(name = PARAM_TARGET_POS_CATEGORIES, mandatory = true)
	private String[] targetPosCategories;
	// derived
	private PosTrimmer trimmer;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		trimmer = new PosTrimmer(gramModelHolder.getGramModel(), targetPosCategories);
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		for (Word word : JCasUtil.select(jcas, Word.class)) {
			FSArray wordformsFSArray = word.getWordforms();
			if (wordformsFSArray == null) {
				continue;
			}
			Collection<Wordform> wordforms = FSCollectionFactory.create(wordformsFSArray,
					Wordform.class);
			for (Wordform wf : wordforms) {
				trimmer.trim(jcas, wf);
			}
		}
	}
}