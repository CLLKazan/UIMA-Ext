/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.lab;

import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_CORPUS_SPLIT_INFO_DIR;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_FOLD;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_POS_CATEGORIES;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_SOURCE_CORPUS_DIR;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_CORPUS;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_MODEL_DIR;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_OUTPUT_DIR;

import java.io.File;
import java.util.Set;

import ru.kfu.itis.cll.uima.util.CorpusUtils.PartitionType;

import com.beust.jcommander.Parameter;

import de.tudarmstadt.ukp.dkpro.lab.Lab;
import de.tudarmstadt.ukp.dkpro.lab.task.Dimension;
import de.tudarmstadt.ukp.dkpro.lab.task.ParameterSpace;
import de.tudarmstadt.ukp.dkpro.lab.task.Task;
import de.tudarmstadt.ukp.dkpro.lab.task.impl.BatchTask;
import de.tudarmstadt.ukp.dkpro.lab.task.impl.BatchTask.ExecutionPolicy;
import de.tudarmstadt.ukp.dkpro.lab.uima.task.UimaTask;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class FinalEvalLauncherBase extends LabLauncherBase {

	@Parameter(names = { "-m", "--model-dir" }, required = true)
	private File modelDir;
	protected Set<String> posCategories;

	protected void run(UimaTask analysisTask) throws Exception {
		if (posCategories == null) {
			throw new IllegalStateException("posCategories was not set!");
		}
		//
		UimaTask preprocessingTask = new CorpusPreprocessingTask(inputTS, morphDictDesc);
		//
		Task evaluationTask = new EvaluationTask(PartitionType.TEST);
		// configure data-flow between tasks
		analysisTask.addImport(preprocessingTask, KEY_CORPUS);
		analysisTask.addImport(modelDir, KEY_MODEL_DIR);
		evaluationTask.addImport(preprocessingTask, KEY_CORPUS);
		evaluationTask.addImport(analysisTask, KEY_OUTPUT_DIR);
		@SuppressWarnings("unchecked")
		ParameterSpace pSpace = new ParameterSpace(
				Dimension.create(DISCRIMINATOR_SOURCE_CORPUS_DIR, srcCorpusDir),
				Dimension.create(DISCRIMINATOR_CORPUS_SPLIT_INFO_DIR, corpusSplitDir),
				// posCategories discriminator is used in the preprocessing task
				Dimension.create(DISCRIMINATOR_POS_CATEGORIES, posCategories),
				Dimension.create(DISCRIMINATOR_FOLD, 0));
		//
		BatchTask batchTask = new BatchTask();
		batchTask.addTask(preprocessingTask);
		batchTask.addTask(analysisTask);
		batchTask.addTask(evaluationTask);
		//
		batchTask.setParameterSpace(pSpace);
		batchTask.setExecutionPolicy(ExecutionPolicy.USE_EXISTING);
		Lab.getInstance().run(batchTask);
	}

}
