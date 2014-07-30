package ru.kfu.itis.issst.uima.morph.hunpos;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ru.kfu.itis.cll.uima.util.CorpusUtils.PartitionType;
import ru.ksu.niimm.cll.uima.morph.lab.FinalEvalLauncherBase;

import com.beust.jcommander.JCommander;

import de.tudarmstadt.ukp.dkpro.lab.task.Dimension;
import de.tudarmstadt.ukp.dkpro.lab.uima.task.UimaTask;

public class HunposFinalEval extends FinalEvalLauncherBase {

	public static void main(String[] args) throws Exception {
		System.setProperty("DKPRO_HOME", new File(HunposLab.DEFAULT_WRK_DIR).getAbsolutePath());
		HunposFinalEval lab = new HunposFinalEval();
		new JCommander(lab, args);
		lab.run();
	}

	private HunposFinalEval() {
	}

	private void run() throws Exception {
		UimaTask analysisTask = new HunposLab.AnalysisTask(PartitionType.TEST, inputTS);
		run(analysisTask);
	}

	@Override
	protected List<Dimension<?>> generateParamDims() throws IOException {
		List<Dimension<?>> dims = super.generateParamDims();
		dims.add(getFileDimension("lexiconFile"));
		return dims;
	}

}
