/**
 * 
 */
package ru.kfu.itis.issst.uima.brat;

import java.util.Map;
import java.util.Set;

import org.apache.uima.cas.Type;
import org.nlplab.brat.configuration.BratEntityType;
import org.nlplab.brat.configuration.BratRelationType;

/**
 * @author Rinat Gareev
 * 
 */
public class BratUimaMapping {

	private Map<BratEntityType, Type> entityTypeMappings;
	private Map<BratRelationType, Type> relationTypeMappings;

	public Set<BratEntityType> getEntityTypes() {
		return entityTypeMappings.keySet();
	}

	public Type getEntityUimaType(BratEntityType bType) {
		return entityTypeMappings.get(bType);
	}

	public Set<BratRelationType> getRelationTypes() {
		return relationTypeMappings.keySet();
	}

	public Type getRelationUimaType(BratRelationType bType) {
		return relationTypeMappings.get(bType);
	}
}