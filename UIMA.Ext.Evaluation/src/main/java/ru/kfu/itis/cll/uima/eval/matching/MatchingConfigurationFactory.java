/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.matching;

import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class MatchingConfigurationFactory implements
		FactoryBean<TypeBasedMatcherDispatcher<AnnotationFS>> {

	@Autowired
	private Environment environment;
	@Autowired
	private TypeSystem ts;

	// state
	private TypeBasedMatcherDispatcher<AnnotationFS> instance;

	@Override
	public TypeBasedMatcherDispatcher<AnnotationFS> getObject() throws Exception {
		if (instance == null) {
			instance = new MatchingConfigurationInitializer(ts, environment).create();
		}
		return instance;
	}

	public Class<?> getObjectType() {
		return TypeBasedMatcherDispatcher.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}