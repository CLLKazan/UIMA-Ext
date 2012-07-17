/**
 * 
 */
package ru.kfu.itis.cll.dictionary.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class Utils {

	private Utils() {
	}

	public static BufferedReader reader(File file) throws IOException {
		FileInputStream is = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(is, "utf-8");
		return new BufferedReader(isr);
	}

	public static PrintWriter writer(File file) throws IOException {
		FileOutputStream os = new FileOutputStream(file);
		OutputStreamWriter osr = new OutputStreamWriter(os, "utf-8");
		return new PrintWriter(new BufferedWriter(osr), true);
	}
}