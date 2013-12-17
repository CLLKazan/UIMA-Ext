/**
 * 
 */
package ru.kfu.itis.cll.uima.io;

import static org.apache.commons.io.FileUtils.openInputStream;
import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Lists;

/**
 * Auxiliary methods to deal with input/output of external {@link Process}es.
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class ProcessIOUtils {

	public static void feedProcessInput(Process proc, File inFile, boolean closeStdIn)
			throws IOException {
		InputStream in = new BufferedInputStream(openInputStream(inFile));
		feedProcessInput(proc, in, closeStdIn);
	}

	/**
	 * @param proc
	 *            process which input stream will receive bytes from the
	 *            argument input stream
	 * @param in
	 *            input stream. Note that it is closed at the end.
	 */
	public static void feedProcessInput(
			Process proc, final InputStream in, final boolean closeStdIn)
			throws IOException {
		final OutputStream procStdIn = proc.getOutputStream();
		final List<Exception> exceptions = Lists.newLinkedList();
		Thread writerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					IOUtils.copy(in, procStdIn);
					if (closeStdIn) {
						procStdIn.flush();
						closeQuietly(procStdIn);
					}
				} catch (Exception e) {
					exceptions.add(e);
				} finally {
					closeQuietly(in);
				}
			}
		});
		writerThread.start();
		try {
			writerThread.join();
		} catch (InterruptedException e) {
			// do nothing, just set flag
			Thread.currentThread().interrupt();
		}
		if (!exceptions.isEmpty()) {
			Exception ex = exceptions.get(0);
			throw ex instanceof IOException ? (IOException) ex
					: new IOException("Unexpected exception in writing thread", ex);
		}
	}

	private ProcessIOUtils() {
	}

}
