package ru.kfu.itis.issst.uima.morph.lemmatizer.util;

import org.apache.commons.io.FileUtils;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.xml.sax.SAXException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

/**
 * @author Rinat Gareev
 */
public class GenerateNormalizedTextWriterDescriptor {
    public static void main(String[] args) throws ResourceInitializationException, IOException, SAXException {
        AnalysisEngineDescription anDesc = createEngineDescription(NormalizedTextWriter.class);
        try (FileOutputStream out = FileUtils.openOutputStream(new File(
                "src/main/resources/" +
                        NormalizedTextWriter.class.getName().replace('.', '/') + ".xml"))) {
            anDesc.toXML(new BufferedOutputStream(out));
        }
    }
}
