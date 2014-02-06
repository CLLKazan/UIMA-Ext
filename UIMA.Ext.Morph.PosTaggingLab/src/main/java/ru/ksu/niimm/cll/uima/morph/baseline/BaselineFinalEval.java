package ru.ksu.niimm.cll.uima.morph.baseline;

import static com.google.common.collect.Sets.newHashSet;

import java.util.List;

import ru.kfu.itis.cll.uima.util.CorpusUtils.PartitionType;
import ru.ksu.niimm.cll.uima.morph.lab.FinalEvalLauncherBase;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import de.tudarmstadt.ukp.dkpro.lab.uima.task.UimaTask;

public class BaselineFinalEval extends FinalEvalLauncherBase {

	public static void main(String[] args) throws Exception {
		System.setProperty("DKPRO_HOME", BaselineLab.DEFAULT_WRK_DIR);
		BaselineFinalEval lab = new BaselineFinalEval();
		new JCommander(lab, args);
		lab.run();
	}

	@Parameter(names = { "-p", "--pos-categories" }, required = true)
	private List<String> posCategoriesList;

	private BaselineFinalEval() {
	}

	private void run() throws Exception {
		posCategories = newHashSet(posCategoriesList);
		//
		UimaTask analysisTask = new BaselineLab.AnalysisTask(PartitionType.TEST, inputTS,
				morphDictDesc);
		//
		run(analysisTask);
	}
}
