/**
 * 
 */
package ru.kfu.itis.cll.uima.eval;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class ConfigurationKeys {

	public static final String KEY_GOLD_CAS_DIR = "goldCasDirectory";
	public static final String KEY_SYSTEM_CAS_DIR = "systemCasDirectory";
	public static final String KEY_ANNOTATION_TYPES = "annotationTypes";
	public static final String TYPE_SYSTEM_DESC = "typeSystem.description";
	public static final String KEY_TYPE_SYSTEM_DESC_PATHS = TYPE_SYSTEM_DESC + ".paths";
	public static final String KEY_TYPE_SYSTEM_DESC_NAMES = TYPE_SYSTEM_DESC + ".names";
	public static final String DOCUMENT_META = "document.meta";
	public static final String KEY_DOCUMENT_META_TYPE = DOCUMENT_META + ".annotationType";
	public static final String KEY_DOCUMENT_META_URI_FEATURE = DOCUMENT_META + ".uriFeatureName";
	public static final String PREFIX_LISTENER_ID = "listener.";
	public static final String PREFIX_LISTENER_PROPERTY = "listenerProperty.";
	public static final String KEY_MATCHING_CONFIGURATION_TARGET_TYPE = "check.targetType";
	public static final String PREFIX_MATCHING_CONFIGURATION = "check.";

	// public static final String 

	private ConfigurationKeys() {
	}

}