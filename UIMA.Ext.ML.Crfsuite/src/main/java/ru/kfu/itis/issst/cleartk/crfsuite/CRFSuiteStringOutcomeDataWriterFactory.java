/**
 * 
 */
package ru.kfu.itis.issst.cleartk.crfsuite;

import java.io.IOException;
import java.util.List;

import org.cleartk.classifier.SequenceDataWriter;
import org.cleartk.classifier.encoder.features.NameNumber;
import org.cleartk.classifier.jar.SequenceDataWriterFactory_ImplBase;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class CRFSuiteStringOutcomeDataWriterFactory
		extends
		SequenceDataWriterFactory_ImplBase<List<NameNumber>, String, String> {

	// public static final String PARAM_DATA_KEY = "dataKey";

	// @ConfigurationParameter(name = PARAM_DATA_KEY, mandatory = true)
	// private String dataKey;

	@Override
	public SequenceDataWriter<String> createDataWriter() throws IOException {
		CRFSuiteStringOutcomeDataWriter dw = new CRFSuiteStringOutcomeDataWriter(outputDirectory);
		setEncodersFromFileSystem(dw);
		return dw;
	}
}
