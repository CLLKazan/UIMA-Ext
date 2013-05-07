/**
 * 
 */
package ru.kfu.itis.issst.uima.brat;

import static ru.kfu.itis.cll.uima.util.AnnotatorUtils.annotationTypeExist;
import static ru.kfu.itis.cll.uima.util.AnnotatorUtils.featureExist;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.nlplab.brat.configuration.BratEntityType;
import org.nlplab.brat.configuration.BratEventType;
import org.nlplab.brat.configuration.BratRelationType;
import org.nlplab.brat.configuration.BratType;

import ru.kfu.itis.issst.uima.brat.UimaBratMapping.Builder;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 
 * syntax for EntitiesToBrat strings:
 * 
 * <pre>
 * &lt;UIMA_TYPE_NAME&gt; ("=>" &lt;BRAT_TYPE_NAME&gt;)?
 * </pre>
 * 
 * syntax for RelationsToBrat strings:
 * 
 * <pre>
 * &lt;UIMA_TYPE_NAME&gt; ("=>" &lt;BRAT_TYPE_NAME&gt;)? ":" &lt;Arg1FeatureName&gt; (" as " &lt;UIMA_TYPE_SHORT_NAME&gt;)? "," &lt;Arg2FeatureName&gt; (" as " &lt;UIMA_TYPE_SHORT_NAME&gt;)? <br/>
 * </pre>
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
abstract class UimaBratMappingInitializer {

	private TypeSystem ts;
	private List<EntityDefinitionValue> entityDefinitions;
	private List<StructureDefinitionValue> relationDefinitions;
	private List<StructureDefinitionValue> eventDefinitions;

	public UimaBratMappingInitializer(TypeSystem ts,
			List<EntityDefinitionValue> entityDefinitions,
			List<StructureDefinitionValue> relationDefinitions,
			List<StructureDefinitionValue> eventDefinitions) {
		this.ts = ts;
		this.entityDefinitions = entityDefinitions;
		this.relationDefinitions = relationDefinitions;
		this.eventDefinitions = eventDefinitions;
	}

	protected abstract BratEntityType getEntityType(String typeName);

	protected abstract BratRelationType getRelationType(String typeName,
			Map<String, String> argTypeNames);

	protected abstract BratEventType getEventType(String typeName,
			Map<String, String> roleTypeNames);

	UimaBratMapping create() throws AnalysisEngineProcessException {
		// mapping builder
		UimaBratMapping.Builder mpBuilder = UimaBratMapping.builder();
		// configure entity types
		for (EntityDefinitionValue entityDef : entityDefinitions) {
			Type uimaType = ts.getType(entityDef.uimaTypeName);
			annotationTypeExist(entityDef.uimaTypeName, uimaType);
			String bratTypeName = entityDef.bratTypeName;
			if (bratTypeName == null) {
				bratTypeName = uimaType.getShortName();
			}
			BratEntityType bratType = getEntityType(bratTypeName);
			checkBratType(bratType, bratTypeName);
			mpBuilder.addEntityMapping(uimaType, bratType);
		}
		// configure relation types
		for (StructureDefinitionValue relationDef : relationDefinitions) {
			String uimaTypeName = relationDef.uimaTypeName;
			Type uimaType = ts.getType(uimaTypeName);
			annotationTypeExist(uimaTypeName, uimaType);
			String bratTypeName = relationDef.bratTypeName;
			if (bratTypeName == null) {
				bratTypeName = uimaType.getShortName();
			}
			Map<String, Feature> argFeatures = Maps.newHashMap();
			Map<String, String> argTypeNames = Maps.newLinkedHashMap();
			for (RoleDefinitionValue rdv : relationDef.roleDefinitions) {
				String argFeatName = rdv.featureName;
				Feature argFeat = featureExist(uimaType, argFeatName);
				argFeatures.put(argFeatName, argFeat);
				Type argUimaType = detectRoleUimaType(mpBuilder, argFeat, rdv.asTypeName);
				BratEntityType argBratType = mpBuilder.getEntityType(argUimaType);
				argTypeNames.put(argFeatName, argBratType.getName());
			}

			BratRelationType brt = getRelationType(bratTypeName, argTypeNames);
			checkBratType(brt, bratTypeName);
			mpBuilder.addRelationMapping(uimaType, brt, argFeatures);
		}
		// configure event types
		for (StructureDefinitionValue eventDef : eventDefinitions) {
			String uimaTypeName = eventDef.uimaTypeName;
			Type uimaType = ts.getType(uimaTypeName);
			annotationTypeExist(uimaTypeName, uimaType);
			String bratTypeName = eventDef.bratTypeName;
			if (bratTypeName == null) {
				bratTypeName = uimaType.getShortName();
			}
			Map<String, Feature> roleFeatures = Maps.newHashMap();
			Map<String, String> roleTypeNames = Maps.newLinkedHashMap();

			for (RoleDefinitionValue rdv : eventDef.roleDefinitions) {
				String roleFeatName = rdv.featureName;
				Feature roleFeat = featureExist(uimaType, roleFeatName);
				roleFeatures.put(roleFeatName, roleFeat);
				Type roleUimaType = detectRoleUimaType(mpBuilder, roleFeat, rdv.asTypeName);
				BratType roleBratType = mpBuilder.getType(roleUimaType);
				roleTypeNames.put(roleFeatName, roleBratType.getName());
			}

			BratEventType bet = getEventType(bratTypeName, roleTypeNames);
			checkBratType(bet, bratTypeName);
			mpBuilder.addEventMapping(uimaType, bet, roleFeatures);
		}
		return mpBuilder.build();
	}

	private void checkBratType(BratType type, String typeName) {
		if (type == null) {
			throw new IllegalStateException(String.format(
					"Can't make mapping to not existing Brat type %s", typeName));
		}
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

class NoteMapperDefinitionValue {
	// TODO >>>
	// TODO use StringParser for parsing all *DefinitionValue instances from strings
	// TODO <<<
	// private static Pattern NOTE_MAPPER_DEF_PATTERN = Pattern.compile("")
	static NoteMapperDefinitionValue fromString(String str) {
		throw new UnsupportedOperationException();
		// TODO
	}

	final String uimaType;
	final String mapperClassName;

	public NoteMapperDefinitionValue(String uimaType, String mapperClassName) {
		this.uimaType = uimaType;
		this.mapperClassName = mapperClassName;
	}
}