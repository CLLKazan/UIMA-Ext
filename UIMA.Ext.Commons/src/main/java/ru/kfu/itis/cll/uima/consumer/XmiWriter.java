/**
 * 
 */
package ru.kfu.itis.cll.uima.consumer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.CasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.XMLSerializer;
import org.xml.sax.SAXException;

import ru.kfu.itis.cll.uima.commons.DocumentMetadata;

/**
 * Modification of XmiWriterCasConsumer from UIMA-tools.
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class XmiWriter extends CasAnnotator_ImplBase {

	/**
	 * Name of configuration parameter that must be set to the path of a
	 * directory into which the output files will be written.
	 */
	public static final String PARAM_OUTPUTDIR = "OutputDirectory";

	private File mOutputDir;

	private int mDocNum;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		mDocNum = 0;
		mOutputDir = new File((String) ctx.getConfigParameterValue(PARAM_OUTPUTDIR));
		if (!mOutputDir.exists()) {
			mOutputDir.mkdirs();
		}
	}

	/**
	 * Processes the CAS which was populated by the TextAnalysisEngines. <br>
	 * In this case, the CAS is converted to XMI and written into the output
	 * file .
	 * 
	 * @param aCAS
	 *            a CAS which has been populated by the TAEs
	 * 
	 * @see org.apache.uima.collection.base_cpm.CasObjectProcessor#processCas(org.apache.uima.cas.CAS)
	 */
	public void process(CAS aCAS) throws AnalysisEngineProcessException {
		JCas jcas;
		try {
			jcas = aCAS.getJCas();
		} catch (CASException e) {
			throw new AnalysisEngineProcessException(e);
		}

		// retreive the filename of the input file from the CAS
		FSIterator<Annotation> it = jcas.getAnnotationIndex(DocumentMetadata.type).iterator();
		File outFile = null;
		if (it.hasNext()) {
			DocumentMetadata meta = (DocumentMetadata) it.next();
			File inFile;
			try {
				inFile = new File(new URL(meta.getSourceUri()).getPath());
				String outFileName = inFile.getName();
				if (meta.getOffsetInSource() > 0) {
					outFileName += ("_" + meta.getOffsetInSource());
				}
				outFileName += ".xmi";
				outFile = new File(mOutputDir, outFileName);
			} catch (MalformedURLException e1) {
				// invalid URL, use default processing below
			}
		}
		if (outFile == null) {
			outFile = new File(mOutputDir, "doc" + mDocNum++ + ".xmi"); // Jira UIMA-629
		}
		// serialize XCAS and write to output file
		try {
			writeXmi(jcas.getCas(), outFile);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		} catch (SAXException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	/**
	 * Serialize a CAS to a file in XMI format
	 * 
	 * @param aCas
	 *            CAS to serialize
	 * @param name
	 *            output file
	 * @throws SAXException
	 * @throws Exception
	 * 
	 * @throws ResourceProcessException
	 */
	private void writeXmi(CAS aCas, File name) throws IOException,
			SAXException {
		FileOutputStream out = null;

		try {
			// write XMI
			out = new FileOutputStream(name);
			XmiCasSerializer ser = new XmiCasSerializer(aCas.getTypeSystem());
			XMLSerializer xmlSer = new XMLSerializer(out, false);
			ser.serialize(aCas, xmlSer.getContentHandler());
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}
}