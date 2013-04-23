/**
 * 
 */
package ru.kfu.itis.issst.uima.brat.util;

import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;

import java.io.FileOutputStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GenerateTS4CasViewer {

	public static void main(String[] args) throws Exception {
		String outPath = "desc/types/TypeSystem4CasViewer.xml";
		TypeSystemDescription tsDesc = createTypeSystemDescription(
				"desc.types.test-TypeSystem");
		tsDesc = CasCreationUtils.mergeTypeSystems(Arrays.asList(tsDesc));
		FileOutputStream fos = new FileOutputStream(outPath);
		try {
			tsDesc.toXML(fos);
		} finally {
			IOUtils.closeQuietly(fos);
		}
	}

}
