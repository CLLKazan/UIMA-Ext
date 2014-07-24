/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public interface GramModelPostProcessor {

	void postprocess(ImmutableGramModel.Builder gmBuilder);

}
