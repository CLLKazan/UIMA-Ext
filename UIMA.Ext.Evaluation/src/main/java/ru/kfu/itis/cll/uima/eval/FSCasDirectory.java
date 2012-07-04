/**
 * 
 */
package ru.kfu.itis.cll.uima.eval;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.uima.UIMAException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.CasCreationUtils;
import org.xml.sax.SAXException;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class FSCasDirectory implements CasDirectory {

	private File dir;
	private TypeSystem ts;

	@Override
	public void setTypeSystem(TypeSystem ts) {
		this.ts = ts;
	}

	public void setDir(File dir) {
		this.dir = dir;
		if (!dir.isDirectory()) {
			throw new IllegalStateException(dir + " is not file directory");
		}
	}

	@Override
	public void init() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JCas getCas(String docUriStr) throws Exception {
		URI docUri = new URI(docUriStr);
		File docFile = new File(docUri);
		String docFileName = docFile.getName();
		String xmiFileName = docFileName + ".xmi";
		File xmiFile = new File(dir, xmiFileName);
		if (!xmiFile.isFile()) {
			throw new IllegalStateException("Not a file: " + xmiFile);
		}
		return deserialize(xmiFile);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<JCas> iterator() {
		IOFileFilter xmiFileFilter = FileFilterUtils.suffixFileFilter(".xmi");
		Iterator<File> xmiFileIter = Iterators.forArray(dir.listFiles((FileFilter) xmiFileFilter));
		return Iterators.transform(xmiFileIter, deserializeFunc());
	}

	private Function<File, JCas> deserializeFunc() {
		return new Function<File, JCas>() {
			@Override
			public JCas apply(File input) {
				try {
					return deserialize(input);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		};
	}

	private JCas deserialize(File xmiFile) throws UIMAException, SAXException, IOException {
		CAS cas = createCas();
		XmiCasDeserializer.deserialize(openStream(xmiFile), cas);
		return cas.getJCas();
	}

	private CAS createCas() throws ResourceInitializationException {
		return CasCreationUtils.createCas(ts, null, null, null);
	}

	private InputStream openStream(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		return new BufferedInputStream(fis);
	}
}