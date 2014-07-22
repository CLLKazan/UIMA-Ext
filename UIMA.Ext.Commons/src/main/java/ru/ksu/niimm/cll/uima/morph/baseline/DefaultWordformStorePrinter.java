/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.baseline;

import static org.apache.commons.io.FileUtils.openInputStream;
import static org.apache.commons.io.FileUtils.openOutputStream;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.commons.io.IOUtils.toBufferedInputStream;
import static org.apache.commons.lang3.SerializationUtils.deserialize;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
@Parameters(separators = " =")
public class DefaultWordformStorePrinter {

	public static void main(String[] args) throws Exception {
		DefaultWordformStorePrinter printer = new DefaultWordformStorePrinter();
		new JCommander(printer).parse(args);
		printer.run();
	}

	@Parameter(names = "-f", required = true)
	private File serFile;
	@Parameter(names = "-t")
	private File outFile;

	private void run() throws Exception {
		// deserialize
		DefaultWordformStore<?> ws = (DefaultWordformStore<?>) deserialize(toBufferedInputStream(
				openInputStream(serFile)));
		// print
		PrintWriter out;
		boolean closeOut;
		if (outFile == null) {
			out = new PrintWriter(System.out, true);
			closeOut = false;
		} else {
			OutputStream os = openOutputStream(outFile);
			out = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(os, "utf-8")), true);
			closeOut = true;
		}
		try {
			for (Map.Entry<String, ?> e : ws.strKeyMap.entrySet()) {
				out.print(escapeTabs(e.getKey()));
				out.print('\t');
				out.print(e.getValue());
				out.println();
			}
		} finally {
			if (closeOut)
				closeQuietly(out);
		}
	}

	private static final String escapeTabs(String src) {
		return src.replaceAll("\t", "\\t");
	}
}