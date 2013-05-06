package ru.ksu.niimm.cll.uima.morph.opencorpora;

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
 * Created with IntelliJ IDEA. User: fsqcds Date: 3/10/13 Time: 10:51 PM To
 * change this template use File | Settings | File Templates.
 */
public class OpenCorporaAccuracyMeasurer {
	public static void main(String[] args) throws IOException, UIMAException {
		if (args.length != 3) {
			System.err
					.println("Usage: <opcorpora-dict-without-test-selection> <opcorpora-test-selection-dict> "
							+
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
		((FileResourceSpecifier) aeDescBig.getResourceManagerConfiguration().getExternalResources()[0]
				.getResourceSpecifier()).setFileUrl(args[0]);
		((FileResourceSpecifier) aeDescSmall.getResourceManagerConfiguration()
				.getExternalResources()[0].getResourceSpecifier()).setFileUrl(args[1]);

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
			n++;

			Word predicted = (Word) predictedIterator.next();
			Word fromDict = (Word) fromDictIterator.next();

			if (hasCommon(predicted, fromDict)) {
				t++;
			}
		}

		System.out.println("Accuracy: " + (double) t / n);
	}

	private static boolean hasCommon(Word predicted, Word fromDict) {
		System.out.println("#################################");
		System.out.println(predicted.getCoveredText());
		System.out.println(fromDict.getCoveredText());

		for (int i = 0; i < predicted.getWordforms().size(); ++i) {
			org.opencorpora.cas.Wordform predictedWf = predicted.getWordforms(i);
			String predictedPos = predictedWf.getPos();
			Set<String> predictedGrammems = FSUtils.toSet(predictedWf.getGrammems());

			System.out.println("------------");
			System.out.println(predictedPos);
			System.out.println(predictedGrammems);
			System.out.println("^^^^^^^^^^^^");

			for (int j = 0; j < fromDict.getWordforms().size(); ++j) {
				org.opencorpora.cas.Wordform secondWf = fromDict.getWordforms(j);
				String fromDictPos = secondWf.getPos();

				System.out.println(fromDictPos);

				if (!fromDictPos.equals(predictedPos))
					continue;
				Set<String> fromDictGrammems = FSUtils.toSet(secondWf.getGrammems());

				System.out.println(fromDictGrammems);

				if (predictedGrammems.containsAll(fromDictGrammems)) {
					return true;
				}
			}
		}

		System.out.println("NOT PREDICTED");
		return false;
	}
}
