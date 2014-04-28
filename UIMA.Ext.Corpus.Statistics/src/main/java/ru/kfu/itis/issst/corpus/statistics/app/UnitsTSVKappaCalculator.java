package ru.kfu.itis.issst.corpus.statistics.app;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.kfu.itis.issst.corpus.statistics.dao.units.InMemoryUnitsDAO;
import ru.kfu.itis.issst.corpus.statistics.dao.units.Unit;
import ru.kfu.itis.issst.corpus.statistics.dao.units.UnitsDAO;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import de.tudarmstadt.ukp.dkpro.statistics.agreement.AnnotationStudy;
import de.tudarmstadt.ukp.dkpro.statistics.agreement.TwoRaterKappaAgreement;

public class UnitsTSVKappaCalculator {

	private final Logger logger = LoggerFactory
			.getLogger(UnitsTSVKappaCalculator.class);

	@Parameter(names = "-tsv", description = "Input TSV file", required = true)
	public String tsv;

	private UnitsDAO dao;

	public static void main(String[] args) throws IOException,
			URISyntaxException {
		UnitsTSVKappaCalculator calculator = new UnitsTSVKappaCalculator(args);
		calculator.calculate();
	}

	public UnitsTSVKappaCalculator(String[] args) throws IOException,
			URISyntaxException {
		new JCommander(this, args);

		dao = new InMemoryUnitsDAO();
		Reader tsvIn = new FileReader(tsv);
		dao.addUnitsFromTSV(tsvIn);
		IOUtils.closeQuietly(tsvIn);

	}

	public void calculate() {
		AnnotationStudy study = new AnnotationStudy(2);
		int units = 0;
		int unitsDoneBySingleAnnotator = 0;
		for (Unit unit : dao.getUnits()) {
			units++;
			String[] classes = unit.getSortedClasses();
			if (classes.length == 2) {
				study.addItemAsArray(classes);
			} else {
				unitsDoneBySingleAnnotator++;
			}
		}
		logger.info("Read units: {}. There are {} units covered by an only annotator",
				units, unitsDoneBySingleAnnotator);
		TwoRaterKappaAgreement kappa = new TwoRaterKappaAgreement(study);
		System.out.println(String.format("Kappa: %s\nObserved agr: %s\nExpected %s",
				kappa.calculateAgreement(),
				kappa.calculateObservedAgreement(), kappa.calculateExpectedAgreement()));

	}
}
