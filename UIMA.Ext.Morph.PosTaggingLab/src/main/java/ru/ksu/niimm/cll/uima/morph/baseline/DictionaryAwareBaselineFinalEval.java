package ru.ksu.niimm.cll.uima.morph.baseline;

import java.io.File;

import ru.kfu.itis.cll.uima.util.CorpusUtils.PartitionType;
import ru.ksu.niimm.cll.uima.morph.lab.FinalEvalLauncherBase;

import com.beust.jcommander.JCommander;

import de.tudarmstadt.ukp.dkpro.lab.uima.task.UimaTask;

public class DictionaryAwareBaselineFinalEval extends FinalEvalLauncherBase {

	public static void main(String[] args) throws Exception {
		System.setProperty("DKPRO_HOME", new File(
				DictionaryAwareBaselineLab.DEFAULT_WRK_DIR).getAbsolutePath());
		DictionaryAwareBaselineFinalEval lab = new DictionaryAwareBaselineFinalEval();
		new JCommander(lab, args);
		lab.run();
	}

	private DictionaryAwareBaselineFinalEval() {
	}

	private void run() throws Exception {
		UimaTask analysisTask = new DictionaryAwareBaselineLab.AnalysisTask(PartitionType.TEST,
				inputTS, morphDictDesc);
		//
		run(analysisTask);
	}
}
