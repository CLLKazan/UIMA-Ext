/**
 * 
 */
package ru.kfu.itis.issst.cleartk.crfsuite;

import org.cleartk.ml.CleartkProcessingException;
import org.cleartk.ml.encoder.outcome.StringToStringOutcomeEncoder;
import org.cleartk.ml.jar.SequenceDataWriter_ImplBase;
import ru.kfu.itis.issst.cleartk.SerializableNameNumber;
import ru.kfu.itis.issst.cleartk.StringEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class CRFSuiteStringOutcomeDataWriter
		extends
		SequenceDataWriter_ImplBase<CRFSuiteStringOutcomeClassifierBuilder, ArrayList<SerializableNameNumber>, String, String> {

	private static final String FEATURE_SEPARATOR = "\t";

	public CRFSuiteStringOutcomeDataWriter(File outputDirectory)
			throws FileNotFoundException {
		super(outputDirectory);
		NameNumberFeaturesEncoder2 fe = new NameNumberFeaturesEncoder2(":\t");
		fe.addEncoder(new StringEncoder());
		this.setFeaturesEncoder(fe);
		this.setOutcomeEncoder(new StringToStringOutcomeEncoder());
	}

	@Override
	protected void writeEncoded(ArrayList<SerializableNameNumber> features, String outcome)
			throws CleartkProcessingException {
		this.trainingDataWriter.print(outcome);
		for (SerializableNameNumber nameNumber : features) {
			this.trainingDataWriter.print(FEATURE_SEPARATOR);
			this.trainingDataWriter.print(nameNumber.name);
		}
		this.trainingDataWriter.println();
	}

	@Override
	protected void writeEndSequence() {
		this.trainingDataWriter.println();
	}

	@Override
	protected CRFSuiteStringOutcomeClassifierBuilder newClassifierBuilder() {
		return new CRFSuiteStringOutcomeClassifierBuilder();
	}

}
