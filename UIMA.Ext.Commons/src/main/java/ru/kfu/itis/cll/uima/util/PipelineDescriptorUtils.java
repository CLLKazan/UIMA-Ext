/**
 * 
 */
package ru.kfu.itis.cll.uima.util;

import static org.uimafit.factory.ResourceCreationSpecifierFactory.createResourceCreationSpecifier;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.uima.Constants;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.impl.AnalysisEngineDescription_impl;
import org.apache.uima.analysis_engine.metadata.AnalysisEngineMetaData;
import org.apache.uima.analysis_engine.metadata.FixedFlow;
import org.apache.uima.analysis_engine.metadata.impl.FixedFlow_impl;
import org.apache.uima.resource.ResourceCreationSpecifier;
import org.apache.uima.resource.metadata.ConfigurationParameter;
import org.apache.uima.resource.metadata.ConfigurationParameterDeclarations;
import org.apache.uima.resource.metadata.Import;
import org.apache.uima.resource.metadata.MetaDataObject;
import org.apache.uima.resource.metadata.ResourceManagerConfiguration;
import org.apache.uima.resource.metadata.impl.ResourceManagerConfiguration_impl;
import org.uimafit.factory.AnalysisEngineFactory;

import com.google.common.collect.ImmutableList;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class PipelineDescriptorUtils {

	/**
	 * Do the same things as the method
	 * {@link AnalysisEngineFactory#createAggregate(List, List, org.apache.uima.resource.metadata.TypeSystemDescription, org.apache.uima.resource.metadata.TypePriorities, org.apache.uima.analysis_engine.metadata.SofaMapping[])}
	 * but allow {@link Import} objects in a description list.
	 * 
	 * @param analysisEngineDescriptions
	 * @param componentNames
	 * @return UIMA description object
	 * @throws UIMAException
	 * @throws IOException
	 */
	public static AnalysisEngineDescription createAggregateDescription(
			List<MetaDataObject> analysisEngineDescriptions,
			List<String> componentNames)
			throws UIMAException, IOException {

		// create the descriptor and set configuration parameters
		AnalysisEngineDescription desc = new AnalysisEngineDescription_impl();
		desc.setFrameworkImplementation(Constants.JAVA_FRAMEWORK_NAME);
		desc.setPrimitive(false);

		// if any of the aggregated analysis engines does not allow multiple
		// deployment, then the aggregate engine may also not be multiply deployed
		boolean allowMultipleDeploy = true;
		for (MetaDataObject mdo : analysisEngineDescriptions) {
			AnalysisEngineDescription d;
			if (mdo instanceof AnalysisEngineDescription) {
				d = (AnalysisEngineDescription) mdo;
			} else {
				Import aedImport = (Import) mdo;
				URL aedUrl = aedImport.findAbsoluteUrl(UIMAFramework.newDefaultResourceManager());
				d = (AnalysisEngineDescription) createResourceCreationSpecifier(aedUrl, null);
			}
			allowMultipleDeploy &= d.getAnalysisEngineMetaData().getOperationalProperties()
					.isMultipleDeploymentAllowed();
		}
		desc.getAnalysisEngineMetaData().getOperationalProperties()
				.setMultipleDeploymentAllowed(allowMultipleDeploy);

		List<String> flowNames = new ArrayList<String>();

		for (int i = 0; i < analysisEngineDescriptions.size(); i++) {
			MetaDataObject aed = analysisEngineDescriptions.get(i);
			String componentName = componentNames.get(i);
			desc.getDelegateAnalysisEngineSpecifiersWithImports().put(componentName, aed);
			flowNames.add(componentName);
		}

		FixedFlow fixedFlow = new FixedFlow_impl();
		fixedFlow.setFixedFlow(flowNames.toArray(new String[flowNames.size()]));
		desc.getAnalysisEngineMetaData().setFlowConstraints(fixedFlow);

		return desc;
	}

	public static AnalysisEngineDescription createAggregateDescription(
			Map<String, MetaDataObject> namedDescriptions) throws UIMAException, IOException {
		return createAggregateDescription(
				ImmutableList.copyOf(namedDescriptions.values()),
				ImmutableList.copyOf(namedDescriptions.keySet()));
	}

	/**
	 * <ol>
	 * <li>Add the specified parameter declaration into the specified aggregate
	 * descriptor
	 * <li>Set it to override the specified parameter of the specified delegate
	 * </ol>
	 * 
	 * @param resultParam
	 *            parameter declaration with name, type, isMultivalued &
	 *            isMandatory set
	 * @param aggrDesc
	 *            pipeline descriptor
	 * @param delegateParameterNames
	 *            map which keys are delegate names (called 'key' in
	 *            'delegateAnalysisEngine' XML element). For each delegate in
	 *            this map its parameter (a value of this map) will be
	 *            referenced by the result override
	 */
	public static void createOverrideParameterDeclaration(
			ConfigurationParameter resultParam, AnalysisEngineDescription aggrDesc,
			Map<String, String> delegateParameterNames) {
		AnalysisEngineMetaData aggrMeta = aggrDesc.getAnalysisEngineMetaData();
		if (aggrDesc.isPrimitive()) {
			throw new IllegalArgumentException(String.format(
					"The provided AE descriptor (name=%s) is primitive",
					aggrMeta.getName()));
		}
		// check that each specified delegate is actually declared
		for (String delegateKey : delegateParameterNames.keySet()) {
			// do not resolve imports, just check existence of the delegate.
			if (!aggrDesc.getDelegateAnalysisEngineSpecifiersWithImports().containsKey(delegateKey)) {
				throw new IllegalArgumentException(String.format(
						"There is no delegate with key '%s' in the description named '%s'",
						delegateKey, aggrMeta.getName()));
			}
		}
		ConfigurationParameterDeclarations cfgParamDecls = aggrMeta
				.getConfigurationParameterDeclarations();
		if (cfgParamDecls.getConfigurationParameter(null, resultParam.getName()) != null) {
			throw new IllegalArgumentException(String.format(
					"Parameter with name = '%s' exists already"));
		}
		for (Map.Entry<String, String> delegateAndItsParam : delegateParameterNames.entrySet()) {
			String delegateKey = delegateAndItsParam.getKey();
			String delegateParamName = delegateAndItsParam.getValue();
			resultParam.addOverride(delegateKey + "/" + delegateParamName);
		}
		cfgParamDecls.addConfigurationParameter(resultParam);
	}

	/**
	 * Simplified signature for
	 * {@link #createOverrideParameterDeclaration(ConfigurationParameter, AnalysisEngineDescription, Map)}
	 */
	public static void createOverrideParameterDeclaration(
			ConfigurationParameter resultParam, AnalysisEngineDescription aggrDesc,
			String delegateKey, String delegateParamName) {
		createOverrideParameterDeclaration(resultParam, aggrDesc,
				Collections.singletonMap(delegateKey, delegateParamName));
	}

	public static ResourceManagerConfiguration getResourceManagerConfiguration(
			ResourceCreationSpecifier aDesc) {
		ResourceManagerConfiguration resMgrCfg = aDesc.getResourceManagerConfiguration();
		if (resMgrCfg == null) {
			resMgrCfg = new ResourceManagerConfiguration_impl();
			aDesc.setResourceManagerConfiguration(resMgrCfg);
		}
		return resMgrCfg;
	}

	private PipelineDescriptorUtils() {
	}
}
