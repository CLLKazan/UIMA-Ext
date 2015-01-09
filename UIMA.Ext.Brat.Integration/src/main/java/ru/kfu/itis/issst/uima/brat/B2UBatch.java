/**
 * 
 */
package ru.kfu.itis.issst.uima.brat;

import java.io.File;

import org.nlplab.brat.util.BratCorpusFileFilter;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class B2UBatch {

	private static final String TYPESYSTEM_DESC_FILENAME = "typesystem.xml";

	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.err.println("Usage: <bratCorporaBaseDir> <outputBaseDir>");
			System.exit(1);
		}
		B2UBatch instance = new B2UBatch();
		instance.bratCorporaBaseDir = new File(args[0]);
		instance.outputBaseDir = new File(args[1]);
		instance.run();
	}

	private File bratCorporaBaseDir;
	private File outputBaseDir;

	private B2UBatch() {
	}

	private void run() throws Exception {
		File tsDescFile = new File(outputBaseDir, TYPESYSTEM_DESC_FILENAME);
		if (!tsDescFile.isFile()) {
			System.err.println("Put type-system descriptor XML file into " + tsDescFile.getPath());
			System.exit(1);
		}
		for (File bratCorpus : bratCorporaBaseDir.listFiles(BratCorpusFileFilter.INSTANCE)) {
			String corpusName = bratCorpus.getName();
			File uimaCorpusDir = new File(outputBaseDir, corpusName);
			System.out.println(String.format(
					"About to start converting brat-to-uima from %s into %s",
					bratCorpus, uimaCorpusDir));
			B2U.main(new String[] {
					tsDescFile.getPath(), bratCorpus.getPath(), uimaCorpusDir.getPath() });
		}
	}
}
