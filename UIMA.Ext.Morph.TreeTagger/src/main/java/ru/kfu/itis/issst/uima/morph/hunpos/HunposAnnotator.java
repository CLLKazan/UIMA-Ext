/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.hunpos;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import org.annolab.tt4j.TokenHandler;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.initializable.InitializableFactory;
import org.uimafit.util.JCasUtil;

import ru.kfu.cll.uima.segmentation.fstype.Sentence;
import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.cll.uima.tokenizer.fstype.W;
import ru.kfu.itis.issst.uima.morph.commons.TagMapper;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class HunposAnnotator extends JCasAnnotator_ImplBase {

	public static final String PARAM_HUNPOS_MODEL_NAME = "hunposModelName";
	public static final String PARAM_TAG_MAPPER_CLASS = "tagMapperClass";
	// config
	@ConfigurationParameter(name = PARAM_HUNPOS_MODEL_NAME, mandatory = true)
	private String hpModelName;
	@ConfigurationParameter(name = PARAM_TAG_MAPPER_CLASS,
			defaultValue = "ru.kfu.itis.issst.uima.morph.commons.DictionaryBasedTagMapper")
	private String tagMapperClassName;
	// monitors
	private final Object casMon = new Object();
	// state fields
	private TagMapper tagMapper;
	private HunposWrapper<Token> hunposTagger;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);

		tagMapper = InitializableFactory.create(ctx, tagMapperClassName, TagMapper.class);

		hunposTagger = new HunposWrapper<Token>();
		hunposTagger.setModelName(hpModelName);
		hunposTagger.setTokenAdapter(new TokenAdapter());
	}

	@Override
	public void process(final JCas jCas) throws AnalysisEngineProcessException {
		for (Sentence sent : JCasUtil.select(jCas, Sentence.class)) {
			process(jCas, sent);
		}
	}

	private void process(final JCas jCas, Sentence sent) throws AnalysisEngineProcessException {
		Collection<Token> tokens = JCasUtil.selectCovered(Token.class, sent);
		final Word[] words = new Word[tokens.size()];

		final AtomicInteger count = new AtomicInteger(0);
		hunposTagger.setTokenHandler(new TokenHandler<Token>() {
			@Override
			public void token(Token token, String pos, String lemma) {
				// should be synchronized on the same object with TokenAdapter#getText (see below)
				synchronized (casMon) {
					// do not create Wordform on punctuation and special tokens
					if (pos != null && (token instanceof W || token instanceof NUM)) {
						pos = pos.intern();
						Word w = new Word(jCas, token.getBegin(), token.getEnd());
						w.setToken(token);

						Wordform wf = new Wordform(jCas);
						wf.setWord(w);
						if (lemma != null) {
							wf.setLemma(lemma);
						}
						if (tagMapper == null) {
							wf.setPos(pos);
						} else {
							tagMapper.parseTag(pos, wf, token.getCoveredText());
						}

						FSArray wfArr = new FSArray(jCas, 1);
						wfArr.set(0, wf);
						w.setWordforms(wfArr);

						if (words[count.get()] != null) {
							throw new IllegalStateException();
						}
						words[count.get()] = w;
					}
					//
					count.getAndIncrement();
				}
			}
		});
		// 
		try {
			hunposTagger.process(tokens);

			// save annotations
			for (Word w : words) {
				if (w != null) {
					w.addToIndexes();
				}
			}
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		} finally {
			// just to clear reference to current CAS
			hunposTagger.setTokenHandler(null);
		}
	}

	@Override
	public void destroy() {
		if (hunposTagger != null) {
			hunposTagger.destroy();
			hunposTagger = null;
		}
		super.destroy();
	}

	@Override
	protected void finalize()
			throws Throwable
	{
		destroy();
		super.finalize();
	}

	private class TokenAdapter implements org.annolab.tt4j.TokenAdapter<Token> {
		@Override
		public String getText(Token t) {
			synchronized (casMon) {
				return t.getCoveredText();
			}
		}
	}
}