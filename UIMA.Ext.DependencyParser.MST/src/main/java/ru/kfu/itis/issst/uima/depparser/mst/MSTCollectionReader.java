package ru.kfu.itis.issst.uima.depparser.mst;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import mstparser.DependencyInstance;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;

import ru.kfu.cll.uima.segmentation.fstype.Sentence;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.itis.cll.uima.cas.FSUtils;
import ru.kfu.itis.cll.uima.commons.DocumentMetadata;
import ru.kfu.itis.issst.uima.depparser.Dependency;
import ru.kfu.itis.issst.uima.segmentation.SentenceSplitterAPI;
import ru.kfu.itis.issst.uima.tokenizer.TokenUtils;
import ru.kfu.itis.issst.uima.tokenizer.TokenizerAPI;

public class MSTCollectionReader extends JCasCollectionReader_ImplBase {

	public static CollectionReaderDescription createDescription(File inputFile)
			throws ResourceInitializationException {
		TypeSystemDescription inputTSD = TypeSystemDescriptionFactory.createTypeSystemDescription(
				"ru.kfu.itis.cll.uima.commons.Commons-TypeSystem",
				TokenizerAPI.TYPESYSTEM_TOKENIZER,
				SentenceSplitterAPI.TYPESYSTEM_SENTENCES,
				"ru.kfu.itis.issst.uima.depparser.dependency-ts");
		return CollectionReaderFactory.createReaderDescription(
				MSTCollectionReader.class, inputTSD,
				PARAM_INPUT_FILE, inputFile.getPath());
	}

	public static final String PARAM_INPUT_FILE = "inputFile";

	@ConfigurationParameter(name = PARAM_INPUT_FILE, mandatory = true)
	private File inputFile;
	// state fields
	private MSTDependencyInstanceIterator depInstIter;
	private int instancesRead;
	private URI inputFileUri;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		inputFileUri = inputFile.toURI();
		//
		try {
			depInstIter = new MSTDependencyInstanceIterator(inputFile);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public void close() throws IOException {
		super.close();
		depInstIter.close();
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
}
