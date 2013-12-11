/**
 * 
 */
package ru.kfu.itis.issst.uima.brat;

import static ru.kfu.itis.issst.uima.brat.UIMA2BratAnnotator.BRAT_NOTE_MAPPERS;
import static ru.kfu.itis.issst.uima.brat.UIMA2BratAnnotator.ENTITIES_TO_BRAT;
import static ru.kfu.itis.issst.uima.brat.UIMA2BratAnnotator.EVENTS_TO_BRAT;
import static ru.kfu.itis.issst.uima.brat.UIMA2BratAnnotator.RELATIONS_TO_BRAT;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.ConfigurationParameterSettings;
import org.nlplab.brat.configuration.BratEntityType;
import org.nlplab.brat.configuration.BratEventType;
import org.nlplab.brat.configuration.BratRelationType;
import org.nlplab.brat.configuration.BratType;
import org.nlplab.brat.configuration.BratTypesConfiguration;
import org.nlplab.brat.configuration.EventRole;
import org.nlplab.brat.configuration.HasRoles;
import org.uimafit.component.initialize.ConfigurationParameterInitializer;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ResourceCreationSpecifierFactory;
import org.uimafit.factory.initializable.Initializable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class ReverseBratUimaMappingFactory implements BratUimaMappingFactory, Initializable {

	public static final String PARAM_U2B_DESC_PATH = "Uima2BratDescriptorPath";
	public static final String PARAM_U2B_DESC_NAME = "Uima2BratDescriptorName";

	@ConfigurationParameter(name = PARAM_U2B_DESC_PATH)
	private String u2bDescPath;
	@ConfigurationParameter(name = PARAM_U2B_DESC_NAME)
	private String u2bDescName;
	private TypeSystem ts;
	private BratTypesConfiguration bratTypesCfg;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		ConfigurationParameterInitializer.initialize(this, ctx);
		// validate parameters
		if ((u2bDescName == null && u2bDescPath == null)
				|| (u2bDescName != null && u2bDescPath != null)) {
			throw new IllegalStateException(String.format(
					"Illegal parameter settings: %s=%s; %s=%s",
					PARAM_U2B_DESC_NAME, u2bDescName,
					PARAM_U2B_DESC_PATH, u2bDescPath));
		}
	}

	@Override
	public void setTypeSystem(TypeSystem ts) {
		this.ts = ts;
	}

	@Override
	public void setBratTypes(BratTypesConfiguration btConf) {
		this.bratTypesCfg = btConf;
	}

	@Override
	public BratUimaMapping getMapping() throws ResourceInitializationException {
		UimaBratMapping u2bMapping;
		try {
			u2bMapping = createU2BMapping();
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
		return BratUimaMapping.reverse(u2bMapping);
	}

	private UimaBratMapping createU2BMapping() throws UIMAException, IOException {
		AnalysisEngineDescription u2bDesc;
		if (u2bDescName != null) {
			u2bDesc = AnalysisEngineFactory.createAnalysisEngineDescription(u2bDescName);
		} else {
			u2bDesc = (AnalysisEngineDescription) ResourceCreationSpecifierFactory
					.createResourceCreationSpecifier(u2bDescPath, null);
		}
		ConfigurationParameterSettings u2bParamSettings =
				u2bDesc.getAnalysisEngineMetaData().getConfigurationParameterSettings();
		String[] entityToBratStrings = (String[]) u2bParamSettings
				.getParameterValue(ENTITIES_TO_BRAT);
		List<EntityDefinitionValue> entitiesToBrat;
		if (entityToBratStrings == null) {
			entitiesToBrat = ImmutableList.of();
		} else {
			entitiesToBrat = Lists.newLinkedList();
			for (String s : entityToBratStrings) {
				entitiesToBrat.add(EntityDefinitionValue.fromString(s));
			}
		}
		String[] relationToBratStrings = (String[]) u2bParamSettings
				.getParameterValue(RELATIONS_TO_BRAT);
		List<StructureDefinitionValue> relationsToBrat;
		if (relationToBratStrings == null) {
			relationsToBrat = ImmutableList.of();
		} else {
			relationsToBrat = Lists.newLinkedList();
			for (String s : relationToBratStrings) {
				relationsToBrat.add(StructureDefinitionValue.fromString(s));
			}
		}
		String[] eventToBratStrings = (String[]) u2bParamSettings
				.getParameterValue(EVENTS_TO_BRAT);
		List<StructureDefinitionValue> eventsToBrat;
		if (eventToBratStrings == null) {
			eventsToBrat = ImmutableList.of();
		} else {
			eventsToBrat = Lists.newLinkedList();
			for (String s : eventToBratStrings) {
				eventsToBrat.add(StructureDefinitionValue.fromString(s));
			}
		}
		String[] noteMapperDefStrings = (String[]) u2bParamSettings
				.getParameterValue(BRAT_NOTE_MAPPERS);
		List<NoteMapperDefinitionValue> noteMapperDefs;
		if (noteMapperDefStrings == null) {
			noteMapperDefs = ImmutableList.of();
		} else {
			noteMapperDefs = Lists.newLinkedList();
			for (String s : noteMapperDefStrings) {
				noteMapperDefs.add(NoteMapperDefinitionValue.fromString(s));
			}
		}
		UimaBratMappingInitializer initializer = new UimaBratMappingInitializer(ts,
				entitiesToBrat, relationsToBrat, eventsToBrat, noteMapperDefs) {
			@Override
			protected BratEntityType getEntityType(String typeName) {
				return bratTypesCfg.getType(typeName, BratEntityType.class);
			}

			@Override
			protected BratRelationType getRelationType(String typeName,
					Map<String, String> argTypeNames) {
				BratRelationType result = bratTypesCfg.getType(typeName, BratRelationType.class);
				checkRoleMappings(result, argTypeNames);
				return result;
			}

			@Override
			protected BratEventType getEventType(String typeName,
					Map<String, String> roleTypeNames,
					Set<String> multiValuedRoles) {
				BratEventType result = bratTypesCfg.getType(typeName, BratEventType.class);
				checkRoleMappings(result, roleTypeNames);
				for (String roleName : roleTypeNames.keySet()) {
					EventRole role = result.getRole(roleName);
					if (role.getCardinality().allowsMultipleValues() != multiValuedRoles
							.contains(roleName)) {
						throw new IllegalStateException(String.format(
								"Incompatible cardinality in mapping for role %s in type %s",
								roleName, typeName));
					}
				}
				return result;
			}
		};
		return initializer.create();
	}

	private void checkRoleMappings(HasRoles targetType, Map<String, String> mpRoleTypeNames) {
		for (String mpRoleName : mpRoleTypeNames.keySet()) {
			String mpRoleTypeName = mpRoleTypeNames.get(mpRoleName);
			BratType mpRoleType = bratTypesCfg.getType(mpRoleTypeName);
			if (!targetType.isLegalAssignment(mpRoleName, mpRoleType)) {
				throw new IllegalStateException(String.format(
						"Incompatible type %s for role %s in %s:",
						mpRoleTypeName, mpRoleName, targetType));
			}
		}
	}
}
