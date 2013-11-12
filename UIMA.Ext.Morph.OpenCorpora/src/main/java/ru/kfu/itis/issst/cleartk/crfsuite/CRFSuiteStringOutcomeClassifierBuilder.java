/**
 * 
 */
package ru.kfu.itis.issst.cleartk.crfsuite;

import static org.apache.commons.io.FileUtils.copyFile;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.List;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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
		trainClassifier(dir, dir, args);
	}

	/**
	 * 
	 * @param modelDir
	 *            The directory where the trained classifier should be stored
	 * @param trainingDir
	 *            The directory where training data and other classifier
	 *            information has been written.
	 * @param args
	 *            Additional command line arguments for the classifier trainer.
	 * @throws Exception
	 */
	public void trainClassifier(File modelDir, File trainingDir, String... args) throws Exception {
		if (!modelDir.getAbsoluteFile().equals(trainingDir.getAbsoluteFile())) {
			// copy encoders description file
			File srcEncFile = getEncodersFile(trainingDir);
			File targetEncFile = getEncodersFile(modelDir);
			copyFile(srcEncFile, targetEncFile);
		}
		// validate args
		CrfSuiteTrainerConfig trainerCfg = CrfSuiteTrainerConfig.fromArgs(args);
		logger.log(Level.INFO, "Start learning CRFsuite sequential classifier");
		// configure
		File modelFile = new File(modelDir, getModelFileName(trainingDataKey));
		File trainingDataFile = getTrainingDataFile(trainingDir);
		CrfSuiteTraining training = new CrfSuiteTraining();
		training.setModelFile(modelFile);
		training.setTrainingAlgorithm(trainerCfg.getTrainingAlgorithm());
		training.setParameters(trainerCfg.getParameters());
		Reader trainingDataReader;
		{
			FileInputStream is = FileUtils.openInputStream(trainingDataFile);
			trainingDataReader = new InputStreamReader(is);
			training.setTrainingDataReader(trainingDataReader);
		}
		try {
			// run
			training.run();
			logger.log(Level.INFO, "Finished learning CRFsuite sequential classifier");
		} finally {
			IOUtils.closeQuietly(trainingDataReader);
		}
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