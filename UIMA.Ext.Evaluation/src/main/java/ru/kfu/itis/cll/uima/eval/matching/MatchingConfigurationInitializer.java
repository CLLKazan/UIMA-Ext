/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.matching;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang3.StringUtils.split;
import static ru.kfu.itis.cll.uima.cas.FSTypeUtils.getFeature;
import static ru.kfu.itis.cll.uima.eval.ConfigurationKeys.KEY_MATCHING_CONFIGURATION_TARGET_TYPE;
import static ru.kfu.itis.cll.uima.eval.ConfigurationKeys.PREFIX_MATCHING_CONFIGURATION;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.springframework.core.env.PropertyResolver;

import ru.kfu.itis.cll.uima.cas.FSTypeUtils;
import ru.kfu.itis.cll.uima.eval.matching.CompositeMatcher.AnnotationMatcherBuilder;
import ru.kfu.itis.cll.uima.eval.matching.CompositeMatcher.Builder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * @author Rinat Gareev
 * 
 */
public class MatchingConfigurationInitializer {

	public static final String MATCHER_DELIMITER_CHARS = ",";
	public static final String SUBMATCHER_DELIMITER_CHARS = "&";
	public static final String BOUNDARY_MATCHER = "checkBoundaries";
	public static final String TYPE_MATCHER = "checkType";
	public static final String PREFIX_ATTRIBUTE_MATCHER = "feature.";

	private TypeSystem ts;
	private PropertyResolver propertyResolver;
	private Type uimaAnnotationType;

	public MatchingConfigurationInitializer(TypeSystem ts, PropertyResolver propertyResolver) {
		this.ts = ts;
		this.propertyResolver = propertyResolver;
		this.uimaAnnotationType = FSTypeUtils.getType(ts, "uima.tcas.Annotation", true);
	}

	public CompositeMatcher<AnnotationFS> create() {
		String targetTypeName = propertyResolver
				.getProperty(KEY_MATCHING_CONFIGURATION_TARGET_TYPE);
		if (targetTypeName == null) {
			throw new IllegalStateException(String.format(
					"Can't create matcher because there is no property under key %s",
					KEY_MATCHING_CONFIGURATION_TARGET_TYPE));
		}
		Type targetType = FSTypeUtils.getType(ts, targetTypeName, true);
		AnnotationMatcherBuilder builder = CompositeMatcher.builderForAnnotation(targetType);
		String targetTypeShortName = targetType.getShortName();
		String topMatcherDescKey = PREFIX_MATCHING_CONFIGURATION + targetTypeShortName;
		String topTypeMatchersDesc = propertyResolver.getProperty(topMatcherDescKey);
		if (topTypeMatchersDesc == null) {
			throw new IllegalStateException("Can't find matcher descriptions under key "
					+ topMatcherDescKey);
		}
		String[] matcherStrings = split(topTypeMatchersDesc, MATCHER_DELIMITER_CHARS);
		parseMatchersDescription(builder, Arrays.asList(matcherStrings));
		return builder.build();
	}

	private <FST extends FeatureStructure> void parseMatchersDescription(
			Builder<FST> builder, List<String> descStrings) {
		for (String matcherStr : descStrings) {
			if (!parseSingleMatcherDescription(builder, matcherStr)) {
				throw new IllegalStateException(String.format(
						"Can't parse matcher description: '%s'", matcherStr));
			}
		}
	}

	private <FST extends FeatureStructure> boolean parseSingleMatcherDescription(
			Builder<FST> builder, String matcherStr) {
		for (MatcherDescriptionParser curParser : matcherDescParsers) {
			java.util.regex.Matcher regexMatcher = curParser.descRegex.matcher(matcherStr);
			if (regexMatcher.matches()) {
				curParser.onParse(regexMatcher, builder);
				return true;
			}
		}
		return false;
	}

	private abstract class MatcherDescriptionParser {
		private Pattern descRegex;

		protected MatcherDescriptionParser(String descPatternStr) {
			this.descRegex = Pattern.compile(descPatternStr);
		}

		protected abstract <FST extends FeatureStructure> void onParse(
				java.util.regex.Matcher regexMatcher,
				Builder<FST> builder);
	}

	private final List<MatcherDescriptionParser> matcherDescParsers;
	{
		List<MatcherDescriptionParser> matcherDescParsers = Lists.newLinkedList();
		// checkBoundaries
		matcherDescParsers.add(new MatcherDescriptionParser("checkBoundaries") {
			@Override
			protected <FST extends FeatureStructure> void onParse(Matcher regexMatcher,
					Builder<FST> builder) {
				if (!(builder instanceof AnnotationMatcherBuilder)) {
					throw new IllegalStateException(
							"Can't add checkBoundaries matcher for non-annotation FS type");
				}
				((AnnotationMatcherBuilder) builder).addBoundaryMatcher();
			}
		});
		// checkType
		matcherDescParsers.add(new MatcherDescriptionParser("checkType") {
			@Override
			protected <FST extends FeatureStructure> void onParse(Matcher regexMatcher,
					Builder<FST> builder) {
				builder.addTypeChecker();
			}
		});
		// attribute matcher
		matcherDescParsers.add(new MatcherDescriptionParser("feature.(\\p{Alnum}+)=\\{([^}]*)\\}") {

			@Override
			protected <FST extends FeatureStructure> void onParse(Matcher regexMatcher,
					Builder<FST> builder) {
				String featName = regexMatcher.group(1);
				String subMatchersDesc = regexMatcher.group(2);
				List<String> subMatcherStrings = newArrayList(
						split(subMatchersDesc, SUBMATCHER_DELIMITER_CHARS));
				if (subMatcherStrings.equals(Arrays.asList("primitive"))) {
					// handle primitive-ranged feature
					builder.addPrimitiveFeatureMatcher(featName);
					return;
				}
				// handle non-primitive-ranged feature
				Boolean ignoreOrder = null;
				if (subMatcherStrings.contains("unordered")) {
					ignoreOrder = true;
					subMatcherStrings.remove("unordered");
				}
				if (subMatcherStrings.contains("ordered")) {
					ignoreOrder = false;
					subMatcherStrings.remove("ordered");
				}
				boolean isCollection = ignoreOrder != null;
				Feature feature = getFeature(builder.getTargetType(), featName, true);
				Type featRange = feature.getRange();
				if (isCollection) {
					// handle array-or-collection-ranged feature
					Type elemType = featRange.getComponentType();
					Builder<?> elemMatcherBuilder = createBuilder(elemType);
					parseMatchersDescription(elemMatcherBuilder, subMatcherStrings);
					builder.addFSCollectionFeatureMatcher(featName,
							elemMatcherBuilder.build(),
							ignoreOrder);
				} else {
					// handle FS-ranged feature
					Builder<?> valueMatcherBuilder = createBuilder(featRange);
					parseMatchersDescription(valueMatcherBuilder, subMatcherStrings);
					builder.addFSFeatureMatcher(featName, valueMatcherBuilder.build());
				}
			}
		});
		this.matcherDescParsers = ImmutableList.copyOf(matcherDescParsers);
	}

	private Builder<?> createBuilder(Type type) {
		Builder<?> result;
		if (ts.subsumes(uimaAnnotationType, type)) {
			result = CompositeMatcher.builderForAnnotation(type);
		} else {
			result = CompositeMatcher.builderForFS(type);
		}
		return result;
	}
}