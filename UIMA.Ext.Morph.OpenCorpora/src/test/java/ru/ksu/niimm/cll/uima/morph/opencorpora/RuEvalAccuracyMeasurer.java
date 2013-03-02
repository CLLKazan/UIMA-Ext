package ru.ksu.niimm.cll.uima.morph.opencorpora;

import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.util.XMLInputSource;
import org.opencorpora.cas.Word;
import ru.kfu.itis.cll.uima.commons.DocumentMetadata;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: fsqcds
 * Date: 2/20/13
 * Time: 10:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class RuEvalAccuracyMeasurer {
    private static String documentText = "";
    private static Map<String, String> ruEvalPOS = Maps.newHashMap();
    private static Map<String, Set<String>> ruEvalGram = Maps.newHashMap();


    public static void main(String[] args) throws IOException, UIMAException {
        if (args.length != 1) {
            System.err.println("Usage: <ru-eval-standart>");
            return;
        }

        File inputFile = new File(args[0]);

        LineIterator it = FileUtils.lineIterator(inputFile, "windows-1251");
        it.nextLine();
        try {
            while (it.hasNext()) {
                String line = it.nextLine();
                processLine(line);
            }
        } finally {
            LineIterator.closeQuietly(it);
        }


        XMLInputSource aeDescInput = new XMLInputSource(
                "target/test-classes/opencorpora/ae-ru-test-MorphAnnotator.xml");
        AnalysisEngineDescription aeDesc = UIMAFramework.getXMLParser()
                .parseAnalysisEngineDescription(aeDescInput);

        // create AE
        AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(aeDesc);

        // prepare input
        System.out.println(documentText);
        String inputText = documentText;
        JCas cas = ae.newJCas();
        cas.setDocumentText(inputText);
        DocumentMetadata inputMeta = new DocumentMetadata(cas);
        inputMeta.setSourceUri(inputFile.toURI().toString());
        inputMeta.addToIndexes();

        ae.process(cas);
        AnnotationIndex<Annotation> wordIdx = cas.getAnnotationIndex(Word.type);

        int t = 0;

        for (Annotation word : wordIdx) {
            if (isCorrect((Word)word)) {
                t++;
            }
        }

        System.out.println("Accuracy: " + (double)t / ruEvalPOS.size());
    }

    private static boolean isCorrect(Word word) {
        String wordStr = word.getCoveredText();
        System.out.println("------------------------------");
        for (int i = 0; i < word.getWordforms().size(); ++i) {
            org.opencorpora.cas.Wordform casWf = word.getWordforms(i);
            String pos = casWf.getPos();
            String[] grammemsArray = new String[casWf.getGrammems().size()];
            casWf.getGrammems().copyToArray(0, grammemsArray, 0, grammemsArray.length);
            Set<String> grammems = new HashSet<String>(Arrays.asList(grammemsArray));

            if (i > 0) System.out.println("------");
            System.out.println(wordStr);
            System.out.println(ruEvalPOS.get(wordStr));
            System.out.println(pos);
            System.out.println(opCorporaPOSToRuEval(pos, grammems));
            System.out.println();
            System.out.println(ruEvalGram.get(wordStr));
            System.out.println(grammems);
            System.out.println(opCorporaGramToRuEval(pos, grammems));
            if (opCorporaPOSToRuEval(pos, grammems).equals(ruEvalPOS.get(wordStr)) &&
                opCorporaGramToRuEval(pos, grammems).containsAll(ruEvalGram.get(wordStr))) {
                return true;
            }
        }
        return false;
    }

    private static void processLine(String line) {
        if (line.isEmpty())
            return;
        String[] parts = line.split("\\t", -1);
        String word = parts[0];
        String pos = parts[2];
        String[] gram = parts[3].split(",");

        documentText += word + "\n";
        ruEvalPOS.put(word, pos);
        ruEvalGram.put(word, new HashSet<String>(Arrays.asList(gram)));
    }

    private static String opCorporaPOSToRuEval(String ocPOS, Set<String> ocGrammems) {
        if (ocPOS.equals("NOUN")) {
            return "S";
        } else if (ocPOS.equals("ADJF") || ocPOS.equals("ADJS")) {
            return "A";
        } else if (ocPOS.equals("VERB") || ocPOS.equals("INFN") || ocPOS.equals("PRTF") ||
                   ocPOS.equals("PRTS") || ocPOS.equals("GRND")) {
            return "V";
        } else if (ocPOS.equals("PREP")) {
            return "PR";
        } else if (ocPOS.equals("CONJ")) {
            return "CONJ";
        } else if (ocPOS.equals("ADVB") || ocGrammems.contains("Prnt") || ocPOS.equals("PRCL") ||
                   ocPOS.equals("INTJ")) {
            return "ADV";
        }
        return ocPOS;
    }

    private static Set<String> opCorporaGramToRuEval(String ocPOS, Set<String> ocGrammems) {
        Set<String> result = new HashSet<String>();
        if (ocGrammems.contains("masc")) {
            result.add("m");
        } else if (ocGrammems.contains("femn")) {
            result.add("f");
        } else if (ocGrammems.contains("neut")) {
            result.add("n");
        }

        if (ocGrammems.contains("nomn") || ocGrammems.contains("voct")) {
            result.add("nom");
        } else if (ocGrammems.contains("gent") || ocGrammems.contains("gen1") || ocGrammems.contains("gen2")) {
            result.add("gen");
        } else if (ocGrammems.contains("datv")) {
            result.add("dat");
        } else if (ocGrammems.contains("accs") || ocGrammems.contains("acc2")) {
            result.add("acc");
        } else if (ocGrammems.contains("ablt")) {
            result.add("ins");
        } else if (ocGrammems.contains("loct") || ocGrammems.contains("loc1") || ocGrammems.contains("loc2")) {
            result.add("loc");
        }

        if (ocGrammems.contains("sing")) {
            result.add("sg");
        } else if (ocGrammems.contains("plur")) {
            result.add("pl");
        }

        if (ocGrammems.contains("pres") || ocGrammems.contains("futr")) {
            result.add("pres");
        } else if (ocGrammems.contains("past")) {
            result.add("past");
        }

        if (ocGrammems.contains("impr")) {
            result.add("imper");
        }

        if (ocPOS.equals("INFN")) {
            result.add("inf");
        } else if (ocPOS.equals("PRTF") || ocPOS.equals("PRTS")) {
            result.add("partcp");
        } else if (ocPOS.equals("GRND")) {
            result.add("ger");
        }

        if (ocGrammems.contains("actv")) {
            result.add("act");
        } else if (ocGrammems.contains("pssv")) {
            result.add("pass");
        }

        if (ocGrammems.contains("1per")) {
            result.add("1p");
        } else if (ocGrammems.contains("2per")) {
            result.add("2p");
        } else if (ocGrammems.contains("3per")) {
            result.add("3p");
        }

        return result;
    }
}
