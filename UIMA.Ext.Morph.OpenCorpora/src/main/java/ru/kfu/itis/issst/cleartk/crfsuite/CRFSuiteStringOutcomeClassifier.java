/**
 * 
 */
package ru.kfu.itis.issst.cleartk.crfsuite;

import java.io.File;
import java.util.List;

import org.cleartk.classifier.CleartkProcessingException;
import org.cleartk.classifier.Feature;
import org.cleartk.classifier.encoder.features.FeaturesEncoder;
import org.cleartk.classifier.encoder.features.NameNumber;
import org.cleartk.classifier.encoder.outcome.OutcomeEncoder;
import org.cleartk.classifier.jar.SequenceClassifier_ImplBase;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class CRFSuiteStringOutcomeClassifier extends
		SequenceClassifier_ImplBase<List<NameNumber>, String, String> {

	private File modelFile;

	public CRFSuiteStringOutcomeClassifier(File modelFile,
			FeaturesEncoder<List<NameNumber>> featuresEncoder,
			OutcomeEncoder<String, String> outcomeEncoder) {
		super(featuresEncoder, outcomeEncoder);
		this.modelFile = modelFile;
	}

	@Override
	public List<String> classify(List<List<Feature>> features) throws CleartkProcessingException {
		// TODO
		throw new UnsupportedOperationException();
	}

}
