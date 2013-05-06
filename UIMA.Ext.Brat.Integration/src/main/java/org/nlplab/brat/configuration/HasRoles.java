/**
 * 
 */
package org.nlplab.brat.configuration;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public interface HasRoles {

	/**
	 * 
	 * @param roleName
	 * @param t
	 * @return true if given t is legal type for role value, false - otherwise
	 * @throws IllegalArgumentException
	 *             if there is no role with given name
	 */
	boolean isLegalAssignment(String roleName, BratType t);

}