/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.stanford;

import static com.google.common.collect.Sets.newHashSet;
import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_CORPUS_SPLIT_INFO_DIR;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_FOLD;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_POS_CATEGORIES;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_SOURCE_CORPUS_DIR;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_CORPUS;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_MODEL_DIR;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_OUTPUT_DIR;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import ru.kfu.itis.cll.uima.util.CorpusUtils.PartitionType;
import ru.ksu.niimm.cll.uima.morph.lab.CorpusPreprocessingTask;
import ru.ksu.niimm.cll.uima.morph.lab.EvaluationTask;
import ru.ksu.niimm.cll.uima.morph.lab.LabConstants;
import ru.ksu.niimm.cll.uima.morph.lab.LabLauncherBase;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.CachedSerializedDictionaryResource;

import com.beust.jcommander.JCommander;
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
public class StanfordPosTaggerLabFinal extends LabLauncherBase {

	public static void main(String[] args) throws Exception {
		System.setProperty("DKPRO_HOME", "wrk/stanford-pos-lab");
		StanfordPosTaggerLabFinal lab = new StanfordPosTaggerLabFinal();
		new JCommander(lab, args);
		lab.run();
	}

	// the leading '_' is added to avoid confusion in Task classes
	@Parameter(names = { "-p", "--pos-categories" }, required = true)
	private List<String> _posCategoriesList;
	private Set<String> _posCategories;
	//
	@Parameter(names = { "-m", "--model" }, required = true)
	private File modelDir;
	private boolean allowTaggerMultiDeployment = false;

	private StanfordPosTaggerLabFinal() {
	}

	private void run() throws Exception {
		_posCategories = newHashSet(_posCategoriesList);
		// prepare input TypeSystem
		final TypeSystemDescription inputTS = createTypeSystemDescription(
				"ru.kfu.itis.cll.uima.commons.Commons-TypeSystem",
				"ru.kfu.cll.uima.tokenizer.tokenizer-TypeSystem",
				"ru.kfu.cll.uima.segmentation.segmentation-TypeSystem",
				"org.opencorpora.morphology-ts");
		// prepare morph dictionary resource
		final ExternalResourceDescription morphDictDesc = createExternalResourceDescription(
				CachedSerializedDictionaryResource.class,
				LabConstants.URL_RELATIVE_MORPH_DICTIONARY);
		//
		UimaTask preprocessingTask = new CorpusPreprocessingTask(inputTS, morphDictDesc);
		//
		UimaTask analysisTask = new StanfordPosTaggerLab.StanfordTaggerAnalysisTask(morphDictDesc,
				inputTS,
				PartitionType.TEST, allowTaggerMultiDeployment);
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
				Dimension.create(DISCRIMINATOR_POS_CATEGORIES, _posCategories),
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
