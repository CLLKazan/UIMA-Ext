/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger.opennlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import opennlp.model.Event;
import opennlp.tools.util.AbstractEventStream;
import opennlp.tools.util.BeamSearchContextGenerator;
import opennlp.tools.util.ObjectStream;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.util.JCasUtil;

import com.google.common.collect.Lists;

import ru.kfu.cll.uima.tokenizer.fstype.Token;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class PosSequenceEventStream<ST extends Annotation> extends AbstractEventStream<ST> {

	private BeamSearchContextGenerator<Token> cg;

	public PosSequenceEventStream(ObjectStream<ST> samples, BeamSearchContextGenerator<Token> cg) {
		super(samples);
		this.cg = cg;
	}

	@Override
	protected Iterator<Event> createEvents(ST spanAnno) {
		JCas jCas;
		try {
			jCas = spanAnno.getCAS().getJCas();
		} catch (CASException e) {
			throw new IllegalStateException(e);
		}
		Collection<Token> tokens = JCasUtil.select(jCas, Token.class);
		List<String> tags = Lists.newArrayListWithExpectedSize(tokens.size());
		for (Token tok : tokens) {
			// XXX
			// XXX
		}
		List<Event> events = generateEvents(
				tokens.toArray(new Token[tokens.size()]), tags.toArray(new String[tags.size()]), cg);
		return events.iterator();
	}

	public static List<Event> generateEvents(Token[] sentence, String[] tags,
			BeamSearchContextGenerator<Token> cg) {
		List<Event> events = new ArrayList<Event>(sentence.length);

		for (int i = 0; i < sentence.length; i++) {

			// it is safe to pass the tags as previous tags because
			// the context generator does not look for non predicted tags
			String[] context = cg.getContext(i, sentence, tags, null);

			events.add(new Event(tags[i], context));
		}
		return events;
	}
}
