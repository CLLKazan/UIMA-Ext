/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.matching;

import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import ru.kfu.itis.cll.uima.eval.ConfigurationKeys;
import ru.kfu.itis.cll.uima.eval.matching.MatchingConfiguration.Builder;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class MatchingStrategyInitializer implements FactoryBean<MatchingConfiguration> {

	@Autowired
	private Environment environment;
	@Autowired
	private TypeSystem ts;

	@Override
	public MatchingConfiguration getObject() throws Exception {
		String targetTypeName = environment
				.getProperty(ConfigurationKeys.KEY_MATCHING_STRATEGY_TARGET_TYPE);
		Type targetType = ts.getType(targetTypeName);
		if (targetType == null) {
			throw new IllegalStateException(String.format(
					"Type %s does not exist", targetTypeName));
		}
		Builder builder = MatchingConfiguration.builder(targetType);
		String targetTypeShortName = targetType.getShortName();
		
		return builder.build();
	}

	public Class<?> getObjectType() {
		return MatchingConfiguration.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}