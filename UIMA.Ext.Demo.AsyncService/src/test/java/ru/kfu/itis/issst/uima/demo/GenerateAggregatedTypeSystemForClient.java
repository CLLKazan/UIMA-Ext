/**
 *
 */
package ru.kfu.itis.issst.uima.demo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.xml.sax.SAXException;

/**
 * @author Rinat Gareev (Kazan Federal University)
 */
public class GenerateAggregatedTypeSystemForClient {

    public static void main(String[] args) throws InvalidXMLException, IOException,
            ResourceInitializationException, SAXException {
        AnalysisEngineDescription pipeDesc = AnalysisEngineFactory.createEngineDescription(
                "ru.kfu.itis.issst.uima.demo.lemmatizer-pipeline");
        TypeSystemDescription resultTSD = CasCreationUtils.mergeDelegateAnalysisEngineTypeSystems(pipeDesc);
        File outFile = new File("target/lemmatizer-aggr-ts.xml");
        try (FileOutputStream out = FileUtils.openOutputStream(outFile)) {
            resultTSD.toXML(out);
        }
    }

}
