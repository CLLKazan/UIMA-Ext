/**
 * 
 */
package ru.kfu.itis.cll.uima.util;

import static org.uimafit.factory.ResourceCreationSpecifierFactory.createResourceCreationSpecifier;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.Constants;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.impl.AnalysisEngineDescription_impl;
import org.apache.uima.analysis_engine.metadata.FixedFlow;
import org.apache.uima.analysis_engine.metadata.impl.FixedFlow_impl;
import org.apache.uima.resource.metadata.Import;
import org.apache.uima.resource.metadata.MetaDataObject;
import org.uimafit.factory.AnalysisEngineFactory;

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

	private PipelineDescriptorUtils() {
	}
}
