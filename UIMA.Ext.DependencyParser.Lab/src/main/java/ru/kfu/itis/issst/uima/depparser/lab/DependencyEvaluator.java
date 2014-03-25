/**
 * 
 */
package ru.kfu.itis.issst.uima.depparser.lab;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import mstparser.DependencyInstance;
import mstparser.io.DependencyReader;

/**
 * Do the same things as {@link mstparser.DependencyEvaluator} but outputs
 * result into specified {@link Writer}
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class DependencyEvaluator {
	public static void evaluate(
			String goldFile, String systemOutputFile, String format, boolean hasConfidence,
			Writer _out) throws IOException {
		PrintWriter out = new PrintWriter(_out, true);
		//
		DependencyReader goldReader = DependencyReader.createDependencyReader(format);
		boolean labeled = goldReader.startReading(goldFile);

		DependencyReader predictedReader;
		if (hasConfidence) {
			predictedReader = DependencyReader.createDependencyReaderWithConfidenceScores(format);
		} else {
			predictedReader = DependencyReader.createDependencyReader(format);
		}
		boolean predLabeled = predictedReader.startReading(systemOutputFile);

		if (labeled != predLabeled)
			out.println("Gold file and predicted file appear to differ on whether or not they are labeled. Expect problems!!!");

		int total = 0;
		int corr = 0;
		int corrL = 0;
		int numsent = 0;
		int corrsent = 0;
		int corrsentL = 0;

		DependencyInstance goldInstance = goldReader.getNext();
		DependencyInstance predInstance = predictedReader.getNext();

		while (goldInstance != null) {

			int instanceLength = goldInstance.length();

			if (instanceLength != predInstance.length())
				out.println("Lengths do not match on sentence " + numsent);

			int[] goldHeads = goldInstance.heads;
			String[] goldLabels = goldInstance.deprels;
			int[] predHeads = predInstance.heads;
			String[] predLabels = predInstance.deprels;

			boolean whole = true;
			boolean wholeL = true;

			// NOTE: the first item is the root info added during nextInstance(), so we skip it.

			for (int i = 1; i < instanceLength; i++) {
				if (predHeads[i] == goldHeads[i]) {
					corr++;
					if (labeled) {
						if (goldLabels[i].equals(predLabels[i]))
							corrL++;
						else
							wholeL = false;
					}
				} else {
					whole = false;
					wholeL = false;
				}
			}
			total += instanceLength - 1; // Subtract one to not score fake root token

			if (whole)
				corrsent++;
			if (wholeL)
				corrsentL++;
			numsent++;

			goldInstance = goldReader.getNext();
			predInstance = predictedReader.getNext();
		}

		out.println("Tokens: " + total);
		out.println("Correct: " + corr);
		out.println("Unlabeled Accuracy: " + ((double) corr / total));
		out.println("Unlabeled Complete Correct: " + ((double) corrsent / numsent));
		if (labeled) {
			out.println("Labeled Accuracy: " + ((double) corrL / total));
			out.println("Labeled Complete Correct: " + ((double) corrsentL / numsent));
		}

	}
}
