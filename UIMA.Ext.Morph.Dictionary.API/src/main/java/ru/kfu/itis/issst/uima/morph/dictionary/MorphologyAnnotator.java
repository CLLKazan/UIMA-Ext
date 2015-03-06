/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.dictionary;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static ru.kfu.itis.cll.uima.util.AnnotatorUtils.annotationTypeExist;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.fit.component.CasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;

import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionary;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionaryHolder;
import ru.kfu.itis.issst.uima.morph.model.Wordform;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class MorphologyAnnotator extends CasAnnotator_ImplBase {

	/**
	 * Create description with default parameter values. The result declares
	 * mandatory dependency on an external resource with
	 * {@link MorphDictionaryHolder} API on resource key
	 * {@value #RESOURCE_KEY_DICTIONARY}
	 * 
	 * @param annotationAdapterClass
	 * @param tsDesc
	 *            a type-system description that declares types produced by the
	 *            specified {@link AnnotationAdapter}
	 * @return description instance
	 * @throws ResourceInitializationException
	 */
	public static AnalysisEngineDescription createDescription(
			Class<? extends AnnotationAdapter> annotationAdapterClass,
			TypeSystemDescription tsDesc)
			throws ResourceInitializationException {
		return createEngineDescription(MorphologyAnnotator.class,
				tsDesc,
				PARAM_ANNOTATION_ADAPTER_CLASS, annotationAdapterClass.getName());
	}

	public static final String PARAM_TOKEN_TYPE = "TokenType";
	public static final String PARAM_ANNOTATION_ADAPTER_CLASS = "AnnotationAdapterClass";

	public static final String RESOURCE_KEY_DICTIONARY = "MorphDictionary";

	@ConfigurationParameter(name = PARAM_TOKEN_TYPE,
			defaultValue = "ru.kfu.cll.uima.tokenizer.fstype.Token")
	private String tokenTypeName;
	@ConfigurationParameter(name = PARAM_ANNOTATION_ADAPTER_CLASS, mandatory = true,
			defaultValue = "ru.kfu.itis.issst.uima.postagger.DefaultAnnotationAdapter")
	private String annoAdapterClassName;
	@ExternalResource(key = RESOURCE_KEY_DICTIONARY)
	private MorphDictionaryHolder dictHolder;
	// derived
	private Type tokenType;
	private AnnotationAdapter annoAdapter;
	private MorphDictionary dict;

	@Override
	public void typeSystemInit(TypeSystem ts) throws AnalysisEngineProcessException {
		super.typeSystemInit(ts);
		tokenType = ts.getType(tokenTypeName);
		annotationTypeExist(tokenTypeName, tokenType);
	}

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		dict = dictHolder.getDictionary();
		if (dict == null) {
			throw new IllegalStateException("dict is null");
		}
		//
		try {
			@SuppressWarnings("unchecked")
			Class<AnnotationAdapter> annoAdapterClass =
					(Class<AnnotationAdapter>) Class.forName(annoAdapterClassName);
			annoAdapter = annoAdapterClass.newInstance();
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
		annoAdapter.init(dict);
		getLogger().info(String.format("%s uses %s", getClass().getSimpleName(),
				annoAdapter.getClass().getSimpleName()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void process(CAS cas) throws AnalysisEngineProcessException {
		try {
			process(cas.getJCas());
		} catch (CASException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	private void process(JCas cas) throws AnalysisEngineProcessException {
		AnnotationIndex<Annotation> tokenIdx = cas.getAnnotationIndex(tokenType);
		for (Annotation token : tokenIdx) {
			String tokenStr = token.getCoveredText();
			if (proceed(tokenStr)) {
				// TODO configuration point
				// tokenizer should care about normalization 
				tokenStr = WordUtils.normalizeToDictionaryForm(tokenStr);
				List<Wordform> wfDictEntries = dict.getEntries(tokenStr);
				if (wfDictEntries != null && !wfDictEntries.isEmpty()) {
					// invoke adapter
					annoAdapter.apply(cas, token, wfDictEntries);
				}
			}
		}
	}

	// TODO configuration point
	private boolean proceed(String tokenStr) {
		return WordUtils.isRussianWord(tokenStr);
	}
}