/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.seman;

import static java.lang.Thread.currentThread;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Rinat Gareev
 * 
 */
public class SemanMorphologyAnnotatorTest {

	private static final Logger log = Logger
			.getLogger(SemanMorphologyAnnotatorTest.class.getName());
	private static AnalysisEngine analysisEngine;

	@BeforeClass
	public static void setupUima() throws InvalidXMLException, ResourceInitializationException {
		InputStream is = currentThread().getContextClassLoader()
				.getResourceAsStream("ae-desc-testMorphAnalyzer.xml");
		XMLInputSource aeDescSource = new XMLInputSource(is, new File("."));
		ResourceSpecifier aeSpecifier = UIMAFramework.getXMLParser().parseResourceSpecifier(
				aeDescSource);
		analysisEngine = UIMAFramework.produceAnalysisEngine(aeSpecifier);
	}

	@Test
	public void test1() throws UIMAException {
		JCas cas = analysisEngine.newJCas();
		cas.setDocumentText("А́страхань. Яблоки падают. Кот мурлыкает. Трава зеленеет. Вувузелой Foobar 1234");
		analysisEngine.process(cas);
		AnnotationIndex<Annotation> wfIndex = cas.getAnnotationIndex(Wordform.type);
		Set<String> expectedLemmas = new HashSet<String>(Arrays.asList(
				"АСТРАХАНЬ", "ЯБЛОКО", "ПАДАТЬ", "КОТ", "МУРЛЫКАТЬ", "ТРАВА", "ЗЕЛЕНЕТЬ"));
		for (Annotation curAnno : wfIndex) {
			Wordform wf = (Wordform) curAnno;
			log.info(toString(wf));
			String lemma = wf.getParadigm().getLemma().toUpperCase();
			expectedLemmas.remove(lemma);
			assertNotNull(wf.getGrammems());
			assertFalse(wf.getCoveredText().startsWith("Foo")
					|| wf.getCoveredText().startsWith("12"));
			if (wf.getCoveredText().startsWith("Вуву")) {
				assertTrue(wf.getParadigm().getParadigmId() < 0);
			} else {
				assertTrue(wf.getParadigm().getParadigmId() >= 0);
			}
			assertNotNull(wf.getParadigm().getGrammems());
			assertNotNull(wf.getParadigm().getPos());
		}
		assertTrue(expectedLemmas.isEmpty());
	}

	@AfterClass
	public static void closeUima() {
		if (analysisEngine != null) {
			analysisEngine.destroy();
		}
	}

	private String toString(Wordform wf) {
		ToStringBuilder sb = new ToStringBuilder(wf, ToStringStyle.SHORT_PREFIX_STYLE);
		sb.append("coveredText", wf.getCoveredText());
		sb.append("flexionNo", wf.getFlexionNo());
		sb.append("grammems", Arrays.toString(wf.getGrammems().toStringArray()));
		sb.append("paradigm", toString(wf.getParadigm()));
		return sb.toString();
	}

	private String toString(Paradigm para) {
		if (para == null) {
			return null;
		}
		ToStringBuilder sb = new ToStringBuilder(para, ToStringStyle.SHORT_PREFIX_STYLE);
		sb.append("coveredText", para.getCoveredText());
		sb.append("paradigmId", para.getParadigmId());
		sb.append("pos", para.getPos());
		sb.append("lemma", para.getLemma());
		sb.append("grammems", Arrays.toString(para.getGrammems().toStringArray()));
		return sb.toString();
	}
}