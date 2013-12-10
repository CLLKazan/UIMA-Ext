/**
 * 
 */
package ru.kfu.itis.cll.uima.io;

import static org.apache.commons.io.FileUtils.openOutputStream;
import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * Some more utils. Lower case 'o' is to avoid name conflicts with other
 * IOUtils. Default encoding for overloaded methods is always UTF-8.
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class IoUtils {

	public static PrintWriter openPrintWriter(File file) throws IOException {
		return openPrintWriter(file, "utf-8");
	}

	public static PrintWriter openPrintWriter(File file, String encoding) throws IOException {
		FileOutputStream fis = openOutputStream(file);
		OutputStreamWriter isr;
		try {
			isr = new OutputStreamWriter(fis, encoding);
		} catch (UnsupportedEncodingException e) {
			closeQuietly(fis);
			throw e;
		}
		BufferedWriter br = new BufferedWriter(isr);
		return new PrintWriter(br);
	}

	private IoUtils() {
	}
}
