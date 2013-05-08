/**
 * 
 */
package ru.kfu.itis.issst.uima.brat;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.nlplab.brat.configuration.BratEntityType;
import org.nlplab.brat.configuration.BratEventType;
import org.nlplab.brat.configuration.BratRelationType;
import org.nlplab.brat.configuration.BratType;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * @author Rinat Gareev
 * 
 */
public class BratUimaMapping {

	private Map<BratEntityType, BratUimaEntityMapping> entityTypeMappings;
	private Map<BratRelationType, BratUimaRelationMapping> relationTypeMappings;
	private Map<BratEventType, BratUimaEventMapping> eventTypeMappings;

	private BratUimaMapping() {
	}

	public Set<BratEntityType> getEntityTypes() {
		return entityTypeMappings.keySet();
	}

	public BratUimaEntityMapping getEntityMapping(BratEntityType bType) {
		return entityTypeMappings.get(bType);
	}

	public Set<BratRelationType> getRelationTypes() {
		return relationTypeMappings.keySet();
	}

	public BratUimaRelationMapping getRelationMapping(BratRelationType bType) {
		return relationTypeMappings.get(bType);
	}

	public Set<BratEventType> getEventTypes() {
		return eventTypeMappings.keySet();
	}

	public BratUimaEventMapping getEventMapping(BratEventType bType) {
		return eventTypeMappings.get(bType);
	}

	public static BratUimaMapping reverse(UimaBratMapping src) {
		BratUimaMapping result = new BratUimaMapping();
		result.entityTypeMappings = Maps.newHashMap();
		result.relationTypeMappings = Maps.newHashMap();
		result.eventTypeMappings = Maps.newHashMap();
		//
		for (Type uType : src.getEntityUimaTypes()) {
			UimaBratEntityMapping srcEntMapping = src.getEntityMapping(uType);
			BratEntityType bType = srcEntMapping.bratType;
			BratUimaEntityMapping entMapping = new BratUimaEntityMapping(bType, uType,
					srcEntMapping.noteMapper);
			if (result.entityTypeMappings.put(bType, entMapping) != null) {
				reportAmbiguousReversal(bType);
			}
		}
		//
		for (Type uType : src.getRelationUimaTypes()) {
			UimaBratRelationMapping srcRelMapping = src.getRelationMapping(uType);
			BratRelationType bType = srcRelMapping.bratType;
			BratUimaRelationMapping relMapping = new BratUimaRelationMapping(
					bType, uType, reverseMap(srcRelMapping.roleFeatures), srcRelMapping.noteMapper);
			if (result.relationTypeMappings.put(bType, relMapping) != null) {
				reportAmbiguousReversal(bType);
			}
		}
		//
		for (Type uType : src.getEventUimaTypes()) {
			UimaBratEventMapping srcEvMapping = src.getEventMapping(uType);
			BratEventType bType = srcEvMapping.bratType;
			BratUimaEventMapping evMapping = new BratUimaEventMapping(
					bType, uType, reverseMap(srcEvMapping.roleFeatures), srcEvMapping.noteMapper);
			if (result.eventTypeMappings.put(bType, evMapping) != null) {
				reportAmbiguousReversal(bType);
			}
		}
		//
		result.entityTypeMappings = ImmutableMap.copyOf(result.entityTypeMappings);
		result.relationTypeMappings = ImmutableMap.copyOf(result.relationTypeMappings);
		result.eventTypeMappings = ImmutableMap.copyOf(result.eventTypeMappings);
		return result;
	}

	private static void reportAmbiguousReversal(BratType bType) {
		throw new IllegalStateException(String.format(
				"Ambiguous mapping for Brat type: %s", bType));
	}

	private static <K, V> Map<V, K> reverseMap(Map<K, V> srcMap) {
		Map<V, K> resultMap = Maps.newHashMapWithExpectedSize(srcMap.size());
		for (K key : srcMap.keySet()) {
			V value = srcMap.get(key);
			if (resultMap.put(value, key) != null) {
				throw new IllegalArgumentException(String.format(
						"Can't reverse map %s", srcMap));
			}
		}
		return resultMap;
	}
}

abstract class BratUimaTypeMappingBase<BT extends BratType> {
	final BT bratType;
	final Type uimaType;
	final BratNoteMapper noteMapper;

	public BratUimaTypeMappingBase(BT bratType, Type uimaType, BratNoteMapper noteMapper) {
		this.bratType = bratType;
		this.uimaType = uimaType;
		this.noteMapper = noteMapper;
	}
}

class BratUimaEntityMapping extends BratUimaTypeMappingBase<BratEntityType> {
	public BratUimaEntityMapping(BratEntityType bratType, Type uimaType, BratNoteMapper noteMapper) {
		super(bratType, uimaType, noteMapper);
	}
}

abstract class BratUimaStructureMapping<BT extends BratType> extends BratUimaTypeMappingBase<BT> {

	final Map<Feature, String> featureRoles;

	BratUimaStructureMapping(BT bratType, Type uimaType, Map<Feature, String> featureRoles,
			BratNoteMapper noteMapper) {
		super(bratType, uimaType, noteMapper);
		this.featureRoles = ImmutableMap.copyOf(featureRoles);
		// sanity check
		List<Feature> utFeatures = uimaType.getFeatures();
		for (Feature f : featureRoles.keySet()) {
			if (!utFeatures.contains(f)) {
				throw new IllegalArgumentException(String.format(
						"Feature %s does not belong to type %s",
						f, uimaType));
			}
		}
	}

	protected void raiseIllegalMapping() {
		throw new IllegalArgumentException(String.format(
				"Illegal arg mapping %s for Brat type %s",
				featureRoles, bratType));
	}
}

class BratUimaRelationMapping extends BratUimaStructureMapping<BratRelationType> {

	public BratUimaRelationMapping(BratRelationType bratType, Type uimaType,
			Map<Feature, String> featureRoles, BratNoteMapper noteMapper) {
		super(bratType, uimaType, featureRoles, noteMapper);
		Collection<String> argNames = featureRoles.values();
		if (featureRoles.size() != 2 || !argNames.contains(bratType.getArg1Name())
				|| !argNames.contains(bratType.getArg2Name())) {
			raiseIllegalMapping();
		}
	}
}

class BratUimaEventMapping extends BratUimaStructureMapping<BratEventType> {

	public BratUimaEventMapping(BratEventType bratType, Type uimaType,
			Map<Feature, String> featureRoles, BratNoteMapper noteMapper) {
		super(bratType, uimaType, featureRoles, noteMapper);
		for (String roleName : featureRoles.values()) {
			if (!bratType.hasRole(roleName)) {
				raiseIllegalMapping();
			}
		}
	}
}