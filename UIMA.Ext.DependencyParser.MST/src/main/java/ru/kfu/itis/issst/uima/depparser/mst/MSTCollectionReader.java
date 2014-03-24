package ru.kfu.itis.issst.uima.depparser.mst;

import static com.google.common.collect.Lists.newArrayList;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import mstparser.DependencyInstance;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.uimafit.component.JCasCollectionReader_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;

import ru.kfu.cll.uima.segmentation.fstype.Sentence;
import ru.kfu.cll.uima.tokenizer.TokenUtils;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.itis.cll.uima.cas.FSUtils;
import ru.kfu.itis.cll.uima.commons.DocumentMetadata;
import ru.kfu.itis.cll.uima.io.IoUtils;
import ru.kfu.itis.issst.uima.depparser.Dependency;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;

public class MSTCollectionReader extends JCasCollectionReader_ImplBase {

	public static final String PARAM_INPUT_FILE = "inputFile";

	@ConfigurationParameter(name = PARAM_INPUT_FILE, mandatory = true)
	private File inputFile;
	// state fields
	private Iterator<DependencyInstance> depInstIter;
	private int instancesRead;
	private URI inputFileUri;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		inputFileUri = inputFile.toURI();
		//
		try {
			depInstIter = new DependencyInstanceIterator();
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public boolean hasNext() throws IOException, CollectionException {
		return depInstIter.hasNext();
	}

	@Override
	public void getNext(JCas jCas) throws IOException, CollectionException {
		DependencyInstance inst = depInstIter.next();
		if (inst.forms.length == 0) {
			throw new IllegalStateException("Empty sentence");
		}
		instancesRead++;
		// 
		int[] tokenBegins = new int[inst.forms.length];
		int[] tokenEnds = new int[inst.forms.length];
		// text builder
		StringBuilder tb = new StringBuilder();
		for (int i = 0; i < inst.forms.length; i++) {
			// surface form
			String sf = inst.forms[i];
			tokenBegins[i] = tb.length();
			tb.append(sf);
			tokenEnds[i] = tb.length();
			tb.append(tokenSepChar);
		}
		// remove last separator char
		tb.deleteCharAt(tb.length() - 1);
		jCas.setDocumentText(tb.toString());
		// create annotations
		// tokens & words
		Word[] words = new Word[inst.forms.length];
		for (int i = 0; i < inst.forms.length; i++) {
			String sf = inst.forms[i];
			Token tok = TokenUtils.makeToken(jCas, sf, tokenBegins[i], tokenEnds[i]);
			tok.addToIndexes();
			//
			Word word = new Word(jCas, tok.getBegin(), tok.getEnd());
			word.setToken(tok);
			Wordform wf = new Wordform(jCas);
			wf.setWord(word);
			wf.setPos(inst.postags[i]);
			word.setWordforms(FSUtils.toFSArray(jCas, wf));
			word.addToIndexes();
			words[i] = word;
		}
		// dependencies
		for (int i = 0; i < inst.forms.length; i++) {
			Word word = words[i];
			Dependency dep = new Dependency(jCas, word.getBegin(), word.getEnd());
			dep.setDependent(word);
			// 0 - means ROOT, so we must decrement the value for proper lookup
			Word head = inst.heads[i] == 0 ? null : words[inst.heads[i] - 1];
			dep.setHead(head);
			dep.addToIndexes();
		}
		// sentence
		new Sentence(jCas, 0, tb.length()).addToIndexes();
		// metadata
		DocumentMetadata docMeta = new DocumentMetadata(jCas, 0, 0);
		docMeta.setSourceUri(getInstanceURI(instancesRead).toString());
		docMeta.addToIndexes();
	}

	private static final char tokenSepChar = ' ';

	private URI getInstanceURI(int instanceNo) {
		try {
			return new URI(inputFileUri.getScheme(), inputFileUri.getSchemeSpecificPart(),
					String.valueOf(instanceNo));
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public Progress[] getProgress() {
		return new Progress[] { new ProgressImpl(instancesRead, -1, Progress.ENTITIES) };
	}

	private class DependencyInstanceIterator extends AbstractIterator<DependencyInstance>
			implements Closeable {
		// state fields
		private BufferedReader reader;
		int currentLine;

		public DependencyInstanceIterator() throws IOException {
			super();
			reader = IoUtils.openReader(inputFile);
		}

		@Override
		protected DependencyInstance computeNext() {
			// surface forms line
			String line = readNextLine();
			if (line == null) {
				return endOfData();
			}
			List<String> forms = newArrayList(mstTokenSplitter.split(line));
			// pos-tags line
			line = readNextLine();
			if (line == null) {
				getLogger().warn(String.format("Unexpected end of file at line %s", currentLine));
				return endOfData();
			}
			List<String> tags = newArrayList(mstTokenSplitter.split(line));
			// heads line
			line = readNextLine();
			if (line == null) {
				getLogger().warn(String.format("Unexpected end of file at line %s", currentLine));
				return endOfData();
			}
			List<Integer> heads;
			{
				List<String> headStrings = newArrayList(mstTokenSplitter.split(line));
				heads = Lists.transform(headStrings, new Function<String, Integer>() {
					@Override
					public Integer apply(String arg) {
						return Integer.valueOf(arg);
					}
				});
			}
			//
			if (forms.size() != tags.size() || forms.size() != heads.size()) {
				throw new IllegalStateException(String.format(
						"Different size of the sequences at lines %s-%s",
						currentLine - 2, currentLine));
			}
			// read delimiter line
			readNextLine();
			return new DependencyInstance(
					forms.toArray(new String[forms.size()]),
					tags.toArray(new String[tags.size()]),
					null,
					ArrayUtils.toPrimitive(heads.toArray(new Integer[heads.size()])));
		}

		@Override
		public void close() throws IOException {
			IOUtils.closeQuietly(reader);
		}

		private String readNextLine() {
			String line;
			try {
				line = reader.readLine();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
			if (line != null) {
				currentLine++;
			}
			return line;
		}
	}

	private static final Splitter mstTokenSplitter = Splitter.on('\t');
}
