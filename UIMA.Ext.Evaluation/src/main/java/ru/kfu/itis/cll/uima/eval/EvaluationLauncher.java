/**
 * 
 */
package ru.kfu.itis.cll.uima.eval;

import java.io.File;
import java.io.OutputStreamWriter;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class EvaluationLauncher {

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.err.println("Usage: <properties-config-filepath>");
			return;
		}
		File propsFile = new File(args[0]);
		if (!propsFile.isFile()) {
			System.err.println("Can't find file " + propsFile);
			return;
		}
		EvaluationConfig cfg = new EvaluationConfig();
		cfg.readFromProperties(propsFile);

		EvaluationContext evalCtx = new EvaluationContext();
		LoggingEvaluationListener loggingListener = new LoggingEvaluationListener(
				new OutputStreamWriter(System.out));
		evalCtx.addListener(loggingListener);

		List<SoftPrecisionRecallListener> metricListeners = Lists.newLinkedList();
		for (String curType : cfg.getAnnoTypes()) {
			SoftPrecisionRecallListener curTypeMetricListener =
					new SoftPrecisionRecallListener(curType);
			metricListeners.add(curTypeMetricListener);
			evalCtx.addListener(curTypeMetricListener);
		}

		new GoldStandardBasedEvaluation(cfg).run(evalCtx);

		// print results
		for (SoftPrecisionRecallListener metrics : metricListeners) {
			StringBuilder sb = new StringBuilder();
			sb.append("Results for type '").append(metrics.getTargetTypeName()).append("':\n");
			if (metrics.getMatchedScore() == 0 && metrics.getSpuriousScore() == 0) {
				sb.append("System did not matched any annotation of this type");
			} else {
				sb.append("Matches score:   ").append(formatAsFloating(metrics.getMatchedScore()))
						.append("\n");
				sb.append("Misses score:    ").append(formatAsFloating(metrics.getMissedScore()))
						.append("\n");
				sb.append("Spurious score:  ").append(formatAsFloating(metrics.getSpuriousScore()))
						.append("\n");
				sb.append("Precision: ").append(formatAsPercentage(metrics.getPrecision()))
						.append("\n");
				sb.append("Recall:    ").append(formatAsPercentage(metrics.getRecall()))
						.append("\n");
				sb.append("F1:        ").append(formatAsPercentage(metrics.getF1()))
						.append("\n");
			}
			System.out.println(sb.toString());
		}
	}

	private static String formatAsPercentage(float value) {
		return String.format("%.1f%%", value * 100);
	}

	private static String formatAsFloating(float value) {
		return String.format("%.2f", value);
	}

	private EvaluationLauncher() {
	}
}