/**
 * 
 */
package ru.kfu.itis.cll.uima.eval;

import static java.util.Arrays.asList;
import static org.apache.uima.util.CasCreationUtils.mergeTypeSystems;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescriptionFromPath;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;

/**
 * TODO move to deeper package, make non-public
 * 
 * @author Rinat Gareev
 * 
 */
public class TypeSystemInitializer {

	public TypeSystem createTypeSystem(EvaluationConfig config) throws IOException, UIMAException {
		TypeSystemDescription tsDesc = null;
		if (config.getTypeSystemDescPaths() != null) {
			tsDesc = createTypeSystemDescriptionFromPath(config.getTypeSystemDescPaths());
		}
		if (config.getTypeSystemDescNames() != null) {
			TypeSystemDescription tsDescFromNames = createTypeSystemDescription(
					config.getTypeSystemDescNames());
			if (tsDesc != null) {
				tsDesc = mergeTypeSystems(asList(tsDesc, tsDescFromNames));
			} else {
				tsDesc = tsDescFromNames;
			}
		}
		CAS dumbCas = CasCreationUtils.createCas(tsDesc, null, null);
		TypeSystem typeSystem = dumbCas.getTypeSystem();
		// printAllTypes();
		return typeSystem;
	}

}