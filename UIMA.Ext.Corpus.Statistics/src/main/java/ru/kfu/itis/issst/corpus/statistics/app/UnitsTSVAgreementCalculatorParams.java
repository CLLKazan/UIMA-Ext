package ru.kfu.itis.issst.corpus.statistics.app;

import com.beust.jcommander.Parameter;

public class UnitsTSVAgreementCalculatorParams {
	@Parameter(names = "-tsv", description = "TSV file.")
	public String tsv;

	@Parameter(names = "-annotators", description = "Count of annotators that annotate one document.")
	public int annotatorCount;
}
