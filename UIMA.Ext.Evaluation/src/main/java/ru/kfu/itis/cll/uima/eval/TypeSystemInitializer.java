/**
 * 
 */
package ru.kfu.itis.cll.uima.eval;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.uima.util.CasCreationUtils.mergeTypeSystems;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescriptionFromPath;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;

/**
 * TODO move to deeper package, make non-public
 * 
 * @author Rinat Gareev
 * 
 */
public class TypeSystemInitializer implements FactoryBean<TypeSystem> {

	@Value("${typeSystem.description.paths}")
	private String typeSystemDescPathsString;
	@Value("${typeSystem.description.names}")
	private String typeSystemDescNamesString;

	// derived
	private String[] typeSystemDescPaths;
	private String[] typeSystemDescNames;

	private TypeSystem ts;

	@SuppressWarnings("unused")
	@PostConstruct
	private void init() {
		if (!isBlank(typeSystemDescPathsString)) {
			typeSystemDescPaths = StringUtils.split(typeSystemDescPathsString, ";");
		}
		if (!isBlank(typeSystemDescNamesString)) {
			typeSystemDescNames = StringUtils.split(typeSystemDescNamesString, ";");
		}
	}

	private TypeSystem createTypeSystem() throws IOException, UIMAException {
		TypeSystemDescription tsDesc = null;
		if (typeSystemDescPaths != null && typeSystemDescPaths.length > 0) {
			tsDesc = createTypeSystemDescriptionFromPath(typeSystemDescPaths);
		}
		if (typeSystemDescNames != null && typeSystemDescNames.length > 0) {
			TypeSystemDescription tsDescFromNames = createTypeSystemDescription(
					typeSystemDescNames);
			if (tsDesc != null) {
				tsDesc = mergeTypeSystems(asList(tsDesc, tsDescFromNames));
			} else {
				tsDesc = tsDescFromNames;
			}
		}
		if (tsDesc == null) {
			throw new IllegalStateException(
					"Type system description paths or names were not specified!");
		}
		CAS dumbCas = CasCreationUtils.createCas(tsDesc, null, null);
		TypeSystem typeSystem = dumbCas.getTypeSystem();
		// printAllTypes();
		return typeSystem;
	}

	@Override
	public TypeSystem getObject() throws Exception {
		if (ts == null) {
			ts = createTypeSystem();
		}
		return ts;
	}

	@Override
	public Class<?> getObjectType() {
		return TypeSystem.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}