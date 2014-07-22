/**
 * 
 */
package ru.kfu.itis.issst.cleartk.crfsuite;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.cleartk.classifier.CleartkProcessingException;
import org.cleartk.classifier.encoder.features.NameNumber;
import org.cleartk.classifier.encoder.features.StringEncoder;
import org.cleartk.classifier.encoder.outcome.StringToStringOutcomeEncoder;
import org.cleartk.classifier.jar.SequenceDataWriter_ImplBase;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class CRFSuiteStringOutcomeDataWriter
		extends
		SequenceDataWriter_ImplBase<CRFSuiteStringOutcomeClassifierBuilder, List<NameNumber>, String, String> {

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
	protected void writeEncoded(List<NameNumber> features, String outcome)
			throws CleartkProcessingException {
		this.trainingDataWriter.print(outcome);
		for (NameNumber nameNumber : features) {
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
