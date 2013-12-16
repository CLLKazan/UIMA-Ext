/**
 * 
 */
package ru.kfu.itis.cll.uima.io;

import java.io.InputStream;

/**
 * Borrowed from {@link TreeTaggerWrapper}
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public abstract class StreamGobblerBase implements Runnable {

	public static StreamGobblerBase toSystemOut(InputStream in) {
		return new StreamGobblerBase(in) {
			@Override
			protected void write(String str) {
				System.out.print(str);
			}

			@Override
			protected void onDone() {
				System.out.println();
			}
		};
	}

	private final InputStream in;
	private boolean done = false;
	private Throwable exception;

	public StreamGobblerBase(InputStream in) {
		this.in = in;
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
						write(new String(buffer, 0, br));
					}
				}
				Thread.sleep(100);
			}
			onDone();
		} catch (final Throwable e) {
			exception = e;
		}
	}

	public Throwable getException() {
		return exception;
	}

	protected abstract void write(String str);

	protected abstract void onDone();
}
