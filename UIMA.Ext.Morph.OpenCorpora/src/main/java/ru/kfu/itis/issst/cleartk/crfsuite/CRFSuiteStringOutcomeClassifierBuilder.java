/**
 * 
 */
package ru.kfu.itis.issst.cleartk.crfsuite;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

import org.apache.uima.UIMAFramework;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.cleartk.classifier.encoder.features.NameNumber;
import org.cleartk.classifier.jar.JarStreams;
import org.cleartk.classifier.jar.SequenceClassifierBuilder_ImplBase;

import ru.kfu.itis.issst.crfsuite4j.CrfSuiteTraining;

/**
 * @author Rinat Gareev (Kazan Federal University)
 *         <p>
 *         Most of this code kindly borrowed from
 *         {@link org.cleartk.classifier.crfsuite.CRFSuiteStringOutcomeClassifierBuilder}
 *         implementation.
 *         </p>
 */
public class CRFSuiteStringOutcomeClassifierBuilder
		extends
		SequenceClassifierBuilder_ImplBase<CRFSuiteStringOutcomeClassifier, List<NameNumber>, String, String> {

	// config fields
	// TODO
	private final String trainingDataKey = "crfsuite";
	private final Logger logger = UIMAFramework.getLogger(getClass());

	public CRFSuiteStringOutcomeClassifierBuilder() {
	}

	@Override
	public File getTrainingDataFile(File dir) {
		return new File(dir, getTrainingDataFileName(trainingDataKey));
	}

	@Override
	protected void packageClassifier(File dir, JarOutputStream modelStream) throws IOException {
		super.packageClassifier(dir, modelStream);
		String modelFileName = getModelFileName(trainingDataKey);
		JarStreams.putNextJarEntry(modelStream, modelFileName, new File(dir, modelFileName));
	}

	@Override
	public void trainClassifier(File dir, String... args) throws Exception {
		// validate args
		CrfSuiteTrainerConfig trainerCfg = CrfSuiteTrainerConfig.fromArgs(args);
		logger.log(Level.INFO, "Start learning CRFsuite sequential classifier");
		// configure
		File modelFile = new File(dir, getModelFileName(trainingDataKey));
		File trainingDataFile = getTrainingDataFile(dir);
		CrfSuiteTraining training = new CrfSuiteTraining();
		training.setTrainingDataFile(trainingDataFile);
		training.setModelFile(modelFile);
		training.setTrainingAlgorithm(trainerCfg.getTrainingAlgorithm());
		training.setParameters(trainerCfg.getParameters());
		// run
		training.run();
		logger.log(Level.INFO, "Finished learning CRFsuite sequential classifier");
	}

	private File modelFile = null;

	/**
	 * As the filename of the model is not known the only solution is to write
	 * the model back to a temporary file
	 */
	@Override
	protected void unpackageClassifier(JarInputStream modelStream) throws IOException {
		super.unpackageClassifier(modelStream);
		JarStreams.getNextJarEntry(modelStream, getModelFileName(trainingDataKey));
		this.modelFile = File.createTempFile("model", ".crfsuite");
		this.modelFile.deleteOnExit();
		logger.log(Level.INFO, "Start writing model to " + modelFile.getAbsolutePath());

		InputStream inputStream = new DataInputStream(modelStream);
		OutputStream out = new FileOutputStream(modelFile);
		byte buf[] = new byte[1024];
		int len;
		while ((len = inputStream.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		out.close();
		inputStream.close();
		logger.log(Level.INFO, "Model is written to " + modelFile.getAbsolutePath());

	}

	@Override
	protected CRFSuiteStringOutcomeClassifier newClassifier() {
		return new CRFSuiteStringOutcomeClassifier(
				this.modelFile,
				this.featuresEncoder,
				this.outcomeEncoder);
	}

	private static final String TRAINING_DATA_FILE_EXTENSION = ".training";
	private static final String MODEL_FILE_EXTENSION = ".model";

	public static String getTrainingDataFileName(String dataKey) {
		return dataKey + TRAINING_DATA_FILE_EXTENSION;
	}

	public static String getModelFileName(String dataKey) {
		return dataKey + MODEL_FILE_EXTENSION;
	}
}