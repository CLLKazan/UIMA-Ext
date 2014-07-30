package ru.ksu.niimm.cll.uima.morph.baseline;

import java.io.File;

import ru.kfu.itis.cll.uima.util.CorpusUtils.PartitionType;
import ru.ksu.niimm.cll.uima.morph.lab.FinalEvalLauncherBase;

import com.beust.jcommander.JCommander;

import de.tudarmstadt.ukp.dkpro.lab.uima.task.UimaTask;

public class BaselineFinalEval extends FinalEvalLauncherBase {

	public static void main(String[] args) throws Exception {
		System.setProperty("DKPRO_HOME", new File(BaselineLab.DEFAULT_WRK_DIR).getAbsolutePath());
		BaselineFinalEval lab = new BaselineFinalEval();
		new JCommander(lab, args);
		lab.run();
	}

	private BaselineFinalEval() {
	}

	private void run() throws Exception {
		UimaTask analysisTask = new BaselineLab.AnalysisTask(PartitionType.TEST, inputTS);
		//
		run(analysisTask);
	}
}
