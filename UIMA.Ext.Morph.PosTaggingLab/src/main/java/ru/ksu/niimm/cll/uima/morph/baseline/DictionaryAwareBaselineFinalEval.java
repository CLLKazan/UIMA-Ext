package ru.ksu.niimm.cll.uima.morph.baseline;

import static com.google.common.collect.Sets.newHashSet;

import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import de.tudarmstadt.ukp.dkpro.lab.uima.task.UimaTask;
import ru.kfu.itis.cll.uima.util.CorpusUtils.PartitionType;
import ru.ksu.niimm.cll.uima.morph.lab.FinalEvalLauncherBase;

public class DictionaryAwareBaselineFinalEval extends FinalEvalLauncherBase {
	
	public static void main(String[] args) throws Exception {
		System.setProperty("DKPRO_HOME", DictionaryAwareBaselineLab.DEFAULT_WRK_DIR);
		DictionaryAwareBaselineFinalEval lab = new DictionaryAwareBaselineFinalEval();
		new JCommander(lab, args);
		lab.run();
	}

	@Parameter(names = { "-p", "--pos-categories" }, required = true)
	private List<String> posCategoriesList;

	private DictionaryAwareBaselineFinalEval() {
	}

	private void run() throws Exception {
		posCategories = newHashSet(posCategoriesList);
		//
		UimaTask analysisTask = new DictionaryAwareBaselineLab.AnalysisTask(PartitionType.TEST,
				inputTS, morphDictDesc);
		//
		run(analysisTask);
	}
}
