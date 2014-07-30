/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.ExternalResourceFactory.createDependency;

import java.util.Collection;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.util.FSCollectionFactory;
import org.uimafit.util.JCasUtil;

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
		AnalysisEngineDescription aeDesc = createPrimitiveDescription(PosTrimmingAnnotator.class,
				PARAM_TARGET_POS_CATEGORIES, targetPosCategories);
		try {
			createDependency(aeDesc, RESOURCE_GRAM_MODEL, GramModelHolder.class);
		} catch (InvalidXMLException e) {
			throw new ResourceInitializationException(e);
		}
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