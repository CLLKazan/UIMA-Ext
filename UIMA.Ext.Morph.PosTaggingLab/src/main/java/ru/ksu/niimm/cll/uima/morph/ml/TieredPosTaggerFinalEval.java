/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import de.tudarmstadt.ukp.dkpro.lab.uima.task.UimaTask;

import ru.kfu.itis.cll.uima.util.CorpusUtils.PartitionType;
import ru.ksu.niimm.cll.uima.morph.lab.FinalEvalLauncherBase;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class TieredPosTaggerFinalEval extends FinalEvalLauncherBase {

	public static void main(String[] args) throws Exception {
		System.setProperty("DKPRO_HOME", TieredPosTaggerLab.DEFAULT_WRK_DIR);
		TieredPosTaggerFinalEval launcher = new TieredPosTaggerFinalEval();
		new JCommander(launcher, args);
		launcher.run();
	}

	@Parameter(names = { "--pos-tiers" }, required = true)
	private List<String> _posTiers;

	private void run() throws Exception {
		// set posCategories
		posCategories = TieredPosTaggerLab.getAllCategories(_posTiers);
		// run
		UimaTask analysisTask = new TieredPosTaggerLab.AnalysisTask(
				inputTS, morphDictDesc, PartitionType.TEST);
		run(analysisTask);
	}
}
