/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.baseline;

import static org.apache.commons.io.FileUtils.openOutputStream;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
class DefaultWordformStore<TagType> implements WordformStore<TagType>, Serializable {

	private static final long serialVersionUID = 1771570908232250753L;

	protected transient Logger log = LoggerFactory.getLogger(getClass());

	protected Map<String, TagType> strKeyMap;
	protected Map<String, Object> metadataMap;

	@Override
	public TagType getTag(String wf) {
		return strKeyMap.get(wf);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getProperty(String key, Class<T> valueClass) {
		if (metadataMap == null) {
			return null;
		}
		return (T) metadataMap.get(key);
	}

	@Override
	public void setProperty(String key, Object value) {
		if (metadataMap == null) {
			metadataMap = Maps.newHashMap();
		}
		metadataMap.put(key, value);
	}

	@Override
	public void persist(File outFile) throws Exception {
		// serialize store object
		ObjectOutputStream modelOS = null;
		try {
			OutputStream os = new BufferedOutputStream(openOutputStream(outFile));
			modelOS = new ObjectOutputStream(os);
			modelOS.writeObject(this);
		} finally {
			IOUtils.closeQuietly(modelOS);
		}
		log.info("Succesfully serialized to {}, size = {} bytes",
				outFile, outFile.length());
	}
}