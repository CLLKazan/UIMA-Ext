/**
 * 
 */
package ru.kfu.itis.cll.dictionary.utils;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class FilterQuotedNames implements LineHandler {

	private int quotedNamesNum;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String handle(String line) {
		if (line.contains("\"")) {
			quotedNamesNum++;
			return null;
		}
		return line;
	}

	@Override
	public void close() {
		System.out.println("Quoted names num = " + quotedNamesNum);
	}
}