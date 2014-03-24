/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.treetagger;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.annolab.tt4j.TokenHandler;
import org.annolab.tt4j.TreeTaggerWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.util.JCasUtil;

import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.cll.uima.tokenizer.fstype.W;
import ru.kfu.itis.issst.uima.morph.commons.TagMapper;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionaryUtils;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class MorphTagger extends JCasAnnotator_ImplBase {

	public static final String PARAM_TREETAGGER_MODEL_NAME = "treeTaggerModelName";
	public static final String PARAM_TAG_MAPPER_CLASS = "tagMapperClass";
	// config
	@ConfigurationParameter(name = PARAM_TREETAGGER_MODEL_NAME, mandatory = true)
	private String ttModelName;
	@ConfigurationParameter(name = PARAM_TAG_MAPPER_CLASS, defaultValue = "ru.kfu.itis.issst.uima.morph.treetagger.MTETagMapper")
	private String tagMapperClassName;
	// monitors
	private final Object casMon = new Object();
	// state fields
	private TagMapper tagMapper;
	private TreeTaggerWrapper<Token> treeTagger;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);

		if (!StringUtils.isBlank(tagMapperClassName)) {
			try {
				@SuppressWarnings("unchecked")
				Class<TagMapper> tagMapperClass = (Class<TagMapper>) Class
						.forName(tagMapperClassName);
				tagMapper = tagMapperClass.newInstance();
			} catch (Exception e) {
				throw new ResourceInitializationException(e);
			}
		}

		treeTagger = new TreeTaggerWrapper<Token>();
		treeTagger.setAdapter(new TokenAdapter());
		try {
			treeTagger.setModel(ttModelName);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public void process(final JCas jCas) throws AnalysisEngineProcessException {
		Collection<Token> tokens = JCasUtil.select(jCas, Token.class);
		final Word[] words = new Word[tokens.size()];

		final AtomicInteger count = new AtomicInteger(0);
		treeTagger.setHandler(new TokenHandler<Token>() {
			@Override
			public void token(Token token, String pos, String lemma) {
				// should be synchronized on the same object with TokenAdapter#getText (see below)
				synchronized (casMon) {
					// do not create Wordform on punctuation and special tokens
					// TODO MTE Rus TreeTagger also outputs tag 'SENT' for sentence end?
					if (pos != null && (token instanceof W || token instanceof NUM)) {
						pos = pos.intern();
						Word w = new Word(jCas, token.getBegin(), token.getEnd());
						w.setToken(token);

						Wordform wf = new Wordform(jCas);
						if (lemma != null) {
							wf.setLemma(lemma);
						}
						if (tagMapper == null) {
							wf.setPos(pos);
						} else {
							Set<String> grams = tagMapper.parseTag(pos, token.getCoveredText());
							MorphDictionaryUtils.applyGrammems(grams, wf);
						}

						wf.setWord(w);
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
			treeTagger.process(tokens);

			// save annotations
			for (Word w : words) {
				if (w != null) {
					w.addToIndexes();
				}
			}
			// just to clear reference to current CAS
			treeTagger.setHandler(null);
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	@Override
	public void destroy() {
		super.destroy();
		if (treeTagger != null) {
			getLogger().info("Cleaning up TreeTagger process");
			treeTagger.destroy();
		}
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