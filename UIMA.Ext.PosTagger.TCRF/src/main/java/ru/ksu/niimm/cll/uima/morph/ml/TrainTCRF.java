/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import java.io.File;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.google.common.collect.Lists;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class TrainTCRF {

	public static void main(String[] args) throws Exception {
		TrainTCRF obj = new TrainTCRF();
		JCommander com = new JCommander(obj);
		try {
			com.parse(args);
		} catch (ParameterException e) {
			System.err.println(e.getMessage());
			com.usage();
			System.exit(1);
		}
		obj.run();
	}

	// input
	@Parameter(names = "--training-dir", required = true)
	private File trainingBaseDir;
	// output
	@Parameter(names = "--model-dir", required = true)
	private File modelBaseDir;
	// optional parameters
	@Parameter(names = "--feature-min-freq")
	private int featureMinFreq = 0;
	@Parameter(names = "--feature-possible-states")
	private boolean featurePossibleStates = true;
	@Parameter(names = "--feature-possible-transitions")
	private boolean featurePossibleTransitions = true;
	@Parameter(names = "--c2")
	private int c2 = 1;
	@Parameter(names = "--optimization-max-iterations")
	private int optMaxIterations = 200;

	private TrainTCRF() {
	}

	private void run() throws Exception {
		// set training parameters
		List<String> trainerArgs = Lists.newArrayList();
		trainerArgs.add("-a");
		trainerArgs.add("lbfgs");
		addTrainParam(trainerArgs, "max_iterations", optMaxIterations);
		addTrainParam(trainerArgs, "feature.minfreq", featureMinFreq);
		if (featurePossibleStates) {
			addTrainParam(trainerArgs, "feature.possible_states", 1);
		}
		if (featurePossibleTransitions) {
			addTrainParam(trainerArgs, "feature.possible_transitions", 1);
		}
		addTrainParam(trainerArgs, "c2", c2);
		//
		TieredPosSequenceAnnotatorFactory.trainModels(trainingBaseDir, modelBaseDir,
				trainerArgs.toArray(new String[trainerArgs.size()]));
	}

	private void addTrainParam(List<String> params, String name, int value) {
		params.add("-p");
		params.add(name + "=" + value);
	}
}