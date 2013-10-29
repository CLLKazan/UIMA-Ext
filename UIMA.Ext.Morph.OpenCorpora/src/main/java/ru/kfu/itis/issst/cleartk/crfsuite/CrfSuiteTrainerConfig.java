/**
 * 
 */
package ru.kfu.itis.issst.cleartk.crfsuite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class CrfSuiteTrainerConfig {

	public static CrfSuiteTrainerConfig fromArgs(String[] args) {
		CrfSuiteTrainerConfig inst = new CrfSuiteTrainerConfig();
		new JCommander(inst, args);
		return inst;
	}

	@Parameter(names = "-a", required = true)
	private String trainingAlgorithm;
	@Parameter(names = "-p")
	private List<String> parametersList;
	// derived
	private Map<String, String> parametersMap;

	private CrfSuiteTrainerConfig() {
	}

	public CrfSuiteTrainerConfig(String trainingAlgorithm, Map<String, String> parametersMap) {
		this.trainingAlgorithm = trainingAlgorithm;
		this.parametersMap = ImmutableMap.copyOf(parametersMap);
	}

	public String getTrainingAlgorithm() {
		return trainingAlgorithm;
	}

	public Map<String, String> getParameters() {
		if (parametersMap == null) {
			parametersMap = Maps.newHashMap();
			for (String pString : parametersList) {
				ArrayList<String> pTokens = Lists.newArrayList(nameValueSplitter.split(pString));
				if (pTokens.size() != 2) {
					throw new IllegalArgumentException(String.format(
							"Can't parse parameter assignment string '%s'", pString));
				}
				parametersMap.put(pTokens.get(0), pTokens.get(1));
			}
			parametersMap = ImmutableMap.copyOf(parametersMap);
		}
		return parametersMap;
	}

	private static final Splitter nameValueSplitter = Splitter.on('=').trimResults();
}