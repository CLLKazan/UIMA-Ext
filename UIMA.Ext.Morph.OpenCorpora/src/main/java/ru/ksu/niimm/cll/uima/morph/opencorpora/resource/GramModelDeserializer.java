/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import static java.lang.System.currentTimeMillis;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GramModelDeserializer {

	private static final Logger log = LoggerFactory.getLogger(GramModelDeserializer.class);

	public static GramModel from(File file) throws Exception {
		if (!file.isFile()) {
			throw new IllegalArgumentException(String.format(
					"%s is not existing file", file));
		}
		return from(new FileInputStream(file), file.toString());
	}

	public static GramModel from(InputStream in, String srcLabel) throws Exception {
		log.info("About to deserialize GramModel from InputStream of {}...", srcLabel);
		long timeBefore = currentTimeMillis();
		InputStream is = new BufferedInputStream(in);
		ObjectInputStream ois = new ObjectInputStream(is);
		GramModel gm;
		try {
			gm = (GramModel) ois.readObject();
		} finally {
			IOUtils.closeQuietly(ois);
		}
		log.info("Deserialization of GramModel finished in {} ms",
				currentTimeMillis() - timeBefore);
		return gm;
	}

	private GramModelDeserializer() {
	}

}
