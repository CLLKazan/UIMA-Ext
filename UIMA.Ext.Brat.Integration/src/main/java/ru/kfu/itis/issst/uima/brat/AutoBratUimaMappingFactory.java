/**
 * 
 */
package ru.kfu.itis.issst.uima.brat;

import static java.util.Arrays.asList;
import static ru.kfu.itis.cll.uima.cas.FSTypeUtils.getAnnotationType;
import static ru.kfu.itis.issst.uima.brat.PUtils.toProperJavaName;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.resource.ResourceInitializationException;
import org.nlplab.brat.configuration.BratEntityType;
import org.nlplab.brat.configuration.BratEventType;
import org.nlplab.brat.configuration.BratRelationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.apache.uima.fit.component.initialize.ConfigurationParameterInitializer;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.initializable.Initializable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import ru.kfu.itis.cll.uima.cas.FSTypeUtils;
import ru.kfu.itis.issst.uima.brat.BratUimaMapping.Builder;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class AutoBratUimaMappingFactory extends BratUimaMappingFactoryBase implements Initializable {

	public static final String PARAM_NAMESPACES_TO_SCAN = "namespacesToScan";

	private final Logger log = LoggerFactory.getLogger(getClass());

	@ConfigurationParameter(name = PARAM_NAMESPACES_TO_SCAN, mandatory = false)
	private Set<String> namespacesToScan;
	//
	private Type annotationType;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		ConfigurationParameterInitializer.initialize(this, ctx);
	}

	@Override
	public void setTypeSystem(TypeSystem ts) {
		super.setTypeSystem(ts);
		annotationType = getAnnotationType(ts);
	}

	@Override
	public BratUimaMapping getMapping() throws ResourceInitializationException {
		Builder b = BratUimaMapping.builder();
		// map entity types
		for (BratEntityType bratType : bratTypesCfg.getEntityTypes()) {
			Type uimaType = searchTypeByBratName(bratType.getName());
			if (uimaType == null) {
				log.warn("Brat entity type {} will be ignored", bratType.getName());
			} else {
				b.addEntityMapping(bratType, uimaType);
				log.info("Brat entity type {} is mapped to UIMA type {}", bratType.getName(),
						uimaType);
			}
		}
		// map relation types
		for (BratRelationType bratType : bratTypesCfg.getRelationTypes()) {
			Type uimaType = searchTypeByBratName(bratType.getName());
			if (uimaType == null) {
				log.warn("Brat relation type {} will be ignored", bratType.getName());
			} else {
				Map<Feature, String> featureRoles = Maps.newHashMap();
				for (String argName : asList(bratType.getArg1Name(), bratType.getArg2Name())) {
					Feature feat = searchFeatureByBratRoleName(uimaType, argName);
					if (feat == null) {
						log.warn("There is no feature {} in {}", argName, uimaType);
						break;
					} else {
						featureRoles.put(feat, argName);
						log.info("Argument {}#{} is mapped to feature {}", new Object[] {
								bratType.getName(), argName, feat
						});
					}
				}
				if (featureRoles.size() == 2) {
					b.addRelationMapping(bratType, uimaType, featureRoles);
					log.info("Brat relation type {} is mapped to UIMA type {}", bratType.getName(),
							uimaType);
				} else {
					log.warn("Brat relation type {} will be ignored. Look at previous warnings",
							bratType.getName());
				}
			}
		}
		// map event types
		for (BratEventType bratType : bratTypesCfg.getEventTypes()) {
			Type uimaType = searchTypeByBratName(bratType.getName());
			if (uimaType == null) {
				log.warn("Brat event type {} will be ignored", bratType.getName());
			} else {
				Map<Feature, String> featureRoles = Maps.newHashMap();
				for (String roleName : bratType.getRoles().keySet()) {
					Feature feat = searchFeatureByBratRoleName(uimaType, roleName);
					if (feat == null) {
						log.warn("Role {}#{} will be ignored", bratType.getName(), roleName);
					} else {
						featureRoles.put(feat, roleName);
						log.info("Role {}#{} is mapped to feature {}", new Object[] {
								bratType.getName(), roleName, feat
						});
					}
				}
				b.addEventMapping(bratType, uimaType, featureRoles);
				log.info("Brat event type {} is mapped to UIMA type {}", bratType.getName(),
						uimaType);
			}
		}
		return b.build();
	}

	private Feature searchFeatureByBratRoleName(Type uimaType, String roleName) {
		String featName = roleName;
		Feature feat = uimaType.getFeatureByBaseName(featName);
		if (feat != null) {
			return feat;
		}
		featName = toProperJavaName(featName);
		feat = uimaType.getFeatureByBaseName(featName);
		if (feat != null) {
			return feat;
		}
		if (featName.length() > 0 && Character.isUpperCase(featName.charAt(0))) {
			featName = StringUtils.uncapitalize(featName);
			feat = uimaType.getFeatureByBaseName(featName);
			if (feat != null) {
				return feat;
			}
		}
		return null;
	}

	private Type searchTypeByBratName(String bratName) {
		// generate candidate base names
		Set<String> baseNameCandidates = Sets.newLinkedHashSet();
		// add original name
		baseNameCandidates.add(bratName);
		// capitalize characters after hyphens and remove hyphens
		baseNameCandidates.add(toProperJavaName(bratName));
		// search
		for (String baseName : baseNameCandidates) {
			Type t = searchTypeByBaseName(baseName);
			if (t != null) {
				return t;
			}
		}
		return null;
	}

	private Type searchTypeByBaseName(String baseName) {
		@SuppressWarnings("unchecked")
		Iterator<Type> iter = Iterators.filter(ts.getTypeIterator(), Predicates.and(asList(
				annotationTypePredicate,
				typeBaseNamePredicate(baseName),
				namespacePredicate)));
		Type result = null;
		if (iter.hasNext()) {
			result = iter.next();
			if (iter.hasNext()) {
				throw new IllegalStateException(String.format(
						"There are at least two types with target basename: %s, %s",
						result, iter.next()));
			}
		}
		return result;
	}

	private Predicate<Type> typeBaseNamePredicate(final String baseName) {
		return new Predicate<Type>() {
			@Override
			public boolean apply(Type t) {
				return baseName.equals(t.getShortName());
			}
		};
	}

	private Predicate<Type> annotationTypePredicate = new Predicate<Type>() {
		@Override
		public boolean apply(Type t) {
			return ts.subsumes(annotationType, t);
		}
	};

	/*
	 * returns true if namespacesToScan parameter is empty
	 * OR a type belongs to some namespace from namespacesToScan
	 */
	private Predicate<Type> namespacePredicate = new Predicate<Type>() {
		@Override
		public boolean apply(Type t) {
			if (namespacesToScan == null || namespacesToScan.isEmpty()) {
				return true;
			}
			LinkedHashSet<String> tNss = FSTypeUtils.getNamespaces(t);
			return !Sets.intersection(namespacesToScan, tNss).isEmpty();
		}
	};
}
