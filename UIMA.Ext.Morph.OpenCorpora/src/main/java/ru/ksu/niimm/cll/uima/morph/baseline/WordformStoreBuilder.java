/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.baseline;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
interface WordformStoreBuilder<TagType> {
	void increment(String wordString, TagType tag);

	WordformStore<TagType> build();
}