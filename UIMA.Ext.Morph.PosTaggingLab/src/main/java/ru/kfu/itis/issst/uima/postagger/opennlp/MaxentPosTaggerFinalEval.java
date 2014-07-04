/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger.opennlp;

import java.io.IOException;
import java.util.List;

import com.beust.jcommander.JCommander;

import de.tudarmstadt.ukp.dkpro.lab.task.Dimension;
import de.tudarmstadt.ukp.dkpro.lab.uima.task.UimaTask;
import ru.kfu.itis.cll.uima.util.CorpusUtils.PartitionType;
import ru.ksu.niimm.cll.uima.morph.lab.FinalEvalLauncherBase;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class MaxentPosTaggerFinalEval extends FinalEvalLauncherBase {

	public static void main(String[] args) throws Exception {
		System.setProperty("DKPRO_HOME", MaxentPosTaggerLab.DEFAULT_WRK_DIR);
		MaxentPosTaggerFinalEval lab = new MaxentPosTaggerFinalEval();
		new JCommander(lab, args);
		lab.run();
	}

	private MaxentPosTaggerFinalEval() {
	}

	private void run() throws Exception {
		UimaTask analysisTask = new MaxentPosTaggerLab.AnalysisTask(
				inputTS, PartitionType.TEST, morphDictDesc);
		run(analysisTask);
	}

	@Override
	protected List<Dimension<?>> generateParamDims() throws IOException {
		List<Dimension<?>> dims = super.generateParamDims();
		dims.add(getIntDimension("beamSize"));
		dims.add(getBoolDimension("beamSearchValidate"));
		return dims;
	}

}
