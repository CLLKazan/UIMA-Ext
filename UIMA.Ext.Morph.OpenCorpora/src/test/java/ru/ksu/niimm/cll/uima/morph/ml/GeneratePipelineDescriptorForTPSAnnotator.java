/**
 *
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.metadata.Import;
import org.apache.uima.resource.metadata.MetaDataObject;
import org.apache.uima.resource.metadata.impl.Import_impl;
import org.xml.sax.SAXException;

import ru.kfu.cll.uima.segmentation.SentenceSplitter;
import ru.kfu.cll.uima.tokenizer.InitialTokenizer;
import ru.kfu.cll.uima.tokenizer.PostTokenizer;
import ru.kfu.itis.cll.uima.util.PipelineDescriptorUtils;

import com.beust.jcommander.internal.Maps;
import com.google.common.collect.ImmutableList;

/**
 * @author Rinat Gareev (Kazan Federal University)
 */
public class GeneratePipelineDescriptorForTPSAnnotator {

    public static void main(String[] args) throws UIMAException, IOException, SAXException {
        if (args.length != 1) {
            System.err.println("Provide output path!");
            System.exit(1);
        }
        File outFile = new File(args[0]);
        //
        AnalysisEngineDescription outDesc = getDescription();
        OutputStream out = FileUtils.openOutputStream(outFile);
        try {
            outDesc.toXML(out);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    public static AnalysisEngineDescription getDescription() throws UIMAException, IOException {
        Map<String, MetaDataObject> aeDescriptions = Maps.newLinkedHashMap();
        aeDescriptions.put("tokenizer", InitialTokenizer.createDescription());
        aeDescriptions.put("post-tokenizer", PostTokenizer.createDescription());
        aeDescriptions.put("sentenceSplitter", SentenceSplitter.createDescription());
        Import posTaggerDescImport = new Import_impl();
        posTaggerDescImport.setName("pos_tagger");
        aeDescriptions.put("pos-tagger", posTaggerDescImport);
        //
        return PipelineDescriptorUtils.createAggregateDescription(
                ImmutableList.copyOf(aeDescriptions.values()),
                ImmutableList.copyOf(aeDescriptions.keySet()));
    }
}
