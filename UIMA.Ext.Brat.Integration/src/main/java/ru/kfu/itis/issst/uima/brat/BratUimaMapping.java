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

/**
 * @author Rinat Gareev
 * 
 */
public class BratUimaMapping {

	private Map<BratEntityType, Type> entityTypeMappings;
	private Map<BratRelationType, BratUimaRelationMapping> relationTypeMappings;
	private Map<BratEventType, BratUimaEventMapping> eventTypeMappings;

	public Set<BratEntityType> getEntityTypes() {
		return entityTypeMappings.keySet();
	}

	public Type getEntityUimaType(BratEntityType bType) {
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
}

abstract class BratUimaStructureMapping<BT extends BratType> {
	final BT bratType;
	final Type uimaType;
	final Map<Feature, String> featureRoles;

	BratUimaStructureMapping(BT bratType, Type uimaType, Map<Feature, String> featureRoles) {
		this.bratType = bratType;
		this.uimaType = uimaType;
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
			Map<Feature, String> featureRoles) {
		super(bratType, uimaType, featureRoles);
		Collection<String> argNames = featureRoles.values();
		if (featureRoles.size() != 2 || !argNames.contains(bratType.getArg1Name())
				|| !argNames.contains(bratType.getArg2Name())) {
			raiseIllegalMapping();
		}
	}
}

class BratUimaEventMapping extends BratUimaStructureMapping<BratEventType> {

	public BratUimaEventMapping(BratEventType bratType, Type uimaType,
			Map<Feature, String> featureRoles) {
		super(bratType, uimaType, featureRoles);
		for (String roleName : featureRoles.values()) {
			if (!bratType.hasRole(roleName)) {
				raiseIllegalMapping();
			}
		}
	}
}