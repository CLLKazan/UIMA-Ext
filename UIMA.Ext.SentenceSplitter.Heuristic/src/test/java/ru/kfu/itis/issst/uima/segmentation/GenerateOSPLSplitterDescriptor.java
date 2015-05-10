package ru.kfu.itis.issst.uima.segmentation;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Rinat Gareev
 */
public class GenerateOSPLSplitterDescriptor {
    public static void main(String[] args) throws UIMAException, IOException, SAXException {
        String outputPath = "src/main/resources/"
                + OneSentencePerLineSplitter.class.getName().replace('.', '/') + ".xml";
        AnalysisEngineDescription desc = OneSentencePerLineSplitter.createDescription();
        FileOutputStream out = FileUtils.openOutputStream(new File(outputPath));
        try {
            desc.toXML(out);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }
}
