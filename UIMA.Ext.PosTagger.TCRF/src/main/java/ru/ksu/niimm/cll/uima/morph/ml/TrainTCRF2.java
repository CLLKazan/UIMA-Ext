/**
 *
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.cleartk.ml.jar.JarClassifierBuilder;
import ru.kfu.itis.issst.cleartk.crfsuite.CRFSuiteStringOutcomeClassifierBuilder;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

import static java.lang.String.format;
import static org.apache.commons.io.filefilter.FileFilterUtils.directoryFileFilter;
import static ru.kfu.itis.issst.cleartk.crfsuite.CRFSuiteStringOutcomeClassifierBuilder.getTrainingDataFileName;
import static ru.kfu.itis.issst.uima.ml.TieredFeatureExtractors.FILENAME_FEATURE_EXTRACTION_CONFIG;

/**
 * @author Rinat Gareev
 */
public class TrainTCRF2 {

    public static void main(String[] args) throws Exception {
        TrainTCRF2 obj = new TrainTCRF2();
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

    private TrainTCRF2() {
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
        trainModels(trainingBaseDir, modelBaseDir,
                trainerArgs.toArray(new String[trainerArgs.size()]));
    }

    private void addTrainParam(List<String> params, String name, int value) {
        params.add("-p");
        params.add(name + "=" + value);
    }

    public static void trainModels(File trDataBaseDir, File modelBaseDir, String[] trainerArgs)
            throws Exception {
        // read and copy config file to output directory
        File configPropsFile = new File(trDataBaseDir, FILENAME_FEATURE_EXTRACTION_CONFIG);
        FileUtils.copyFile(configPropsFile,
                new File(modelBaseDir, FILENAME_FEATURE_EXTRACTION_CONFIG));
        //
        for (File trainingDir : trDataBaseDir.listFiles((FileFilter) directoryFileFilter())) {
            // FIXME
            if (!new File(trainingDir, getTrainingDataFileName("crfsuite")).isFile()) {
                System.err.println(format("Directory %s is skipped as it does not contain training data", trainingDir));
                continue;
            }
            File modelDir = new File(modelBaseDir, trainingDir.getName());
            // The following lines contain a few hacks to avoid
            // extensive training file duplicates reproduction
            JarClassifierBuilder<?> _classifierBuilder = JarClassifierBuilder
                    .fromTrainingDirectory(trainingDir);
            CRFSuiteStringOutcomeClassifierBuilder classifierBuilder =
                    (CRFSuiteStringOutcomeClassifierBuilder) _classifierBuilder;
            // invoke implementation-specific method (i.e., it is not declared in the interface)
            classifierBuilder.trainClassifierOnSerializedTrainingData(modelDir, trainingDir, trainerArgs);
            classifierBuilder.packageClassifier(modelDir);
        }
    }
}
