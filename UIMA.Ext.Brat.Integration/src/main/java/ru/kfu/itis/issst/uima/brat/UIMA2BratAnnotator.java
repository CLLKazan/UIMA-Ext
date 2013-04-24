package ru.kfu.itis.issst.uima.brat;

import static ru.kfu.itis.cll.uima.util.AnnotatorUtils.annotationTypeExist;
import static ru.kfu.itis.cll.uima.util.AnnotatorUtils.featureExist;
import static ru.kfu.itis.issst.uima.brat.BratConstants.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.resource.ResourceInitializationException;
import org.nlplab.brat.ann.BratAnnotation;
import org.nlplab.brat.ann.BratAnnotationContainer;
import org.nlplab.brat.ann.BratEntity;
import org.nlplab.brat.ann.BratEvent;
import org.nlplab.brat.ann.BratEventTrigger;
import org.nlplab.brat.ann.BratRelation;
import org.nlplab.brat.configuration.BratEntityType;
import org.nlplab.brat.configuration.BratEventType;
import org.nlplab.brat.configuration.BratRelationType;
import org.nlplab.brat.configuration.BratType;
import org.nlplab.brat.configuration.BratTypesConfiguration;
import org.nlplab.brat.configuration.EventRole;
import org.uimafit.component.CasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.OperationalProperties;
import org.uimafit.util.CasUtil;

import ru.kfu.itis.issst.uima.brat.UimaBratMapping.Builder;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * UIMA Annotator is CAS Annotator to convert UIMA annotations to brat standoff
 * format annotations. 1) defines input, ouput files directories 2) reading
 * annotator descriptor file and converts parameters to brat configuration file
 * saved as annotation.conf 3) saves annotations text file using specified file
 * name parameter in DocumentMetadata annotations. 4) reading UIMA annotations
 * and converts them to brat annotation (*.ann files)
 * 
 * T: text-bound annotation R: relation E: event A: attribute M: modification
 * (alias for attribute, for backward compatibility) N: normalization #: note
 * 
 * For event annotation you have to add additional info about event entities
 * into desc file
 * 
 * @author pathfinder
 * @author Rinat Gareev (Kazan Federal University)
 */
@OperationalProperties(modifiesCas = false, multipleDeploymentAllowed = false)
public class UIMA2BratAnnotator extends CasAnnotator_ImplBase {

	public final static String BRAT_OUT = "BratOutputDir";
	public final static String ENTITIES_TO_BRAT = "EntitiesToBrat";
	public final static String RELATIONS_TO_BRAT = "RelationsToBrat";
	public final static String EVENTS_TO_BRAT = "EventsToBrat";
	public final static String DOC_META_TYPE = "DocumentMetadataType";
	public final static String DOC_META_URI_FEATURE = "DocumentMetadataUriFeature";
	public final static String CONF_FILE = "annotation.conf";

	// annotator configuration fields
	@ConfigurationParameter(name = BRAT_OUT, mandatory = true)
	private File bratDirectory;
	@ConfigurationParameter(name = ENTITIES_TO_BRAT)
	private String[] entitiesToBratRaw;
	private List<EntityDefinitionValue> entitiesToBrat;
	@ConfigurationParameter(name = RELATIONS_TO_BRAT)
	private String[] relationsToBratRaw;
	private List<StructureDefinitionValue> relationsToBrat;
	@ConfigurationParameter(name = EVENTS_TO_BRAT)
	private String[] eventsToBratRaw;
	private List<StructureDefinitionValue> eventsToBrat;
	@ConfigurationParameter(name = DOC_META_TYPE, defaultValue = "ru.kfu.itis.cll.uima.commons.DocumentMetadata")
	private String docMetaTypeName;
	private Type docMetaType;
	@ConfigurationParameter(name = DOC_META_URI_FEATURE, defaultValue = "sourceUri")
	private String docMetaUriFeatName;
	private Feature docMetaUriFeature;

	// derived configuration fields
	private BratTypesConfiguration bratTypesConfig;
	private UimaBratMapping mapping;

	// state fields
	private TypeSystem ts;

	// per-CAS state fields
	private String currentDocName;
	private BratAnnotationContainer bac;
	private ToBratMappingContext context;

	@Override
	public void initialize(UimaContext ctx)
			throws ResourceInitializationException {
		super.initialize(ctx);

		getLogger().info("Annotator is initializing ...");
		if (entitiesToBratRaw == null) {
			entitiesToBrat = ImmutableList.of();
		} else {
			entitiesToBrat = Lists.newLinkedList();
			for (String valStr : entitiesToBratRaw) {
				entitiesToBrat.add(EntityDefinitionValue.fromString(valStr));
			}
			entitiesToBrat = ImmutableList.copyOf(entitiesToBrat);
		}
		if (relationsToBratRaw == null) {
			relationsToBrat = ImmutableList.of();
		} else {
			relationsToBrat = Lists.newLinkedList();
			for (String valStr : relationsToBratRaw) {
				StructureDefinitionValue val = StructureDefinitionValue.fromString(valStr);
				if (val.roleDefinitions.size() != 2) {
					throw new IllegalArgumentException(String.format(
							"Illegal relation definition: %s", valStr));
				}
				relationsToBrat.add(val);
			}
			relationsToBrat = ImmutableList.copyOf(relationsToBrat);
		}
		if (eventsToBratRaw == null) {
			eventsToBrat = ImmutableList.of();
		} else {
			eventsToBrat = Lists.newLinkedList();
			for (String valStr : eventsToBratRaw) {
				eventsToBrat.add(StructureDefinitionValue.fromString(valStr));
			}
			eventsToBrat = ImmutableList.copyOf(eventsToBrat);
		}
	}

	@Override
	public void typeSystemInit(TypeSystem ts)
			throws AnalysisEngineProcessException {
		super.typeSystemInit(ts);
		this.ts = ts;
		getLogger().info("Configuring document metadata & uri retrieval...");
		docMetaType = ts.getType(docMetaTypeName);
		annotationTypeExist(docMetaTypeName, docMetaType);
		docMetaUriFeature = featureExist(docMetaType, docMetaUriFeatName);

		getLogger().info("Reading UIMA types to convert to brat annotations ... ");
		createBratTypesConfiguration();
		Writer acWriter = null;
		try {
			if (!bratDirectory.isDirectory())
				bratDirectory.mkdirs();
			File annotationConfFile = new File(bratDirectory, CONF_FILE);
			acWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(annotationConfFile), ANNOTATION_CONF_ENCODING));
			bratTypesConfig.writeTo(acWriter);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		} finally {
			IOUtils.closeQuietly(acWriter);
		}
	}

	@Override
	public void process(CAS cas) throws AnalysisEngineProcessException {
		// extract target file name
		currentDocName = extractDocName(cas);
		// prepare paths
		BratDocument bratDoc = new BratDocument(bratDirectory, currentDocName);
		// write doc text
		String txt = cas.getDocumentText();
		try {
			FileUtils.write(bratDoc.getTxtFile(), txt, TXT_FILES_ENCODING);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}

		// populate Brat annotation container
		bac = new BratAnnotationContainer();
		context = new ToBratMappingContext();
		// start with entities
		for (Type uType : mapping.getEntityUimaTypes()) {
			BratEntityType bType = mapping.getBratEntityType(uType);
			for (AnnotationFS uEntity : cas.getAnnotationIndex(uType)) {
				mapEntity(bType, uEntity);
			}
		}
		// then relations
		for (Type uType : mapping.getRelationUimaTypes()) {
			UimaBratRelationMapping relMapping = mapping.getRelationMapping(uType);
			for (AnnotationFS uRelation : cas.getAnnotationIndex(uType)) {
				mapRelation(relMapping, uRelation);
			}
		}
		// then events
		for (Type uType : mapping.getEventUimaTypes()) {
			UimaBratEventMapping evMapping = mapping.getEventMapping(uType);
			for (AnnotationFS uEvent : cas.getAnnotationIndex(uType)) {
				mapEvent(evMapping, uEvent);
			}
		}
		// write .ann file
		Writer annWriter = null;
		try {
			annWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(bratDoc.getAnnFile()), ANN_FILES_ENCODING));
			bac.writeTo(annWriter);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		} finally {
			IOUtils.closeQuietly(annWriter);
		}
		// clear per-CAS state
		currentDocName = null;
		bac = null;
		context = null;
	}

	private void mapEntity(BratEntityType bType, AnnotationFS uEntity) {
		if (context.isMapped(uEntity)) {
			return;
		}
		// create brat annotation instance
		BratEntity bEntity = new BratEntity(bType,
				uEntity.getBegin(), uEntity.getEnd(), uEntity.getCoveredText());
		// add to container - it assigns ID
		bEntity = bac.add(bEntity);
		// remember that it was mapped => put into mappedAnnos
		context.mapped(uEntity, bEntity);
	}

	private void mapRelation(UimaBratRelationMapping relMapping, AnnotationFS uRelation) {
		if (context.isMapped(uRelation)) {
			return;
		}
		BratRelationType bType = relMapping.bratType;
		Map<String, BratEntity> argMap = makeArgMap(
				uRelation, bType, relMapping.roleFeatures);
		if (argMap == null) {
			return;
		}
		// create
		BratRelation bRelation = new BratRelation(bType, argMap);
		// assign id
		bRelation = bac.add(bRelation);
		// memorize
		context.mapped(uRelation, bRelation);
	}

	private void mapEvent(UimaBratEventMapping evMapping, AnnotationFS uEvent) {
		if (context.isMapped(uEvent)) {
			return;
		}
		BratEventType bType = evMapping.bratType;
		// use UIMA event annotation boundaries as Brat event trigger boundaries
		BratEventTrigger trigger = new BratEventTrigger(bType,
				uEvent.getBegin(), uEvent.getEnd(), uEvent.getCoveredText());
		// assign id to trigger
		trigger = bac.add(trigger);
		// fill slots
		Map<String, BratAnnotation<?>> roleAnnotations = makeRoleMap(
				uEvent, bType, evMapping.roleFeatures);
		// create
		BratEvent bEvent = new BratEvent(bType, trigger, roleAnnotations);
		// assign id
		bEvent = bac.add(bEvent);
		// memorize
		context.mapped(uEvent, bEvent);
	}

	// fill relation roles
	private Map<String, BratEntity> makeArgMap(AnnotationFS uAnno,
			BratRelationType bratType, Map<String, Feature> argFeatMap) {
		Map<String, BratEntity> argAnnotations = Maps.newHashMapWithExpectedSize(2);
		for (String argName : argFeatMap.keySet()) {
			Feature argFeat = argFeatMap.get(argName);
			FeatureStructure argFS = uAnno.getFeatureValue(argFeat);
			if (argFS == null) {
				getLogger().warn(String.format(
						"Can't map %s to Brat relation. Its feature '%s' is not set.",
						toPrettyString(uAnno), argFeat));
				return null;
			}
			BratEntity argValue = context.demandEntity(argFS);
			argAnnotations.put(argName, argValue);
		}
		return argAnnotations;
	}

	// fill event roles
	private Map<String, BratAnnotation<?>> makeRoleMap(AnnotationFS uAnno,
			BratEventType bratType, Map<String, Feature> roleFeatMap) {
		Map<String, BratAnnotation<?>> roleAnnotations = Maps.newHashMapWithExpectedSize(
				roleFeatMap.size());
		for (String roleName : roleFeatMap.keySet()) {
			EventRole roleDesc = bratType.getRole(roleName);
			Feature roleFeat = roleFeatMap.get(roleName);
			FeatureStructure roleFS = uAnno.getFeatureValue(roleFeat);
			if (roleFS == null) {
				continue;
			}
			BratAnnotation<?> roleValue;
			if (roleDesc.getRange() instanceof BratEntityType) {
				roleValue = context.demandEntity(roleFS);
			} else { // role value should be event
				roleValue = context.getEvent(roleFS, false);
				if (roleValue == null) {
					// means that a sub-event has not been mapped yet
					// TODO implement nested event mapping
					throw new UnsupportedOperationException(
							"Nested event mapping is not supported yet");
				}
			}
			roleAnnotations.put(roleName, roleValue);
		}
		return roleAnnotations;
	}

	private String toPrettyString(AnnotationFS anno) {
		return String.format("<%s, offset %s in %s>", anno.getCoveredText(), anno.getBegin(),
				currentDocName);
	}

	private String toPrettyString(FeatureStructure fs) {
		if (fs instanceof AnnotationFS) {
			return toPrettyString((AnnotationFS) fs);
		}
		return String.valueOf(fs);
	}

	private String extractDocName(CAS cas) {
		FeatureStructure docMeta = CasUtil.selectSingle(cas, docMetaType);
		String uriStr = docMeta.getStringValue(docMetaUriFeature);
		if (uriStr == null) {
			throw new IllegalStateException(String.format("Value of %s is null", docMetaUriFeature));
		}
		URI uri;
		try {
			uri = new URI(uriStr);
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
		File docFile = new File(uri);
		String docName = FilenameUtils.getBaseName(docFile.getPath());
		if (StringUtils.isBlank(docName)) {
			throw new IllegalStateException(String.format(
					"Can't extract doc name from uri: %s", uri));
		}
		return docName;
	}

	/*
	 * syntax for EntitiesToBrat strings:
	 * <UIMA_TYPE_NAME> ("=>" <BRAT_TYPE_NAME>)?
	 * syntax for RelationsToBrat strings:
	 * <UIMA_TYPE_NAME> ("=>" <BRAT_TYPE_NAME>)? ":" <Arg1FeatureName> (" as " <UIMA_TYPE_SHORT_NAME>)? "," <Arg2FeatureName> (" as " <UIMA_TYPE_SHORT_NAME>)?
	 */
	private void createBratTypesConfiguration() throws AnalysisEngineProcessException {
		// mapping builder
		UimaBratMapping.Builder mpBuilder = UimaBratMapping.builder();
		// type configuration builder
		BratTypesConfiguration.Builder tcBuilder = BratTypesConfiguration.builder();
		// configure entity types
		for (EntityDefinitionValue entityDef : entitiesToBrat) {
			Type uimaType = ts.getType(entityDef.uimaTypeName);
			annotationTypeExist(entityDef.uimaTypeName, uimaType);
			String bratTypeName = entityDef.bratTypeName;
			if (bratTypeName == null) {
				bratTypeName = uimaType.getShortName();
			}
			BratEntityType bratType = tcBuilder.addEntityType(bratTypeName);
			mpBuilder.addEntityMapping(uimaType, bratType);
		}
		// configure relation types
		for (StructureDefinitionValue relationDef : relationsToBrat) {
			String uimaTypeName = relationDef.uimaTypeName;
			Type uimaType = ts.getType(uimaTypeName);
			annotationTypeExist(uimaTypeName, uimaType);
			String bratTypeName = relationDef.bratTypeName;
			if (bratTypeName == null) {
				bratTypeName = uimaType.getShortName();
			}
			Map<String, Feature> argFeatures = Maps.newHashMap();
			Map<String, BratEntityType> argTypes = Maps.newLinkedHashMap();
			for (RoleDefinitionValue rdv : relationDef.roleDefinitions) {
				String argFeatName = rdv.featureName;
				Feature argFeat = featureExist(uimaType, argFeatName);
				argFeatures.put(argFeatName, argFeat);
				Type argUimaType = detectRoleUimaType(mpBuilder, argFeat, rdv.asTypeName);
				BratEntityType argBratType = mpBuilder.getEntityType(argUimaType);
				argTypes.put(argFeatName, argBratType);
			}

			BratRelationType brt = tcBuilder.addRelationType(bratTypeName, argTypes);
			mpBuilder.addRelationMapping(uimaType, brt, argFeatures);
		}
		// configure event types
		for (StructureDefinitionValue eventDef : eventsToBrat) {
			String uimaTypeName = eventDef.uimaTypeName;
			Type uimaType = ts.getType(uimaTypeName);
			annotationTypeExist(uimaTypeName, uimaType);
			String bratTypeName = eventDef.bratTypeName;
			if (bratTypeName == null) {
				bratTypeName = uimaType.getShortName();
			}
			Map<String, Feature> roleFeatures = Maps.newHashMap();
			Map<String, BratType> roleTypes = Maps.newLinkedHashMap();

			for (RoleDefinitionValue rdv : eventDef.roleDefinitions) {
				String roleFeatName = rdv.featureName;
				Feature roleFeat = featureExist(uimaType, roleFeatName);
				roleFeatures.put(roleFeatName, roleFeat);
				Type roleUimaType = detectRoleUimaType(mpBuilder, roleFeat, rdv.asTypeName);
				BratType roleBratType = mpBuilder.getType(roleUimaType);
				roleTypes.put(roleFeatName, roleBratType);
			}

			BratEventType bet = tcBuilder.addEventType(bratTypeName, roleTypes);
			mpBuilder.addEventMapping(uimaType, bet, roleFeatures);
		}
		bratTypesConfig = tcBuilder.build();
		mapping = mpBuilder.build();
	}

	private Type detectRoleUimaType(Builder mpBuilder, Feature roleFeat,
			String shortTypeNameHint) {
		Type uRoleType;
		if (shortTypeNameHint == null) {
			uRoleType = roleFeat.getRange();
		} else {
			uRoleType = mpBuilder.getUimaTypeByShortName(shortTypeNameHint);
			if (!ts.subsumes(roleFeat.getRange(), uRoleType)) {
				throw new IllegalStateException(String.format(
						"%s is not subtype of %s", uRoleType, roleFeat.getRange()));
			}
		}
		return uRoleType;
	}

	private class ToBratMappingContext {
		private Map<AnnotationFS, BratAnnotation<?>> mappedAnnos = Maps.newHashMap();

		private boolean isMapped(AnnotationFS anno) {
			return mappedAnnos.containsKey(anno);
		}

		private BratEntity demandEntity(FeatureStructure fs) {
			return getMapped(fs, BratEntity.class, true);
		}

		private BratEvent getEvent(FeatureStructure fs, boolean require) {
			return getMapped(fs, BratEvent.class, require);
		}

		@SuppressWarnings("unchecked")
		private <B extends BratAnnotation<?>> B getMapped(
				FeatureStructure fs, Class<B> targetClass, boolean require) {
			BratAnnotation<?> result = mappedAnnos.get(fs);
			if (result == null) {
				if (require) {
					throw new IllegalStateException(String.format(
							"Can't find mapped instance for %s in %s",
							toPrettyString(fs), currentDocName));
				}
				return null;
			}
			if (!targetClass.isInstance(result)) {
				throw new IllegalStateException(String.format(
						"Unexpected mapped instance type for %s:\n required: %s\n actual:%s",
						toPrettyString(fs), targetClass.getName(), result.getClass().getName()));
			}
			return (B) result;
		}

		private void mapped(AnnotationFS uAnno, BratAnnotation<?> bAnno) {
			mappedAnnos.put(uAnno, bAnno);
		}
	}
}

class EntityDefinitionValue {
	private static final String P_NAME_MAPPING = "([._\\p{Alnum}]+)\\s*(=>\\s*([_\\p{Alnum}]+))?";
	static final Pattern ENTITY_TYPE_MAPPING_PATTERN = Pattern.compile(P_NAME_MAPPING);

	static EntityDefinitionValue fromString(String str) {
		Matcher matcher = ENTITY_TYPE_MAPPING_PATTERN.matcher(str);
		if (matcher.matches()) {
			String uimaTypeName = matcher.group(1);
			String bratTypeName = matcher.group(3);
			return new EntityDefinitionValue(uimaTypeName, bratTypeName);
		} else {
			throw new IllegalStateException(String.format(
					"Can't parse entity mapping param value:\n%s", str));
		}
	}

	final String uimaTypeName;
	final String bratTypeName;

	EntityDefinitionValue(String uimaTypeName, String bratTypeName) {
		this.uimaTypeName = uimaTypeName;
		this.bratTypeName = bratTypeName;
	}
}

// base for events and relations
class StructureDefinitionValue {
	private static final Pattern BEFORE_ROLES = Pattern.compile("\\s*:\\s*");

	static StructureDefinitionValue fromString(String _src) {
		String src = _src;
		Matcher typeNamesMatcher = EntityDefinitionValue.ENTITY_TYPE_MAPPING_PATTERN.matcher(src);
		if (typeNamesMatcher.lookingAt()) {
			String uimaTypeName = typeNamesMatcher.group(1);
			String bratTypeName = typeNamesMatcher.group(3);
			src = skip(src.substring(typeNamesMatcher.end()), BEFORE_ROLES);
			// split by comma
			String[] roleDeclStrings = src.split("\\s*,\\s*");
			// trim trailing whitespace in last string 
			roleDeclStrings[roleDeclStrings.length - 1] =
					roleDeclStrings[roleDeclStrings.length - 1].trim();

			List<RoleDefinitionValue> roleDefs = Lists.newLinkedList();
			for (String roleDeclStr : roleDeclStrings) {
				try {
					roleDefs.add(RoleDefinitionValue.fromString(roleDeclStr));
				} catch (Exception e) {
					throw new IllegalArgumentException(String.format(
							"Can't parse: %s", _src),
							e);
				}
			}
			return new StructureDefinitionValue(uimaTypeName, bratTypeName, roleDefs);
		} else {
			throw new IllegalArgumentException(String.format(
					"Can't parse structure mapping param value:\n%s", _src));
		}
	}

	/**
	 * @param src
	 * @param pattern
	 * @return src without prefix matched by pattern
	 * @throws IllegalArgumentException
	 *             if pattern does not match prefix
	 */
	private static String skip(String src, Pattern pattern) {
		Matcher m = pattern.matcher(src);
		if (m.lookingAt()) {
			return src.substring(m.end());
		} else {
			throw new IllegalArgumentException(String.format(
					"'%s' prefix was expected in '%s'", pattern, src));
		}
	}

	final String uimaTypeName;
	final String bratTypeName;
	final List<RoleDefinitionValue> roleDefinitions;

	StructureDefinitionValue(String uimaTypeName, String bratTypeName,
			List<RoleDefinitionValue> roleDefinitions) {
		this.uimaTypeName = uimaTypeName;
		this.bratTypeName = bratTypeName;
		this.roleDefinitions = ImmutableList.copyOf(roleDefinitions);
	}

}

class RoleDefinitionValue {
	private static final Pattern ROLE_DEF_PATTERN = Pattern.compile(
			"(\\p{Alnum}+)(\\s+as\\s+([_\\p{Alnum}]+))?");

	static RoleDefinitionValue fromString(String src) {
		Matcher m = ROLE_DEF_PATTERN.matcher(src);
		if (m.matches()) {
			String featureName = m.group(1);
			String asTypeName = m.group(3);
			return new RoleDefinitionValue(featureName, asTypeName);
		} else {
			throw new IllegalArgumentException(String.format(
					"Can't parse role definition: %s", src));
		}
	}

	final String featureName;
	final String asTypeName;

	RoleDefinitionValue(String featureName, String asTypeName) {
		this.featureName = featureName;
		this.asTypeName = asTypeName;
	}

	@Override
	public int hashCode() {
		return featureName.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof RoleDefinitionValue)) {
			return false;
		}
		RoleDefinitionValue that = (RoleDefinitionValue) obj;
		return Objects.equal(this.featureName, that.featureName)
				&& Objects.equal(this.asTypeName, that.asTypeName);
	}
}