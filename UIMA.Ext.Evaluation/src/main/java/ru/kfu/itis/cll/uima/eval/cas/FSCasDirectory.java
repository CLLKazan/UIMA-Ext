/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.cas;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.uima.UIMAException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.CasCreationUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.xml.sax.SAXException;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class FSCasDirectory implements CasDirectory, BeanNameAware {

	protected String beanName;
	private File dir;
	@Autowired
	private TypeSystem ts;
	@Autowired
	private Environment env;

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
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	@PostConstruct
	@Override
	public void init() {
		this.dir = env.getProperty(beanName + ".dir", File.class);
		if (dir == null) {
			throw new IllegalStateException(String.format(
					"'dir' value is not specified for %s", beanName));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CAS getCas(String docUriStr) throws Exception {
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
	public Iterator<CAS> iterator() {
		Iterator<File> xmiFileIter = Iterators.forArray(getXmiFiles());
		return Iterators.transform(xmiFileIter, deserializeFunc());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return getXmiFiles().length;
	}

	/**
	 * Subclasses may override this to perform alignment, filtering,
	 * transformation and etc. operations on deserialized CAS.
	 * 
	 * @param cas
	 */
	protected void postProcessCAS(CAS cas) {
		// default impl do nothing
	}

	private File[] xmiFiles;

	private File[] getXmiFiles() {
		if (xmiFiles == null) {
			IOFileFilter xmiFileFilter = FileFilterUtils.suffixFileFilter(".xmi");
			xmiFiles = dir.listFiles((FileFilter) xmiFileFilter);
		}
		return xmiFiles;
	}

	private Function<File, CAS> deserializeFunc() {
		return new Function<File, CAS>() {
			@Override
			public CAS apply(File input) {
				try {
					return deserialize(input);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		};
	}

	private CAS deserialize(File xmiFile) throws UIMAException, SAXException, IOException {
		CAS cas = createCas();
		InputStream is = openStream(xmiFile);
		try {
			XmiCasDeserializer.deserialize(is, cas);
		} finally {
			IOUtils.closeQuietly(is);
		}
		postProcessCAS(cas);
		return cas;
	}

	private CAS createCas() throws ResourceInitializationException {
		return CasCreationUtils.createCas(ts, null, null, null);
	}

	private InputStream openStream(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		return new BufferedInputStream(fis);
	}
}