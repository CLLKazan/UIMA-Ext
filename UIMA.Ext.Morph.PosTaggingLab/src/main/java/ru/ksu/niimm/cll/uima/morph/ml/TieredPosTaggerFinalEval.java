/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import java.io.File;

import ru.kfu.itis.cll.uima.util.CorpusUtils.PartitionType;
import ru.ksu.niimm.cll.uima.morph.lab.FinalEvalLauncherBase;

import com.beust.jcommander.JCommander;

import de.tudarmstadt.ukp.dkpro.lab.uima.task.UimaTask;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class TieredPosTaggerFinalEval extends FinalEvalLauncherBase {

	public static void main(String[] args) throws Exception {
		System.setProperty("DKPRO_HOME",
				new File(TieredPosTaggerLab.DEFAULT_WRK_DIR).getAbsolutePath());
		TieredPosTaggerFinalEval launcher = new TieredPosTaggerFinalEval();
		new JCommander(launcher, args);
		launcher.run();
	}

	private void run() throws Exception {
		// run
		UimaTask analysisTask = new TieredPosTaggerLab.AnalysisTask(
				inputTS, morphDictDesc, PartitionType.TEST);
		run(analysisTask);
	}
}
