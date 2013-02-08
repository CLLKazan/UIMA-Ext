/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.matching;

import static com.google.common.collect.Maps.newHashMap;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.PropertySourcesPropertyResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import ru.kfu.itis.cll.uima.eval.TypeSystemInitializer;
import ru.kfu.itis.cll.uima.eval.matching.CompositeMatcher.AnnotationMatcherBuilder;

/**
 * @author Rinat Gareev
 * 
 */
@ContextConfiguration(classes = MatchingConfigurationInitializerTest.AppContext.class)
public class MatchingConfigurationInitializerTest extends AbstractJUnit4SpringContextTests {

	@Configuration
	@PropertySource("classpath:MatchingConfigurationInitializerTest.properties")
	public static class AppContext {
		@Bean
		public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
			return new PropertySourcesPlaceholderConfigurer();
		}

		@Bean
		public TypeSystemInitializer typeSystemInitializer() {
			return new TypeSystemInitializer();
		}
	}

	@Autowired
	private TypeSystem ts;

	@Test
	public void testChunkMatcher() {
		Map<String, Object> properties = newHashMap();
		properties.put("check.targetType", "test.Chunk");
		properties
				.put("check.Chunk",
						"checkType,feature.chunkType={primitive},feature.head={checkBoundaries},feature.dependents={ordered&checkBoundaries}");
		PropertyResolver propResolver = makePropertyResolver(properties);
		CompositeMatcher<AnnotationFS> actualMatcher = new MatchingConfigurationInitializer(ts,
				propResolver).create();

		Type chunkType = ts.getType("test.Chunk");
		// build expected config
		AnnotationMatcherBuilder expectedBuilder = CompositeMatcher.builderForAnnotation(chunkType);
		expectedBuilder.addTypeChecker();
		expectedBuilder.addPrimitiveFeatureMatcher("chunkType");
		Type headRange = chunkType.getFeatureByBaseName("head").getRange();
		expectedBuilder.addFSFeatureMatcher("head",
				CompositeMatcher.builderForAnnotation(headRange).addBoundaryMatcher().build());
		Type dependentsElemRage = chunkType.getFeatureByBaseName("dependents").getRange()
				.getComponentType();
		expectedBuilder.addFSCollectionFeatureMatcher("dependents",
				CompositeMatcher.builderForAnnotation(dependentsElemRage).addBoundaryMatcher()
						.build(), false);
		CompositeMatcher<AnnotationFS> expectedMatcher = expectedBuilder.build();
		assertEquals(expectedMatcher, actualMatcher);
	}

	private PropertyResolver makePropertyResolver(Map<String, Object> properties) {
		MutablePropertySources propSources = new MutablePropertySources();
		propSources.addFirst(new MapPropertySource("default", properties));
		PropertyResolver result = new PropertySourcesPropertyResolver(propSources);
		return result;
	}

}