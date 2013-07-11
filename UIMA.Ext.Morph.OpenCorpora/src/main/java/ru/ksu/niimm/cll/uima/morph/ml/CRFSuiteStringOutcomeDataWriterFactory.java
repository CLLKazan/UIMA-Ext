/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import java.io.IOException;
import java.util.List;

import org.cleartk.classifier.SequenceDataWriter;
import org.cleartk.classifier.crfsuite.CRFSuiteStringOutcomeDataWriter;
import org.cleartk.classifier.encoder.features.BooleanEncoder;
import org.cleartk.classifier.encoder.features.NameNumber;
import org.cleartk.classifier.encoder.features.NameNumberFeaturesEncoder;
import org.cleartk.classifier.encoder.features.NumberEncoder;
import org.cleartk.classifier.encoder.features.StringEncoder;
import org.cleartk.classifier.encoder.outcome.StringToStringOutcomeEncoder;
import org.cleartk.classifier.jar.SequenceDataWriterFactory_ImplBase;

/**
 * @author Rinat Gareev
 * 
 */
public class CRFSuiteStringOutcomeDataWriterFactory extends
		SequenceDataWriterFactory_ImplBase<List<NameNumber>, String, String> {

	@Override
	public SequenceDataWriter<String> createDataWriter() throws IOException {
		CRFSuiteStringOutcomeDataWriter mdw = new CRFSuiteStringOutcomeDataWriter(outputDirectory);
		if (!this.setEncodersFromFileSystem(mdw)) {
			NameNumberFeaturesEncoder fe = new NameNumberFeaturesEncoder(false, false);
			fe.addEncoder(new NumberEncoder());
			fe.addEncoder(new BooleanEncoder());
			fe.addEncoder(new StringEncoder());
			mdw.setFeaturesEncoder(fe);

			mdw.setOutcomeEncoder(new StringToStringOutcomeEncoder());
		}
		return mdw;
	}
}