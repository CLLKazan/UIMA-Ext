/**
 * 
 */
package ru.kfu.itis.cll.uima.eval;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class EvaluationLauncher {

	private EvaluationLauncher() {
	}

	public static void main(String[] args) throws UIMAException, IOException {
		if (args.length != 3) {
			System.out.println("Usage: typeSystemDescFile goldStandardDir systemOutDir");
			return;
		}
		String typeSystemDescPath = args[0];
		String goldStandardDirPath = args[1];
		String systemOutDirPath = args[2];
	}

}