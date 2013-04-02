/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.matching;

import static com.google.common.collect.Maps.newHashMap;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.HashCodeBuilder;
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
import ru.kfu.itis.cll.uima.eval.matching.TypeBasedMatcherDispatcher.Builder;

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
		properties.put("check.targetTypes", "test.Chunk");
		properties
				.put("check.Chunk",
						"checkType,feature.chunkType={primitive},feature.head={checkBoundaries},feature.dependents={ordered&ref:Word},feature.subChunks={unordered&ref:Chunk}");
		properties.put("check.Word", "checkBoundaries,feature.uid={primitive}");
		PropertyResolver propResolver = makePropertyResolver(properties);
		TypeBasedMatcherDispatcher<AnnotationFS> actualMatcher =
				new MatchingConfigurationInitializer(ts, propResolver).create();

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
				CompositeMatcher.builderForAnnotation(dependentsElemRage)
						.addBoundaryMatcher()
						.addPrimitiveFeatureMatcher("uid")
						.build(), false);
		expectedBuilder.addFSCollectionFeatureMatcher("subChunks", expectedBuilder, true);
		CompositeMatcher<AnnotationFS> expectedMatcher = expectedBuilder.build();
		assertMatchersEqual(
				TypeBasedMatcherDispatcher.<AnnotationFS> builder()
						.addSubmatcher(chunkType, expectedMatcher).build(),
				actualMatcher);
	}

	@Test
	public void testDispatcher() {
		Map<String, Object> properties = newHashMap();
		properties.put("check.targetTypes", "test.TestFirst,test.TestSecond");
		properties.put("check.TestFirst", "checkType");
		properties.put("check.TestSecond", "checkBoundaries");
		PropertyResolver propResolver = makePropertyResolver(properties);
		TypeBasedMatcherDispatcher<AnnotationFS> actualMatcher =
				new MatchingConfigurationInitializer(ts, propResolver).create();

		Type firstType = ts.getType("test.TestFirst");
		Type secondType = ts.getType("test.TestSecond");
		Builder<AnnotationFS> expectedBuilder = TypeBasedMatcherDispatcher.builder();
		expectedBuilder.addSubmatcher(firstType,
				CompositeMatcher.builderForAnnotation(firstType).addTypeChecker().build());
		expectedBuilder.addSubmatcher(secondType,
				CompositeMatcher.builderForAnnotation(secondType).addBoundaryMatcher().build());

		assertMatchersEqual(expectedBuilder.build(), actualMatcher);
	}

	private void assertMatchersEqual(Matcher<?> expected, Matcher<?> actual) {
		assertMatchersEqual(expected, actual, new HashSet<CompareTuple>());
	}

	private void assertMatchersEqual(Matcher<?> expected,
			Matcher<?> actual,
			Set<CompareTuple> compared) {
		if (compared.contains(new CompareTuple(expected, actual)))
			return;
		compared.add(new CompareTuple(expected, actual));
		if (expected instanceof MatcherBase && actual instanceof MatcherBase) {
			Collection<Matcher<?>> expSubList = ((MatcherBase<?>) expected).getSubMatchers();
			Collection<Matcher<?>> actSubList = ((MatcherBase<?>) actual).getSubMatchers();
			assertEquals(expSubList.size(), actSubList.size());
			Iterator<Matcher<?>> actSubIter = actSubList.iterator();
			for (Matcher<?> expSub : expSubList) {
				Matcher<?> actSub = actSubIter.next();
				assertMatchersEqual(expSub, actSub, compared);
			}
		} else {
			assertEquals(expected, actual);
		}
	}

	private PropertyResolver makePropertyResolver(Map<String, Object> properties) {
		MutablePropertySources propSources = new MutablePropertySources();
		propSources.addFirst(new MapPropertySource("default", properties));
		PropertyResolver result = new PropertySourcesPropertyResolver(propSources);
		return result;
	}

	@SuppressWarnings("unused")
	private String diffMsg(Object expected, Object actual) {
		return new StringBuilder("Expected:\n").append(expected)
				.append("\nBut was:\n").append(actual).toString();
	}
}

final class CompareTuple {
	private Object x;
	private Object y;

	CompareTuple(Object x, Object y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(x).append(y).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CompareTuple)) {
			return false;
		}
		CompareTuple that = (CompareTuple) obj;
		return this.x == that.x && this.y == that.y;
	}
}