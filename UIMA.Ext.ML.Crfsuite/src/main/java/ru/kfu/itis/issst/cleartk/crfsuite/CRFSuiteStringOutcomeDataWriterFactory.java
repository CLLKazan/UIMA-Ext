/**
 * 
 */
package ru.kfu.itis.issst.cleartk.crfsuite;

import org.cleartk.ml.SequenceDataWriter;
import org.cleartk.ml.jar.SequenceDataWriterFactory_ImplBase;
import ru.kfu.itis.issst.cleartk.SerializableNameNumber;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class CRFSuiteStringOutcomeDataWriterFactory
		extends
		SequenceDataWriterFactory_ImplBase<ArrayList<SerializableNameNumber>, String, String> {

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
