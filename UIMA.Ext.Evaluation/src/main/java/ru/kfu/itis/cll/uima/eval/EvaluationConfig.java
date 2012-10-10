/**
 * 
 */
package ru.kfu.itis.cll.uima.eval;

import static java.lang.String.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class EvaluationConfig {

	public static final String KEY_GOLD_STANDARD_IMPL = "gold.casdir.impl";
	public static final String KEY_GOLD_STANDARD_PROPS = "gold.casdir.properties.";
	public static final String KEY_SYSTEM_OUTPUT_IMPL = "system.casdir.impl";
	public static final String KEY_SYSTEM_OUTPUT_PROPS = "system.casdir.properties.";
	public static final String KEY_ANNOTATION_TYPES = "annotationTypes";
	public static final String KEY_TYPE_SYSTEM_DESC_PATHS = "typeSystem.description.paths";
	public static final String KEY_TYPE_SYSTEM_DESC_NAMES = "typeSystem.description.names";
	public static final String KEY_DOC_URI_ANNO_TYPE = "document.meta.annotationType";
	public static final String KEY_DOC_URI_FEATURE_NAME = "document.meta.uriFeatureName";

	private String goldStandardImpl;
	private String systemOutputImpl;
	private Map<String, String> goldStandardProps;
	private Map<String, String> systemOutputProps;
	private Set<String> annoTypes;
	private String[] typeSystemDescPaths;
	private String[] typeSystemDescNames;
	private boolean stripDocumentUri = true;
	//
	private String docUriAnnotationType;
	private String docUriFeatureName;

	public void readFromProperties(File propsFile) throws IOException {
		Properties props = new Properties();
		FileInputStream fileIS = new FileInputStream(propsFile);
		try {
			props.load(fileIS);
		} finally {
			fileIS.close();
		}
		Set<String> annoTypes = toStringSet(notNull(props, KEY_ANNOTATION_TYPES), ';');
		setAnnoTypes(annoTypes);

		setDocUriAnnotationType(notNull(props, KEY_DOC_URI_ANNO_TYPE));
		setDocUriFeatureName(notNull(props, KEY_DOC_URI_FEATURE_NAME));

		setGoldStandardImpl(notNull(props, KEY_GOLD_STANDARD_IMPL));
		Map<String, String> goldCDProps = filterToMap(props, KEY_GOLD_STANDARD_PROPS);
		setGoldStandardProps(goldCDProps);

		setSystemOutputImpl(notNull(props, KEY_SYSTEM_OUTPUT_IMPL));
		Map<String, String> sysCDProps = filterToMap(props, KEY_SYSTEM_OUTPUT_PROPS);
		setSystemOutputProps(sysCDProps);

		if (props.getProperty(KEY_TYPE_SYSTEM_DESC_PATHS) != null) {
			setTypeSystemDescPaths(notNull(props, KEY_TYPE_SYSTEM_DESC_PATHS).split(";"));
		}
		if (props.getProperty(KEY_TYPE_SYSTEM_DESC_NAMES) != null) {
			setTypeSystemDescNames(notNull(props, KEY_TYPE_SYSTEM_DESC_NAMES).split(";"));
		}
		if (getTypeSystemDescNames() == null && getTypeSystemDescPaths() == null) {
			throw new IllegalStateException(format(
					"None of {%s,%s} properties was specified",
					KEY_TYPE_SYSTEM_DESC_PATHS, KEY_TYPE_SYSTEM_DESC_NAMES));
		}
	}

	private static Map<String, String> filterToMap(Properties props, String prefix) {
		Map<String, String> result = Maps.newHashMap();
		for (String curKey : props.stringPropertyNames()) {
			if (curKey.startsWith(prefix)) {
				String resultKey = curKey.substring(prefix.length());
				result.put(resultKey, props.getProperty(curKey));
			}
		}
		return result;
	}

	private static String notNull(Properties props, String key) {
		String val = props.getProperty(key);
		if (val == null) {
			throw new IllegalStateException(String.format(
					"'%s' value is required!", key));
		}
		return val;
	}

	private static Set<String> toStringSet(String src, char separatorChar) {
		String[] arr = StringUtils.split(src, separatorChar);
		HashSet<String> result = Sets.newHashSetWithExpectedSize(arr.length);
		for (String curStr : arr) {
			curStr = curStr.trim();
			if (curStr.length() > 0) {
				result.add(curStr);
			}
		}
		return result;
	}

	public Set<String> getAnnoTypes() {
		return annoTypes;
	}

	public String getGoldStandardImpl() {
		return goldStandardImpl;
	}

	public void setGoldStandardImpl(String goldStandardImpl) {
		this.goldStandardImpl = goldStandardImpl;
	}

	public String getSystemOutputImpl() {
		return systemOutputImpl;
	}

	public void setSystemOutputImpl(String systemOutputImpl) {
		this.systemOutputImpl = systemOutputImpl;
	}

	public void setAnnoTypes(Set<String> annoTypes) {
		this.annoTypes = annoTypes;
	}

	public Map<String, String> getGoldStandardProps() {
		return goldStandardProps;
	}

	public void setGoldStandardProps(Map<String, String> goldStandardProps) {
		this.goldStandardProps = goldStandardProps;
	}

	public Map<String, String> getSystemOutputProps() {
		return systemOutputProps;
	}

	public void setSystemOutputProps(Map<String, String> systemOutputProps) {
		this.systemOutputProps = systemOutputProps;
	}

	public String[] getTypeSystemDescNames() {
		return typeSystemDescNames;
	}

	public void setTypeSystemDescNames(String[] typeSystemDescNames) {
		this.typeSystemDescNames = typeSystemDescNames;
	}

	public String[] getTypeSystemDescPaths() {
		return typeSystemDescPaths;
	}

	public void setTypeSystemDescPaths(String[] typeSystemDescPaths) {
		this.typeSystemDescPaths = typeSystemDescPaths;
	}

	public String getDocUriAnnotationType() {
		return docUriAnnotationType;
	}

	public void setDocUriAnnotationType(String docUriAnnotationType) {
		this.docUriAnnotationType = docUriAnnotationType;
	}

	public String getDocUriFeatureName() {
		return docUriFeatureName;
	}

	public void setDocUriFeatureName(String docUriFeatureName) {
		this.docUriFeatureName = docUriFeatureName;
	}

	public boolean isStripDocumentUri() {
		return stripDocumentUri;
	}

	public void setStripDocumentUri(boolean stripDocumentUri) {
		this.stripDocumentUri = stripDocumentUri;
	}
}