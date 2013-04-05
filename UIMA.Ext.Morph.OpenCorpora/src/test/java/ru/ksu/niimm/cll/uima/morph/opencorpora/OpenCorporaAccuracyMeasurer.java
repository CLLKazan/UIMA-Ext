package ru.ksu.niimm.cll.uima.morph.opencorpora;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.apache.commons.io.IOUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.FileResourceSpecifier;
import org.apache.uima.util.XMLInputSource;
import org.opencorpora.cas.Word;
import ru.kfu.itis.cll.uima.cas.FSUtils;

import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: fsqcds
 * Date: 3/10/13
 * Time: 10:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class OpenCorporaAccuracyMeasurer {
    private static Set<String> importantGrammems = ImmutableSet.of(
            "masc", "femn", "neut",

            "nomn", "voct",
            "gent", "gen1", "gen2",
            "datv",
            "accs", "acc2",
            "ablt",
            "loct", "loc1", "loc2",

            "sing", "plur",
            "pres", "futr", "past",
            "impr",
            "INFN",
            "PRTF", "PRTS",
            "GRND",
            "actv", "pssv",
            "1per", "2per", "3per"
    );

    public static void main(String[] args) throws IOException, UIMAException {
        if (args.length != 3) {
            System.err.println("Usage: <opcorpora-dict-without-test-selection> <opcorpora-test-selection-dict> " +
                    "<opcorpora-test-selection-textfile>");
            return;
        }

        XMLInputSource aeDescInputBig = new XMLInputSource(
                "target/test-classes/opencorpora/ae-ru-cvd-MorphAnnotator.xml");
        AnalysisEngineDescription aeDescBig = UIMAFramework.getXMLParser()
                .parseAnalysisEngineDescription(aeDescInputBig);

        XMLInputSource aeDescInputSmall = new XMLInputSource(
                "target/test-classes/opencorpora/ae-ru-cvd-MorphAnnotator.xml");
        AnalysisEngineDescription aeDescSmall = UIMAFramework.getXMLParser()
                .parseAnalysisEngineDescription(aeDescInputSmall);

//        ExternalResourceDescription dictDescription = new
        ((FileResourceSpecifier)aeDescBig.getResourceManagerConfiguration().getExternalResources()[0].getResourceSpecifier()).setFileUrl(args[0]);
        ((FileResourceSpecifier)aeDescSmall.getResourceManagerConfiguration().getExternalResources()[0].getResourceSpecifier()).setFileUrl(args[1]);

        AnalysisEngine aeBig = UIMAFramework.produceAnalysisEngine(aeDescBig);
        AnalysisEngine aeSmall = UIMAFramework.produceAnalysisEngine(aeDescSmall);


        JCas casBig = aeBig.newJCas();
        JCas casSmall = aeSmall.newJCas();

        String textString = IOUtils.toString(new FileReader(args[2]));
        casBig.setDocumentText(textString);
        casSmall.setDocumentText(textString);

        aeBig.process(casBig);
        aeSmall.process(casSmall);

        AnnotationIndex<Annotation> wordIdxPredicted = casBig.getAnnotationIndex(Word.type);
        AnnotationIndex<Annotation> wordIdxFromDict = casSmall.getAnnotationIndex(Word.type);

        int t = 0;
        int n = 0;

        Iterator<Annotation> predictedIterator = wordIdxPredicted.iterator();
        Iterator<Annotation> fromDictIterator = wordIdxFromDict.iterator();

        while (predictedIterator.hasNext()) {

            Word predicted = (Word)predictedIterator.next();
            Word fromDict = (Word)fromDictIterator.next();

            for (int i = 0; i < fromDict.getWordforms().size(); ++i) {
                n++;
                org.opencorpora.cas.Wordform fromDictWf = fromDict.getWordforms(i);

                if (hasCommon(predicted, fromDictWf))
                    t++;
            }
        }

        System.out.println("Accuracy: " + (double)t / n);
    }

    private static boolean hasCommon(Word predicted, org.opencorpora.cas.Wordform fromDictWf) {

        String fromDictPos = fromDictWf.getPos();
        Set<String> fromDictGrammems = FSUtils.grammemsToSet(fromDictWf.getGrammems());
        fromDictGrammems = Sets.intersection(fromDictGrammems, importantGrammems);

        for (int j = 0; j < predicted.getWordforms().size(); ++j) {
            org.opencorpora.cas.Wordform predictedWf = predicted.getWordforms(j);
            String predictedPos = predictedWf.getPos();

            if (!fromDictPos.equals(predictedPos))
                continue;
            Set<String> predictedGrammems = FSUtils.grammemsToSet(predictedWf.getGrammems());

            if (predictedGrammems.containsAll(fromDictGrammems)) {
                return true;
            }
        }

        return false;
    }
}
