/**
 * 
 */
package ru.kfu.itis.issst.uima.brat;

import java.io.File;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class BratDocument {

	public final static String TXT_FILE_SUFFIX = ".txt";
	public final static String ANN_FILE_SUFFIX = ".ann";

	private String documentName;
	private File txtFile;
	private File annFile;

	public BratDocument(File dir, String baseName) {
		this.documentName = baseName;
		this.txtFile = new File(dir, baseName + TXT_FILE_SUFFIX);
		this.annFile = new File(dir, baseName + ANN_FILE_SUFFIX);
	}

	public boolean exists() {
		return txtFile.isFile() && annFile.isFile();
	}

	public String getDocumentName() {
		return documentName;
	}

	public File getTxtFile() {
		return txtFile;
	}

	public File getAnnFile() {
		return annFile;
	}
}