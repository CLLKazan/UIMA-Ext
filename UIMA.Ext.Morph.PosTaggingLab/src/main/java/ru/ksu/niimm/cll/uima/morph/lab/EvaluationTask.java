/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.lab;

import static ru.ksu.niimm.cll.uima.morph.lab.AnalysisTaskBase.getTargetFileList;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_CORPUS;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_OUTPUT_DIR;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.PLACEHOLDER_OUTPUT_BASE_DIR;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.kfu.itis.cll.uima.eval.EvaluationLauncher;
import ru.kfu.itis.cll.uima.util.ConfigPropertiesUtils;
import ru.kfu.itis.cll.uima.util.CorpusUtils.PartitionType;

import com.google.common.collect.Maps;

import de.tudarmstadt.ukp.dkpro.lab.engine.TaskContext;
import de.tudarmstadt.ukp.dkpro.lab.storage.StorageService.AccessMode;
import de.tudarmstadt.ukp.dkpro.lab.task.Discriminator;
import de.tudarmstadt.ukp.dkpro.lab.task.impl.ExecutableTaskBase;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class EvaluationTask extends ExecutableTaskBase {
	// config fields
	protected final Logger log = LoggerFactory.getLogger(getClass());
	private PartitionType targetPartition;
	// state fields
	@Discriminator
	int fold;
	@Discriminator
	File corpusSplitInfoDir;

	public EvaluationTask(PartitionType targetPartition) {
		this.targetPartition = targetPartition;
		switch (targetPartition) {
		case TRAIN:
			throw new IllegalArgumentException();
		case DEV:
			setType("Evaluation");
			break;
		case TEST:
			setType("EvaluationFinal");
			break;
		default:
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public void execute(TaskContext taskCtx) throws Exception {
		File outputDir = taskCtx.getStorageLocation(KEY_OUTPUT_DIR, AccessMode.ADD_ONLY);
		File goldDir = taskCtx.getStorageLocation(KEY_CORPUS, AccessMode.READONLY);
		Properties evalCfg = readEvaluationConfig();
		// replace placeholders
		Map<String, String> phValues = Maps.newHashMap();
		phValues.put(PLACEHOLDER_OUTPUT_BASE_DIR, outputDir.getPath());
		ConfigPropertiesUtils.replacePlaceholders(evalCfg, phValues);
		evalCfg.setProperty("goldCasDirectory.dir", goldDir.getPath());
		evalCfg.setProperty("goldCasDirectory.listFile",
				getTargetFileList(corpusSplitInfoDir, targetPartition, fold).getPath());
		evalCfg.setProperty("systemCasDirectory.dir", outputDir.getPath());
		if (log.isInfoEnabled()) {
			log.info("Evaluation config:\n {}",
					ConfigPropertiesUtils.prettyString(evalCfg));
		}
		EvaluationLauncher.runUsingProperties(evalCfg);
	}

	private Properties readEvaluationConfig() throws IOException {
		Properties evalProps = new Properties();
		String evalPropsPath = "ru/ksu/niimm/cll/uima/morph/lab/eval.properties";
		InputStream evalPropsIS = getClassLoader().getResourceAsStream(evalPropsPath);
		if (evalPropsIS == null) {
			throw new IllegalStateException(String.format("Can't find classpath resource %s",
					evalPropsPath));
		}
		Reader evalPropsReader = new BufferedReader(new InputStreamReader(evalPropsIS, "utf-8"));
		try {
			evalProps.load(evalPropsReader);
		} finally {
			evalPropsReader.close();
		}
		return evalProps;
	}

	private ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
}
