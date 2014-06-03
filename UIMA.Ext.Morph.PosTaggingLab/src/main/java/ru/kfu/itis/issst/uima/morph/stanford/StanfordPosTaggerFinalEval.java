/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.stanford;

import static ru.kfu.itis.issst.uima.morph.stanford.StanfordPosTaggerLab.DEFAULT_WRK_DIR;
import ru.kfu.itis.cll.uima.util.CorpusUtils.PartitionType;
import ru.ksu.niimm.cll.uima.morph.lab.FinalEvalLauncherBase;

import com.beust.jcommander.JCommander;

import de.tudarmstadt.ukp.dkpro.lab.uima.task.UimaTask;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class StanfordPosTaggerFinalEval extends FinalEvalLauncherBase {

	public static void main(String[] args) throws Exception {
		System.setProperty("DKPRO_HOME", DEFAULT_WRK_DIR);
		StanfordPosTaggerFinalEval lab = new StanfordPosTaggerFinalEval();
		new JCommander(lab, args);
		lab.run();
	}

	private boolean allowTaggerMultiDeployment = false;

	private StanfordPosTaggerFinalEval() {
	}

	private void run() throws Exception {
		UimaTask analysisTask = new StanfordPosTaggerLab.StanfordTaggerAnalysisTask(inputTS,
				PartitionType.TEST, allowTaggerMultiDeployment);
		run(analysisTask);
	}
}
