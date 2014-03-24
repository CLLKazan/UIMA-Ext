package ru.kfu.itis.issst.uima.depparser.mst;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.coveredTextFunction;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import mstparser.DependencyInstance;
import mstparser.DependencyParser;
import mstparser.DependencyPipe;
import mstparser.DependencyPipe2O;
import mstparser.ParserOptions;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceAccessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.opencorpora.cas.Word;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.util.JCasUtil;

import ru.kfu.cll.uima.segmentation.fstype.Sentence;
import ru.kfu.itis.cll.uima.io.IoUtils;
import ru.kfu.itis.cll.uima.util.AnnotatorUtils;
import ru.kfu.itis.issst.uima.depparser.Dependency;
import ru.kfu.itis.issst.uima.morph.commons.TagUtils;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class MSTParsingAnnotator extends JCasAnnotator_ImplBase {

	public static final String RESOURCE_MODEL_FILE = "modelFile";
	public static final String MODEL_PROPERTIES_FILE_EXTENSION = ".props";
	public static final String MODEL_PROP_ORDER = "order";

	// state fields
	private DependencyParser parser;
	private Function<Word, String> tagFunction;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		//
		tagFunction = TagUtils.tagFunction();
		// TODO:LOW make it works with URL instead of files 
		String modelFilePath;
		try {
			modelFilePath = ctx.getResourceFilePath(RESOURCE_MODEL_FILE);
		} catch (ResourceAccessException e) {
			throw new ResourceInitializationException(e);
		}
		AnnotatorUtils.mandatoryResourceObject(RESOURCE_MODEL_FILE, modelFilePath);
		File modelFile = new File(modelFilePath);
		Properties modelProperties;
		try {
			modelProperties = readModelProperties(modelFile);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}

		InputStream modelStream = null;
		try {
			// configure parser options
			ParserOptions options = new ParserOptions(new String[0]);
			options.test = true;
			options.train = false;
			options.eval = false;
			options.format = "MST";
			options.secondOrder = "2".equals(modelProperties.get(MODEL_PROP_ORDER));
			DependencyPipe pipe = options.secondOrder
					? new DependencyPipe2O(options)
					: new DependencyPipe(options);
			// make Parser intance
			parser = new DependencyParser(pipe, options);
			// load model
			modelStream = new BufferedInputStream(new FileInputStream(modelFile));
			parser.loadModel(modelStream);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		} finally {
			IOUtils.closeQuietly(modelStream);
		}
	}

	@Override
	public void process(JCas cas) throws AnalysisEngineProcessException {
		if (!JCasUtil.exists(cas, Sentence.class) || !JCasUtil.exists(cas, Word.class)) {
			return;
		}
		try {
			parser.options.testfile = generateTempInputFile(cas);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
		// run parsing
		List<DependencyInstance> parsedInstances;
		try {
			parsedInstances = parser.getParses();
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		} finally {
			new File(parser.options.testfile).delete();
			parser.options.testfile = null;
		}
		// add to CAS
		List<Sentence> sentences = newArrayList(JCasUtil.select(cas, Sentence.class));
		for (int instanceIndex = 0; instanceIndex < parsedInstances.size(); instanceIndex++) {
			DependencyInstance instance = parsedInstances.get(instanceIndex);
			Sentence sentence = sentences.get(instanceIndex);

			List<Word> words = newArrayList(JCasUtil.selectCovered(cas, Word.class, sentence));
			for (int wIndex = 0; wIndex < instance.forms.length; wIndex++) {
				Word w = words.get(wIndex);
				// get dependency relation and head information for token
				int head = instance.heads[wIndex];

				// write dependency information as annotation to JCas
				Dependency dep = new Dependency(cas, w.getBegin(), w.getEnd());
				dep.setDependent(w);
				if (head > 0) {
					dep.setHead(words.get(head - 1));
				}
				// the root
				else {
					dep.setHead(null);
				}
				dep.addToIndexes();
			}
		}
	}

	private Properties readModelProperties(File modelFile) throws IOException {
		File modelDir = modelFile.getParentFile();
		String propsFileName = modelFile.getName() + MODEL_PROPERTIES_FILE_EXTENSION;
		File propsFile = new File(modelDir, propsFileName);
		return IoUtils.readProperties(propsFile);
	}

	/**
	 * Generates a temporary file from a jcas. This is needed as input to the
	 * MST parser.
	 */
	private String generateTempInputFile(JCas jcas) throws IOException {
		File tempfile = File.createTempFile("MSTinput", "txt");
		BufferedWriter out = IoUtils.openBufferedWriter(tempfile);

		// write sentences to temporary file in MST input format
		try {
			for (Sentence sentence : JCasUtil.select(jcas, Sentence.class)) {
				List<Word> words = JCasUtil.selectCovered(jcas, Word.class, sentence);
				List<String> forms = Lists.transform(words, coveredTextFunction());
				List<String> tags = Lists.transform(words, tagFunction);
				// Dummy values for heads
				List<Integer> dummyHeads = newArrayListWithExpectedSize(words.size());
				for (int i = 0; i < words.size(); i++) {
					dummyHeads.add(0);
				}
				MSTFormat.writeInstance(out, forms, tags, dummyHeads);
			}
		} finally {
			IOUtils.closeQuietly(out);
		}
		tempfile.deleteOnExit();
		return tempfile.getPath();
	}
}
