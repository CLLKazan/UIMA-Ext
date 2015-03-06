package ru.kfu.itis.issst.uima.morph.compare;

import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toPrettyString;

import java.io.File;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.OperationalProperties;
import org.apache.uima.fit.util.JCasUtil;

import ru.kfu.cll.uima.segmentation.fstype.Sentence;
import ru.kfu.itis.cll.uima.io.IoUtils;
import ru.kfu.itis.cll.uima.util.DocumentUtils;
import ru.kfu.itis.issst.uima.morph.compare.impl.HSQLDBAnnotationDao;
import ru.kfu.itis.issst.uima.morph.compare.impl.HSQLDBFeatureDao;
import ru.kfu.itis.issst.uima.postagger.MorphCasUtils;

@OperationalProperties(multipleDeploymentAllowed = false)
public class TableWriter extends JCasAnnotator_ImplBase {

	public static final String PARAM_TAGGING_NAME = "taggingName";
	public static final String PARAM_DATA_SOURCE_CONFIG_FILE = "dataSourceConfigFile";
	public static final String PARAM_DISABLE_NEW_TEXT = "disableNewText";

	@ConfigurationParameter(name = PARAM_TAGGING_NAME, mandatory = true)
	private String taggingName;
	@ConfigurationParameter(name = PARAM_DATA_SOURCE_CONFIG_FILE, mandatory = true)
	private File dataSourceConfigFile;
	@ConfigurationParameter(name = PARAM_DISABLE_NEW_TEXT, defaultValue = "true")
	private boolean disableNewText;
	// state fields
	private BasicDataSource ds;
	private AnnotationDao wordDao;
	private AnnotationDao sentDao;
	private FeatureDao wordPosDao;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		// initialize data source
		try {
			Properties dsProps = IoUtils.readProperties(dataSourceConfigFile);
			ds = (BasicDataSource) BasicDataSourceFactory.createDataSource(dsProps);
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
		// initialize dao instances
		sentDao = new HSQLDBAnnotationDao(ds, Sentence.class.getSimpleName());
		wordDao = new HSQLDBAnnotationDao(ds, Word.class.getSimpleName());
		wordPosDao = new HSQLDBFeatureDao(ds, taggingName);
	}

	@Override
	public void destroy() {
		try {
			ds.close();
		} catch (SQLException e) {
			throw new RuntimeException();
		}
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		long timeBefore = System.currentTimeMillis();
		int wordCount = 0;
		String docUri = DocumentUtils.getDocumentUri(jCas);
		if (docUri == null) {
			throw new IllegalStateException("docUri == null");
		}
		// retain only filename 
		docUri = DocumentUtils.getFilenameSafe(docUri);
		for (Sentence sent : JCasUtil.select(jCas, Sentence.class)) {
			long sentId = getSentenceId(docUri, sent);
			for (Word word : JCasUtil.selectCovered(jCas, Word.class, sent)) {
				Long wordId = getWordId(sentId, word);
				if (wordId == null) {
					continue;
				}
				writeTag(wordId, getTag(word, docUri));
				wordCount++;
			}
		}
		getLogger().info(String.format("%s with %s words processed in %s ms",
				docUri, wordCount, System.currentTimeMillis() - timeBefore));
	}

	private Long getWordId(long sentId, Word word) {
		AnnotationEntity sentEntity = sentDao.getAnnotation(sentId);
		if (sentEntity == null) {
			throw new IllegalStateException();
		}
		String docUri = sentEntity.getDocUri();
		EnclosedAnnotationEntity wordEntity = wordDao.getAnnotation(docUri,
				word.getBegin(), word.getEnd());
		if (wordEntity == null) {
			if (disableNewText) {
				getLogger().warn(String.format(
						"Persisting of new annotations is disabled, "
								+ "but there is a Word:%s",
						toPrettyString(word)));
				return null;
			}
			wordEntity = new EnclosedAnnotationEntity(null, docUri,
					word.getBegin(), word.getEnd(), word.getCoveredText(),
					sentEntity.getId());
			wordEntity = wordDao.save(wordEntity);
		}
		return wordEntity.getId();
	}

	private long getSentenceId(String docUri, Sentence sent) {
		EnclosedAnnotationEntity sentEntity = sentDao.getAnnotation(docUri, sent.getBegin(),
				sent.getEnd());
		if (sentEntity == null) {
			if (disableNewText) {
				reportUnknownAnnotation(docUri, sent);
			}
			sentEntity = new EnclosedAnnotationEntity(null,
					docUri, sent.getBegin(), sent.getEnd(), null,
					null);
			sentEntity = sentDao.save(sentEntity);
		}
		return sentEntity.getId();
	}

	private void reportUnknownAnnotation(String docUri, Annotation anno) {
		throw new IllegalStateException(String.format(
				"Unknown %s in %s.\nAdding new text spans are disabled.",
				toPrettyString(anno), docUri));
	}

	private String getTag(Word word, String docUri) {
		Wordform wf = MorphCasUtils.getOnlyWordform(word);
		if (wf == null) {
			return null;
		}
		return String.valueOf(wf.getPos());
	}

	private void writeTag(long wordId, String tag) {
		wordPosDao.saveString(wordId, tag);
	}
}
