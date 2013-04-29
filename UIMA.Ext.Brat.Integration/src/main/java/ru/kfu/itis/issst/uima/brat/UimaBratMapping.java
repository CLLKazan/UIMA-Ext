/**
 * 
 */
package ru.kfu.itis.issst.uima.brat;

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
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class UimaBratMapping {

	private Map<Type, BratEntityType> entityTypeMappings;
	private Map<Type, UimaBratRelationMapping> relationTypeMappings;
	private Map<Type, UimaBratEventMapping> eventTypeMappings;

	public Set<Type> getEntityUimaTypes() {
		return entityTypeMappings.keySet();
	}

	public BratEntityType getBratEntityType(Type uType) {
		return entityTypeMappings.get(uType);
	}

	public Set<Type> getRelationUimaTypes() {
		return relationTypeMappings.keySet();
	}

	public UimaBratRelationMapping getRelationMapping(Type uType) {
		return relationTypeMappings.get(uType);
	}

	public Set<Type> getEventUimaTypes() {
		return eventTypeMappings.keySet();
	}

	public UimaBratEventMapping getEventMapping(Type uType) {
		return eventTypeMappings.get(uType);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private UimaBratMapping instance = new UimaBratMapping();
		private Map<String, Type> shortName2Type = Maps.newHashMap();

		private Builder() {
			instance.entityTypeMappings = Maps.newLinkedHashMap();
			instance.relationTypeMappings = Maps.newLinkedHashMap();
			instance.eventTypeMappings = Maps.newLinkedHashMap();
		}

		public void addEntityMapping(Type uType, BratEntityType bType) {
			instance.entityTypeMappings.put(uType, bType);
			memorizeShortName(uType);
		}

		public Type getUimaTypeByShortName(String shortName) {
			Type t = shortName2Type.get(shortName);
			if (t == null)
				throw new IllegalStateException(String.format(
						"Can't find UIMA type with short name '%s'",
						shortName));
			return t;
		}

		public BratEntityType getEntityType(Type uType) {
			BratEntityType bet = instance.entityTypeMappings.get(uType);
			if (bet == null) {
				throw new IllegalArgumentException(String.format(
						"Can't find Brat entity type for %s", uType));
			}
			return bet;
		}

		public BratType getType(Type uimaType) {
			BratType result = instance.entityTypeMappings.get(uimaType);
			if (result == null) {
				UimaBratRelationMapping rm = instance.relationTypeMappings.get(uimaType);
				if (rm != null) {
					result = rm.bratType;
				}
			}
			if (result == null) {
				UimaBratEventMapping em = instance.eventTypeMappings.get(uimaType);
				if (em != null) {
					result = em.bratType;
				}
			}
			if (result == null) {
				throw new IllegalStateException(String.format(
						"Can't find mapped brat type for %s", uimaType));
			}
			return result;
		}

		public void addRelationMapping(Type uType, BratRelationType bType,
				Map<String, Feature> argFeatures) {
			UimaBratRelationMapping mp = new UimaBratRelationMapping(bType, argFeatures);
			instance.relationTypeMappings.put(uType, mp);
			memorizeShortName(uType);
		}

		public void addEventMapping(Type uType, BratEventType bType,
				Map<String, Feature> roleFeatures) {
			UimaBratEventMapping mp = new UimaBratEventMapping(bType, roleFeatures);
			instance.eventTypeMappings.put(uType, mp);
			memorizeShortName(uType);
		}

		public UimaBratMapping build() {
			instance.entityTypeMappings = ImmutableMap.copyOf(instance.entityTypeMappings);
			instance.relationTypeMappings = ImmutableMap.copyOf(instance.relationTypeMappings);
			instance.eventTypeMappings = ImmutableMap.copyOf(instance.eventTypeMappings);
			return instance;
		}

		private void memorizeShortName(Type t) {
			if (shortName2Type.containsKey(t.getShortName())) {
				throw new IllegalStateException(String.format(
						"Duplicate type short names: %s", t.getShortName()));
			}
			shortName2Type.put(t.getShortName(), t);
		}
	}
}

abstract class UimaBratStructureMapping<BT extends BratType> {
	final BT bratType;
	final Map<String, Feature> roleFeatures;

	public UimaBratStructureMapping(BT bratType, Map<String, Feature> roleFeatures) {
		this.bratType = bratType;
		this.roleFeatures = ImmutableMap.copyOf(roleFeatures);
	}
}

class UimaBratRelationMapping extends UimaBratStructureMapping<BratRelationType> {
	UimaBratRelationMapping(BratRelationType bratType, Map<String, Feature> argFeatures) {
		super(bratType, argFeatures);
		// check
		if (argFeatures.size() != 2) {
			throw new IllegalStateException(String.format(
					"Illegal argFeatures param for RelationMapping: %s", argFeatures));
		}
	}
}

class UimaBratEventMapping extends UimaBratStructureMapping<BratEventType> {
	public UimaBratEventMapping(BratEventType bratType, Map<String, Feature> roleFeatures) {
		super(bratType, roleFeatures);
	}
}