package ru.kfu.itis.issst.uima.depparser.mst;

import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;
import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.coveredTextFunction;
import static ru.kfu.itis.issst.uima.morph.commons.TagUtils.tagFunction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.opencorpora.cas.Word;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.OperationalProperties;
import org.uimafit.util.JCasUtil;

import ru.kfu.cll.uima.segmentation.fstype.Sentence;
import ru.kfu.itis.cll.uima.io.IoUtils;
import ru.kfu.itis.issst.uima.depparser.Dependency;

import com.google.common.collect.Lists;

@OperationalProperties(multipleDeploymentAllowed = false)
public class MSTWriter extends JCasAnnotator_ImplBase {

	public static final AnalysisEngineDescription createDescription(File outputFile)
			throws ResourceInitializationException {
		TypeSystemDescription tsDesc = createTypeSystemDescription(
				"ru.kfu.cll.uima.segmentation.segmentation-TypeSystem",
				"ru.kfu.itis.issst.uima.depparser.dependency-ts");
		return createPrimitiveDescription(MSTWriter.class, tsDesc,
				PARAM_OUTPUT_FILE, outputFile.getPath());
	}

	public static final String PARAM_OUTPUT_FILE = "outputFile";

	@ConfigurationParameter(name = PARAM_OUTPUT_FILE, mandatory = true)
	private File outputFile;
	// state fields
	private BufferedWriter outWriter;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		//
		try {
			outWriter = IoUtils.openBufferedWriter(outputFile);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		for (Sentence sent : JCasUtil.select(jCas, Sentence.class)) {
			writeSentence(outWriter, jCas, sent);
		}
	}

	static void writeSentence(Writer outWriter, JCas jCas, Sentence sent)
			throws AnalysisEngineProcessException {
		List<Word> words = JCasUtil.selectCovered(jCas, Word.class, sent);
		List<Dependency> deps = JCasUtil.selectCovered(jCas, Dependency.class, sent);
		if (words.size() != deps.size()) {
			throw new IllegalStateException("Words.size != Deps.size");
		}
		List<String> forms = Lists.transform(words, coveredTextFunction());
		List<String> tags = Lists.transform(words, tagFunction());
		List<Integer> heads = newArrayListWithExpectedSize(deps.size());
		for (Dependency dep : deps) {
			Word head = dep.getHead();
			if (head == null) {
				heads.add(0);
			} else {
				int headIndex = words.indexOf(head);
				if (headIndex < 0) {
					throw new IllegalStateException();
				}
				heads.add(headIndex + 1);
			}
		}
		// sanity check
		if (heads.size() != forms.size() || tags.size() != forms.size()) {
			throw new IllegalStateException();
		}
		//
		try {
			MSTFormat.writeInstance(outWriter, forms, tags, heads);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		super.collectionProcessComplete();
		IOUtils.closeQuietly(outWriter);
		outWriter = null;
	}

	@Override
	public void destroy() {
		super.destroy();
		IOUtils.closeQuietly(outWriter);
		outWriter = null;
	}
}
