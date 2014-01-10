/**
 * 
 */
package ru.kfu.itis.cll.uima.io;

import static org.apache.commons.io.FileUtils.openInputStream;
import static org.apache.commons.io.FileUtils.openOutputStream;
import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * Some more utils. Lower case 'o' is to avoid name conflicts with other
 * IOUtils. Default encoding for overloaded methods is always UTF-8.
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class IoUtils {

	public static PrintWriter openPrintWriter(File file) throws IOException {
		return openPrintWriter(file, "utf-8", false);
	}

	public static PrintWriter openPrintWriter(File file, boolean append) throws IOException {
		return openPrintWriter(file, "utf-8", append);
	}

	public static PrintWriter openPrintWriter(File file, String encoding, boolean append)
			throws IOException {
		return new PrintWriter(openBufferedWriter(file, encoding, append));
	}

	public static BufferedWriter openBufferedWriter(File file) throws IOException {
		return openBufferedWriter(file, "utf-8", false);
	}

	public static BufferedWriter openBufferedWriter(File file, String encoding, boolean append)
			throws IOException {
		FileOutputStream fos = openOutputStream(file, append);
		OutputStreamWriter osr;
		try {
			osr = new OutputStreamWriter(fos, encoding);
		} catch (UnsupportedEncodingException e) {
			closeQuietly(fos);
			throw e;
		}
		return new BufferedWriter(osr);
	}

	public static BufferedReader openReader(File file) throws IOException {
		return openReader(file, "utf-8");
	}

	public static BufferedReader openReader(File file, String encoding) throws IOException {
		FileInputStream fis = openInputStream(file);
		InputStreamReader isr;
		try {
			isr = new InputStreamReader(fis, encoding);
		} catch (UnsupportedEncodingException e) {
			closeQuietly(fis);
			throw e;
		}
		return new BufferedReader(isr);
	}

	public static void write(Properties props, File outFile) throws IOException {
		BufferedWriter bw = openBufferedWriter(outFile);
		try {
			props.store(bw, null);
		} finally {
			closeQuietly(bw);
		}
	}

	public static Properties readProperties(File inFile) throws IOException {
		return readProperties(inFile, "utf-8");
	}

	public static Properties readProperties(File inFile, String encoding) throws IOException {
		Properties props = new Properties();
		BufferedReader r = openReader(inFile, encoding);
		try {
			props.load(r);
		} finally {
			closeQuietly(r);
		}
		return props;
	}

	private IoUtils() {
	}
}
