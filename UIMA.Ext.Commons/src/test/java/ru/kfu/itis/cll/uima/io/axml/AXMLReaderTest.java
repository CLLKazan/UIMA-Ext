/**
 * 
 */
package ru.kfu.itis.cll.uima.io.axml;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static java.util.Arrays.asList;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.junit.Assert.assertEquals;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;
import static org.uimafit.util.CasUtil.select;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.uima.cas.CAS;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.junit.Test;
import org.xml.sax.SAXException;

import ru.kfu.itis.cll.uima.cas.AnnotationUtils;
import ru.kfu.itis.cll.uima.io.axml.AXMLReader;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class AXMLReaderTest {

	private final TypeSystemDescription inputTSD = createTypeSystemDescription("test.entities-ts");

	@Test
	public void readTest1Xml() throws ResourceInitializationException, IOException, SAXException {
		CAS cas = CasCreationUtils.createCas(inputTSD, null, null);
		AXMLReader.read(new File("test-data/test1.xml"), cas);
		String expectedText = readFileToString(new File("test-data/test1.txt"), "utf-8");
		expectedText = expectedText.replaceAll("\r\n", "\n");
		assertEquals(expectedText, cas.getDocumentText());
		List<String> expectedPersons = asList(
				"Представители молодого поколения сицилийской мафии",
				"Современные «авторитеты» Cosa Nostra",
				"молодые мафиози",
				"мафиози из Палермо Доменико Палацотто",
				"он", "он",
				"Палацотто",
				"Представители органов правопорядка",
				"итальянских преступников",
				"Они",
				"членов мафии",
				"своих рядах",
				"воспитанной на соцсетях молодежи",
				"Ее члены"
				);
		List<String> expectedOrgs = asList(
				"сицилийской мафии",
				"The Telegraph",
				"Cosa Nostra",
				"издание",
				"полиции",
				"органов правопорядка",
				"мафия",
				"Преступная организация",
				"она"
				);
		List<String> expectedGPEs = asList(
				"Палермо",
				"Сицилии"
				);
		List<String> expectedTimes = asList(
				"в начале XIX века",
				"к началу XX века"
				);
		assertEquals(expectedPersons,
				transform(
						newArrayList(select(cas, cas.getTypeSystem().getType("test.Person"))),
						AnnotationUtils.coveredTextFunction()));
		assertEquals(
				expectedOrgs,
				transform(
						newArrayList(select(cas, cas.getTypeSystem().getType("test.Organization"))),
						AnnotationUtils.coveredTextFunction()));
		assertEquals(expectedGPEs,
				transform(
						newArrayList(select(cas, cas.getTypeSystem().getType("test.GPE"))),
						AnnotationUtils.coveredTextFunction()));
		assertEquals(expectedTimes,
				transform(
						newArrayList(select(cas, cas.getTypeSystem().getType("test.Time"))),
						AnnotationUtils.coveredTextFunction()));
	}
}
