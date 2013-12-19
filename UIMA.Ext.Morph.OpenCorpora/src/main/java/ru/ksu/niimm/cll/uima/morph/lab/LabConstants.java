/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.lab;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class LabConstants {
	// task data-flow keys
	public static final String KEY_CORPUS = "Corpus";
	public static final String KEY_TRAINING_DIR = "TrainingDir";
	public static final String KEY_MODEL_DIR = "ModelDir";
	public static final String KEY_OUTPUT_DIR = "OutputDir";
	// task discriminator names
	public static final String DISCRIMINATOR_FOLD = "fold";
	public static final String DISCRIMINATOR_SOURCE_CORPUS_DIR = "srcCorpusDir";
	public static final String DISCRIMINATOR_CORPUS_SPLIT_INFO_DIR = "corpusSplitInfoDir";
	public static final String DISCRIMINATOR_POS_CATEGORIES = "posCategories";
	// config file placeholders 
	public static final String PLACEHOLDER_OUTPUT_BASE_DIR = "outputBaseDir";
	// urls & files
	public static final String MORPH_DICT_FILENAME = "dict.opcorpora.ser";
	public static final String MORPH_DICT_XML = "dict.opcorpora.xml";
	public static final String URL_RELATIVE_MORPH_DICTIONARY = "file:" + MORPH_DICT_FILENAME;
}