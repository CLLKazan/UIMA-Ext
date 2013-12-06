/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.treetagger;

import java.io.InputStream;

/**
 * Borrowed from {@link TreeTaggerWrapper}
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
class StreamGobbler implements Runnable {
	private final InputStream in;
	private boolean done = false;
	private Throwable exception;

	public StreamGobbler(final InputStream aIn) {
		in = aIn;
	}

	public void done() {
		done = true;
	}

	@Override
	public void run() {
		byte[] buffer = new byte[1024];
		try {
			while (!done) {
				while (in.available() > 0) {
					int br = in.read(buffer, 0, Math.min(buffer.length, in.available()));
					if (br > 0) {
						System.out.print(new String(buffer, 0, br));
					}
				}
				Thread.sleep(100);
			}
			System.out.println();
		} catch (final Throwable e) {
			exception = e;
		}
	}

	public Throwable getException() {
		return exception;
	}
}
