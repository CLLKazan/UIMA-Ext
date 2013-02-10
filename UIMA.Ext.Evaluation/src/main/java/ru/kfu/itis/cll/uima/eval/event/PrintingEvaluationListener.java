/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.event;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import javax.annotation.PreDestroy;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public abstract class PrintingEvaluationListener implements EvaluationListener {

	// config
	private File outputFile;
	// derived
	protected PrintWriter printer;

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

	protected void init() throws Exception {
		// init printer
		Writer writer;
		if (outputFile != null) {
			OutputStream os = new FileOutputStream(outputFile);
			writer = new BufferedWriter(new OutputStreamWriter(os, "utf-8"));
		} else {
			writer = new OutputStreamWriter(System.out);
		}
		printer = new PrintWriter(writer, true);
	}

	@PreDestroy
	protected void clean() {
		// do not close stdout!
		if (outputFile != null && printer != null) {
			printer.close();
			printer = null;
		}
	}
}