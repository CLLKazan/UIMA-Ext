/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger.opennlp;

import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toPrettyString;
import static ru.kfu.itis.cll.uima.util.DocumentUtils.getDocumentUri;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import opennlp.model.Event;
import opennlp.tools.util.AbstractEventStream;
import opennlp.tools.util.BeamSearchContextGenerator;
import opennlp.tools.util.ObjectStream;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.util.JCasUtil;

import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.cll.uima.tokenizer.fstype.W;
import ru.kfu.itis.issst.uima.morph.commons.PunctuationUtils;
import ru.kfu.itis.issst.uima.postagger.MorphCasUtils;

import com.google.common.collect.Lists;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class POSTokenEventStream<ST extends Annotation> extends AbstractEventStream<ST> {

	private BeamSearchContextGenerator<Token> cg;
	@SuppressWarnings("unused")
	private final Logger log = LoggerFactory.getLogger(getClass());

	public POSTokenEventStream(ObjectStream<ST> samples, BeamSearchContextGenerator<Token> cg) {
		super(samples);
		this.cg = cg;
	}

	@Override
	protected Iterator<Event> createEvents(ST spanAnno) {
		Event[] events = generateEvents(spanAnno, cg);
		return Arrays.asList(events).iterator();
	}

	public static Event[] generateEvents(Annotation spanAnno,
			BeamSearchContextGenerator<Token> contextGen) {
		JCas jCas;
		try {
			jCas = spanAnno.getCAS().getJCas();
		} catch (CASException e) {
			throw new IllegalStateException(e);
		}
		List<Token> tokens = new ArrayList<Token>(JCasUtil.selectCovered(
				jCas, Token.class, spanAnno));
		Map<Token, Word> token2WordIndex = MorphCasUtils.getToken2WordIndex(jCas, spanAnno);
		List<String> tags = Lists.newArrayListWithExpectedSize(tokens.size());
		for (Token tok : tokens) {
			Word word = token2WordIndex.get(tok);
			String tokStr = tok.getCoveredText();
			if (word == null) {
				if (tok instanceof NUM || tok instanceof W) {
					throw new IllegalStateException(String.format(
							"Token %s in %s does not have corresponding Word annotation",
							toPrettyString(tok), getDocumentUri(jCas)));
				}
				String tag = PunctuationUtils.getPunctuationTag(tokStr);
				tags.add(tag);
			} else {
				Wordform wf = MorphCasUtils.requireOnlyWordform(word);
				String tag = wf.getPos();
				tags.add(String.valueOf(tag));
			}
		}
		return generateEvents(spanAnno, tokens.toArray(new Token[tokens.size()]),
				tags.toArray(new String[tags.size()]),
				contextGen);
	}

	public static Event[] generateEvents(Annotation spanAnno,
			Token[] spanTokens, String[] tags,
			BeamSearchContextGenerator<Token> cg) {
		Event[] events = new Event[spanTokens.length];

		for (int i = 0; i < spanTokens.length; i++) {

			// it is safe to pass the tags as previous tags because
			// the context generator does not look for non predicted tags
			String[] context = cg.getContext(i, spanTokens, tags,
					new Object[] { spanAnno });

			events[i] = new Event(tags[i], context);
		}
		return events;
	}
}
