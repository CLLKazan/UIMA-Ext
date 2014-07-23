/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.hunpos;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class HunposException extends Exception {

	private static final long serialVersionUID = 4705792115915258144L;

	/**
	 * 
	 */
	public HunposException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public HunposException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public HunposException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public HunposException(Throwable cause) {
		super(cause);
	}

}
