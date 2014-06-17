/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.commons;

import java.util.BitSet;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public interface TwoTagPredicate {

	boolean apply(BitSet first, BitSet second);

}
