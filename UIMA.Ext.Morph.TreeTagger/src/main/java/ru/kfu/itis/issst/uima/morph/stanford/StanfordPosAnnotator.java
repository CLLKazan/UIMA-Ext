/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.stanford;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.util.JCasUtil;

import com.google.common.collect.Lists;

import ru.kfu.cll.uima.segmentation.fstype.Sentence;
import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.cll.uima.tokenizer.fstype.W;
import ru.kfu.itis.cll.uima.cas.FSUtils;
import ru.kfu.itis.cll.uima.util.DocumentUtils;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class StanfordPosAnnotator extends JCasAnnotator_ImplBase {

	public static final String PARAM_MODEL_FILE = "modelFile";

	@ConfigurationParameter(name = PARAM_MODEL_FILE, mandatory = true)
	private File modelFile;
	// state fields
	private MaxentTagger tagger;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		if (!modelFile.isFile()) {
			throw new IllegalStateException(String.format(
					"%s is not an existing file", modelFile));
		}
		tagger = new MaxentTagger(modelFile.getPath());
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		String docUri = DocumentUtils.getDocumentUri(jCas);
		Collection<Sentence> sents = JCasUtil.select(jCas, Sentence.class);
		int sentsAnnotated = 0;
		for (Sentence sent : sents) {
			process(jCas, sent);
			sentsAnnotated++;
			if (getLogger().isDebugEnabled()) {
				getLogger().debug(String.format(
						"Annotated %s (of %s) sentences in %s",
						sentsAnnotated, sents.size(), docUri));
			}
		}
	}

	private void process(JCas jCas, Sentence sent) throws AnalysisEngineProcessException {
		List<Token> tokens = JCasUtil.selectCovered(jCas, Token.class, sent);
		if (tokens.isEmpty()) {
			return;
		}
		// s~Stanford
		List<TaggedWord> sWords = Lists.newArrayListWithExpectedSize(tokens.size());
		for (Token t : tokens) {
			sWords.add(new TaggedWord(t.getCoveredText()));
		}
		sWords = tagger.tagSentence(sWords);
		if (sWords.size() != tokens.size()) {
			throw new IllegalStateException();
		}
		Iterator<TaggedWord> swIter = sWords.iterator();
		for (Token t : tokens) {
			TaggedWord sw = swIter.next();
			if (t instanceof NUM || t instanceof W) {
				Word word = new Word(jCas, t.getBegin(), t.getEnd());
				word.setToken(t);

				Wordform wf = new Wordform(jCas);
				wf.setWord(word);
				wf.setPos(sw.tag());
				word.setWordforms(FSUtils.toFSArray(jCas, wf));

				word.addToIndexes();
			}
		}
	}

}
