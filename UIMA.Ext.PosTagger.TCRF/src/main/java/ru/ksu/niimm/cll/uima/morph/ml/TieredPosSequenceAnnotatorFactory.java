/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import static org.uimafit.factory.AnalysisEngineFactory.createAggregateDescription;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.ExternalResourceFactory.bindExternalResource;
import static ru.kfu.itis.issst.uima.postagger.PosTaggerAPI.PARAM_REUSE_EXISTING_WORD_ANNOTATIONS;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.cleartk.classifier.CleartkSequenceAnnotator;
import org.cleartk.classifier.jar.DirectoryDataWriterFactory;
import org.cleartk.classifier.jar.JarClassifierBuilder;
import org.opencorpora.cas.Word;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ConfigurationParameterFactory;
import org.uimafit.util.ReflectionUtil;
import org.xml.sax.SAXException;

import ru.kfu.itis.cll.uima.io.IoUtils;
import ru.kfu.itis.cll.uima.util.PipelineDescriptorUtils;
import ru.kfu.itis.issst.cleartk.GenericJarClassifierFactory;
import ru.kfu.itis.issst.cleartk.JarSequenceClassifierFactory;
import ru.kfu.itis.issst.cleartk.crfsuite.CRFSuiteStringOutcomeClassifierBuilder;
import ru.kfu.itis.issst.cleartk.crfsuite.CRFSuiteStringOutcomeDataWriterFactory;
import ru.kfu.itis.issst.uima.morph.commons.GramModelBasedTagMapper;
import ru.kfu.itis.issst.uima.morph.commons.TagAssembler;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionaryHolder;
import ru.kfu.itis.issst.uima.postagger.PosTaggerAPI;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class TieredPosSequenceAnnotatorFactory {

	public static final String POS_TAGGER_DESCRIPTOR_NAME = "pos_tagger";

	/**
	 * Create a descriptor of training data writer with the specified parameter
	 * values. The result descriptor expects that there is an external resource
	 * with name '{@value PosTaggerAPI#MORPH_DICTIONARY_RESOURCE_NAME}' that
	 * implements {@link MorphDictionaryHolder}.
	 * <p>
	 * <strong>Note about side effect:</strong> this method writes the provided
	 * configuration to a file in the specified directory.
	 * 
	 * @param posTiers
	 * @param annotatorParams
	 * @param trainingBaseDir
	 * @return descriptor instance
	 * @throws ResourceInitializationException
	 * @throws IOException
	 */
	public static AnalysisEngineDescription getTrainingDataWriterDescriptor(
			List<String> posTiers,
			Map<String, Object> annotatorParams,
			File trainingBaseDir)
			throws ResourceInitializationException, IOException {
		//
		Properties configProps = new Properties();
		List<AnalysisEngineDescription> delegateDescs = Lists.newArrayList();
		List<String> delegateNames = Lists.newArrayList();
		// create description objects
		for (int i = 0; i < posTiers.size(); i++) {
			String posTier = posTiers.get(i);
			configProps.setProperty(tierConfigKey(i), posTier);
			//
			File trainingDir = getTierDir(trainingBaseDir, posTier);
			//
			List<Object> finalParams = Lists.newArrayList(
					TieredPosSequenceAnnotator.PARAM_POS_TIERS, posTiers,
					TieredPosSequenceAnnotator.PARAM_CURRENT_TIER, i,
					TieredPosSequenceAnnotator.PARAM_DATA_WRITER_FACTORY_CLASS_NAME,
					CRFSuiteStringOutcomeDataWriterFactory.class.getName(),
					DirectoryDataWriterFactory.PARAM_OUTPUT_DIRECTORY, trainingDir);
			for (String paramName : annotatorParams.keySet()) {
				finalParams.add(paramName);
				Object paramValue = annotatorParams.get(paramName);
				finalParams.add(paramValue);
				if (paramValue == null) {
					throw new UnsupportedOperationException();
				}
				configProps.setProperty(annotatorParamKey(paramName), String.valueOf(paramValue));
			}
			//
			AnalysisEngineDescription ptDesc = createPrimitiveDescription(
					TieredPosSequenceAnnotator.class,
					finalParams.toArray());
			ptDesc.getAnalysisEngineMetaData().getOperationalProperties()
					.setMultipleDeploymentAllowed(false);
			/*
			try {
				bindResource(ptDesc,
						TieredPosSequenceAnnotator.RESOURCE_KEY_MORPH_DICTIONARY,
						morphDictDesc);
			} catch (InvalidXMLException e) {
				throw new IllegalStateException(e);
			}
			*/
			//
			delegateDescs.add(ptDesc);
			delegateNames.add(ptDesc.getImplementationName() + "-" + posTier);
		}
		AnalysisEngineDescription resultDesc = createAggregateDescription(
				delegateDescs, delegateNames, null, null, null, null);
		// bind MorhpDictionaryHolder resource by names to each delegate
		for (String dName : delegateNames) {
			bindExternalResource(resultDesc,
					dName + "/" + TieredPosSequenceAnnotator.RESOURCE_MORPH_DICTIONARY,
					PosTaggerAPI.MORPH_DICTIONARY_RESOURCE_NAME);
		}
		// write config.props
		File configPropsFile = new File(trainingBaseDir, CONFIG_PROPS_FILENAME);
		IoUtils.write(configProps, configPropsFile);
		//
		return resultDesc;
	}

	public static void trainModels(File trDataBaseDir, File modelBaseDir, String[] trainerArgs)
			throws Exception {
		// read and copy config file to output directory
		File configPropsFile = new File(trDataBaseDir, CONFIG_PROPS_FILENAME);
		FileUtils.copyFile(configPropsFile, new File(modelBaseDir, CONFIG_PROPS_FILENAME));
		Properties configProps = IoUtils.readProperties(configPropsFile);
		List<String> posTiers = getTiersList(configProps);
		//
		for (String posTier : posTiers) {
			File trainingDir = getTierDir(trDataBaseDir, posTier);
			File modelDir = getTierDir(modelBaseDir, posTier);
			// TODO The following lines contain a few hacks to avoid
			// extensive training file duplicates reproduction
			JarClassifierBuilder<?> _classifierBuilder = JarClassifierBuilder
					.fromTrainingDirectory(trainingDir);
			CRFSuiteStringOutcomeClassifierBuilder classifierBuilder =
					(CRFSuiteStringOutcomeClassifierBuilder) _classifierBuilder;
			// invoke implementation-specific method (i.e., it is not declared in the interface)
			classifierBuilder.trainClassifier(modelDir, trainingDir, trainerArgs);
			classifierBuilder.packageClassifier(modelDir);
		}
	}

	/**
	 * Create a tagger description for the specified model. The result has the
	 * following configuration parameters:
	 * <dl>
	 * <dt> {@value GenericJarClassifierFactory#PARAM_ADDITIONAL_SEARCH_PATHS}
	 * <dd>By default a tagger tries to initialize itself in assumption that
	 * modelBaseDir is in UIMA datapath. If this is not the case then it will
	 * try to use values of this parameter.
	 * <dt> {@value PosTaggerAPI#PARAM_REUSE_EXISTING_WORD_ANNOTATIONS}
	 * <dd>Defines whether a tagger should produce {@link Word} annotations or
	 * reuse existing. The default value of this parameter is specified by the
	 * argument of this method.
	 * </dl>
	 * <p>
	 * The result descriptor expects that there is an external resource with
	 * name '{@value PosTaggerAPI#MORPH_DICTIONARY_RESOURCE_NAME}' that
	 * implements {@link MorphDictionaryHolder}.
	 * 
	 * @param modelBaseDir
	 *            a base model directory that contains model directories for
	 *            each tier
	 * @param reuseExistingWordAnnotations
	 * @return descriptor instance
	 * @throws ResourceInitializationException
	 * @throws SAXException
	 */
	public static AnalysisEngineDescription createTaggerDescription(
			File modelBaseDir,
			boolean reuseExistingWordAnnotations)
			throws ResourceInitializationException, IOException {
		List<AnalysisEngineDescription> aeDescriptions = Lists.newArrayList();
		// list of all annotator names
		List<String> aeNames = Lists.newArrayList();
		// list of tagger annotator names only
		List<String> taggerNames = Lists.newArrayList();
		// prepare TypeSystemDescriptor consisting of produced types
		TypeSystemDescription tsDesc = PosTaggerAPI.getTypeSystemDescription();
		//
		File configPropsFile = new File(modelBaseDir, CONFIG_PROPS_FILENAME);
		Properties configProps = IoUtils.readProperties(configPropsFile);
		// read pos tiers
		List<String> posTiers = getTiersList(configProps);
		// read annotator parameters
		Map<String, Object> annotatorParams = getAnnotatorParameters(configProps);
		//
		for (int i = 0; i < posTiers.size(); i++) {
			String posTier = posTiers.get(i);
			File modelDir = getTierDir(modelBaseDir, posTier);
			File modelJarFile = JarClassifierBuilder.getModelJarFile(modelDir);
			// make model jar path relative to modelBaseDir
			String jarRelativePath = relativize(modelBaseDir, modelJarFile);
			// 
			List<Object> finalParams = Lists.newArrayList(
					CleartkSequenceAnnotator.PARAM_IS_TRAINING, false,
					PARAM_REUSE_EXISTING_WORD_ANNOTATIONS,
					reuseExistingWordAnnotations,
					TieredPosSequenceAnnotator.PARAM_POS_TIERS, posTiers,
					TieredPosSequenceAnnotator.PARAM_CURRENT_TIER, i,
					TieredPosSequenceAnnotator.PARAM_CLASSIFIER_FACTORY_CLASS_NAME,
					JarSequenceClassifierFactory.class.getName());
			for (String paramName : annotatorParams.keySet()) {
				finalParams.add(paramName);
				finalParams.add(annotatorParams.get(paramName));
			}
			//
			AnalysisEngineDescription ptDesc = createPrimitiveDescription(
					TieredPosSequenceAnnotator.class, tsDesc,
					finalParams.toArray());
			// set jarRelativePath and add optional additionalSearchPaths parameter without value to allow overrides
			ConfigurationParameterFactory.addConfigurationParameters(ptDesc,
					JarSequenceClassifierFactory.class);
			ptDesc.getAnalysisEngineMetaData()
					.getConfigurationParameterSettings()
					.setParameterValue(
							JarSequenceClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
							jarRelativePath);
			/*
			try {
				bindResource(ptDesc,
						TieredPosSequenceAnnotator.RESOURCE_KEY_MORPH_DICTIONARY,
						morphDictDesc);
			} catch (InvalidXMLException e) {
				throw new IllegalStateException(e);
			}
			*/
			aeDescriptions.add(ptDesc);
			String taggerDelegateName = ptDesc.getImplementationName() + "-" + posTier;
			aeNames.add(taggerDelegateName);
			taggerNames.add(taggerDelegateName);
		}
		// add tag-assembler
		aeDescriptions.add(TagAssembler.createDescription());
		aeNames.add("tag-assembler");
		// create result aggregate descriptor
		AnalysisEngineDescription aggrDesc = AnalysisEngineFactory.createAggregateDescription(
				aeDescriptions, aeNames, null, null, null, null);
		// add parameter overrides
		{
			// maps of a delegate name to one of its parameter
			Map<String, String> taggerDelegateREWParamsMap = Maps.newLinkedHashMap();
			Map<String, String> taggerDelegateASPParamsMap = Maps.newLinkedHashMap();
			for (String tdName : taggerNames) {
				taggerDelegateREWParamsMap.put(tdName, PARAM_REUSE_EXISTING_WORD_ANNOTATIONS);
				taggerDelegateASPParamsMap.put(tdName,
						JarSequenceClassifierFactory.PARAM_ADDITIONAL_SEARCH_PATHS);
			}
			PipelineDescriptorUtils.createOverrideParameterDeclaration(
					PosTaggerAPI.createReuseExistingWordAnnotationParameterDeclaration(),
					aggrDesc,
					taggerDelegateREWParamsMap);
			try {
				PipelineDescriptorUtils.createOverrideParameterDeclaration(
						ConfigurationParameterFactory.createPrimitiveParameter(
								ReflectionUtil.getField(
										JarSequenceClassifierFactory.class,
										"additionalSearchPaths")),
						aggrDesc,
						taggerDelegateASPParamsMap);
			} catch (NoSuchFieldException e) {
				// must never happen
				throw new RuntimeException(e);
			}
		}
		// bind MorphDictionaryHolder resource to tagger delegates
		for (String tdName : taggerNames) {
			bindExternalResource(aggrDesc,
					tdName + "/" + TieredPosSequenceAnnotator.RESOURCE_MORPH_DICTIONARY,
					PosTaggerAPI.MORPH_DICTIONARY_RESOURCE_NAME);
		}
		// bind GramModelHolder resource to tagAssembler
		bindExternalResource(aggrDesc,
				"tag-assembler/" + GramModelBasedTagMapper.RESOURCE_GRAM_MODEL,
				PosTaggerAPI.MORPH_DICTIONARY_RESOURCE_NAME);
		//
		return aggrDesc;
	}

	public static AnalysisEngineDescription createTaggerDescription(File modelBaseDir)
			throws ResourceInitializationException, IOException {
		return createTaggerDescription(modelBaseDir, false);
	}

	private static File getTierDir(File baseDir, String posTier) {
		// TODO escape chars that are not safe for filename
		posTier = posTier.replace("&", "_and_");
		return new File(baseDir, posTier);
	}

	private static String tierConfigKey(int tierIndex) {
		return "tier." + tierIndex;
	}

	private static List<String> getTiersList(Properties config) {
		List<String> result = Lists.newArrayList();
		String curTier;
		int curTierIndex = 0;
		while ((curTier = config.getProperty(tierConfigKey(curTierIndex))) != null) {
			result.add(curTier);
			curTierIndex++;
		}
		if (result.isEmpty()) {
			throw new IllegalStateException("No tiers defined in properties:\n" + config);
		}
		return result;
	}

	private static Map<String, Object> getAnnotatorParameters(Properties config) {
		Map<String, Object> resultMap = Maps.newHashMap();
		for (String propKey : config.stringPropertyNames()) {
			if (propKey.startsWith(ANNOTATOR_PARAM_KEY_PREFIX)) {
				String paramName = propKey.substring(ANNOTATOR_PARAM_KEY_PREFIX.length());
				if (paramName.isEmpty()) {
					throw new IllegalStateException("Illegal property key: " + propKey);
				}
				Class<?> paramValType = getParameterType(paramName);
				String paramValStr = config.getProperty(propKey);
				Object paramVal;
				if (String.class.equals(paramValType)) {
					paramVal = paramValStr;
				} else if (Integer.TYPE.equals(paramValType) || Integer.class.equals(paramValType)) {
					paramVal = Integer.valueOf(paramValStr);
				} else if (Boolean.TYPE.equals(paramValType) || Boolean.class.equals(paramValType)) {
					paramVal = Boolean.valueOf(paramValStr);
				} else {
					// TODO
					throw new UnsupportedOperationException();
				}
				resultMap.put(paramName, paramVal);
			}
		}
		return resultMap;
	}

	private static Class<?> getParameterType(String paramName) {
		for (Field field : ReflectionUtil.getFields(TieredPosSequenceAnnotator.class)) {
			if (ConfigurationParameterFactory.isConfigurationParameterField(field)) {
				ConfigurationParameter annotation = field
						.getAnnotation(ConfigurationParameter.class);
				if (paramName.equals(annotation.name())) {
					return field.getType();
				}
			}
		}
		throw new IllegalArgumentException("Unknown annotator configuration parameter: "
				+ paramName);
	}

	private static String annotatorParamKey(String paramName) {
		return ANNOTATOR_PARAM_KEY_PREFIX + paramName;
	}

	private static final String CONFIG_PROPS_FILENAME = "config.props";
	private static final String ANNOTATOR_PARAM_KEY_PREFIX = "annotatorParam.";

	/**
	 * @param baseDir
	 * @param target
	 * @return relative path of target against baseDir
	 */
	private static final String relativize(File baseDir, File target) {
		// TODO:LOW use File#relativize after migration on Java 7
		// this solution will work well only when target is in baseDir,
		// but this is enough in the context of this class
		URI relativeUri = baseDir.toURI().relativize(target.toURI());
		return FilenameUtils.separatorsToSystem(relativeUri.getPath());
	}

	private TieredPosSequenceAnnotatorFactory() {
	}
}
