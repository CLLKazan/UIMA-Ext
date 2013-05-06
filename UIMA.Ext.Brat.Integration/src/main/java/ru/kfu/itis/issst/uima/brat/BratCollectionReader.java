package ru.kfu.itis.issst.uima.brat;

import static org.nlplab.brat.BratConstants.ANNOTATION_CONF_FILE;
import static org.nlplab.brat.BratConstants.ANN_FILES_ENCODING;
import static org.nlplab.brat.BratConstants.TXT_FILES_ENCODING;
import static ru.kfu.itis.cll.uima.util.AnnotatorUtils.annotationTypeExist;
import static ru.kfu.itis.cll.uima.util.AnnotatorUtils.featureExist;
import static ru.kfu.itis.issst.uima.brat.UIMA2BratAnnotator.ENTITIES_TO_BRAT;
import static ru.kfu.itis.issst.uima.brat.UIMA2BratAnnotator.EVENTS_TO_BRAT;
import static ru.kfu.itis.issst.uima.brat.UIMA2BratAnnotator.RELATIONS_TO_BRAT;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.ConfigurationParameterSettings;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.nlplab.brat.ann.BratAnnotation;
import org.nlplab.brat.ann.BratAnnotationContainer;
import org.nlplab.brat.ann.BratEntity;
import org.nlplab.brat.ann.BratEvent;
import org.nlplab.brat.ann.BratEventTrigger;
import org.nlplab.brat.ann.BratRelation;
import org.nlplab.brat.ann.BratStructureAnnotation;
import org.nlplab.brat.configuration.BratEntityType;
import org.nlplab.brat.configuration.BratEventType;
import org.nlplab.brat.configuration.BratRelationType;
import org.nlplab.brat.configuration.BratType;
import org.nlplab.brat.configuration.BratTypesConfiguration;
import org.nlplab.brat.configuration.HasRoles;
import org.uimafit.component.CasCollectionReader_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ResourceCreationSpecifierFactory;

import ru.kfu.itis.cll.uima.cas.FSUtils;
import ru.kfu.itis.cll.uima.commons.DocumentMetadata;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Brat 2 UIMA Annotator is CAS Annotator to convert Brat standoff format
 * annotations to UIMA annotations. 1) defines input, ouput files directories of
 * .txt and .ann files 2) reading files and process its content using specified
 * file name parameter in DocumentMetadata annotations. 4) reading Brat
 * annotations and converts them to UIMA annotation (*.xmi files) T: text-bound
 * annotation R: relation E: event A: attribute M: modification (alias for
 * attribute, for backward compatibility) N: normalization #: note
 * 
 * @author RGareev (Kazan Federal University)
 * @author pathfinder
 */

public class BratCollectionReader extends CasCollectionReader_ImplBase {

	public static final String PARAM_BRAT_COLLECTION_DIR = "BratCollectionDir";
	public static final String PARAM_U2B_DESC_PATH = "Uima2BratDescriptorPath";
	public static final String PARAM_U2B_DESC_NAME = "Uima2BratDescriptorName";

	@ConfigurationParameter(name = PARAM_BRAT_COLLECTION_DIR, mandatory = true)
	private File bratCollectionDir;
	@ConfigurationParameter(name = PARAM_U2B_DESC_PATH)
	private String u2bDescPath;
	@ConfigurationParameter(name = PARAM_U2B_DESC_NAME)
	private String u2bDescName;

	// config fields
	private BratTypesConfiguration bratTypesCfg;
	private BratUimaMapping mapping;
	// fields derived from config
	private int totalDocsNum = -1;
	private Feature beginFeature;
	private Feature endFeature;
	private Type documentMetadataType;
	private Feature docMetaUriFeature;
	private Feature docMetaSizeFeature;
	// state fields
	private Iterator<BratDocument> bratDocIter;
	private int docsRead = 0;
	// per-CAS state fields
	@SuppressWarnings("unused")
	private String currentDocName;
	private FromBratMappingContext mappingCtx;
	private CAS cas;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		// validate parameters
		if ((u2bDescName == null && u2bDescPath == null)
				|| (u2bDescName != null && u2bDescPath != null)) {
			throw new IllegalStateException(String.format(
					"Illegal parameter settings: %s=%s; %s=%s",
					PARAM_U2B_DESC_NAME, u2bDescName,
					PARAM_U2B_DESC_PATH, u2bDescPath));
		}
		// make bratDocIter
		File[] annFiles = bratCollectionDir.listFiles(
				(FileFilter) FileFilterUtils.suffixFileFilter(BratDocument.ANN_FILE_SUFFIX));
		List<BratDocument> bratDocs = Lists.newArrayListWithExpectedSize(annFiles.length);
		for (File annFile : annFiles) {
			String docBaseName = FilenameUtils.getBaseName(annFile.getPath());
			BratDocument bratDoc = new BratDocument(bratCollectionDir, docBaseName);
			if (bratDoc.exists()) {
				bratDocs.add(bratDoc);
			} else {
				throw new IllegalStateException(String.format(
						"Missing txt file for %s", annFile));
			}
		}
		totalDocsNum = bratDocs.size();
		bratDocIter = bratDocs.iterator();
	}

	@Override
	public void typeSystemInit(TypeSystem ts) throws ResourceInitializationException {
		super.typeSystemInit(ts);
		// memorize Annotation begin and end features
		Type annotationType = ts.getType("uima.tcas.Annotation");
		beginFeature = annotationType.getFeatureByBaseName("begin");
		assert beginFeature != null;
		endFeature = annotationType.getFeatureByBaseName("end");
		assert endFeature != null;
		// memorize document metadata type and its features
		documentMetadataType = ts.getType(DocumentMetadata.class.getName());
		try {
			annotationTypeExist(DocumentMetadata.class.getName(), documentMetadataType);
			docMetaUriFeature = featureExist(documentMetadataType, "sourceUri");
			docMetaSizeFeature = featureExist(documentMetadataType, "documentSize");
		} catch (AnalysisEngineProcessException e) {
			throw new ResourceInitializationException(e);
		}
		// initialize BratTypesConfiguration
		File annotationConfFile = new File(bratCollectionDir, ANNOTATION_CONF_FILE);
		if (!annotationConfFile.isFile()) {
			throw new IllegalStateException(String.format(
					"%s is missing", annotationConfFile));
		}
		try {
			bratTypesCfg = BratTypesConfiguration.readFrom(annotationConfFile);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
		// initialize Brat -> UIMA mapping
		// 1) initialize UIMA -> Brat mapping from corresponding descriptor
		UimaBratMapping u2bMapping;
		try {
			u2bMapping = createU2BMapping(ts);
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
		// 2) reverse it
		mapping = BratUimaMapping.reverse(u2bMapping);
	}

	private UimaBratMapping createU2BMapping(TypeSystem ts) throws UIMAException, IOException {
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
		UimaBratMappingInitializer initializer = new UimaBratMappingInitializer(ts,
				entitiesToBrat, relationsToBrat, eventsToBrat) {
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
			protected BratEventType getEventType(String typeName, Map<String, String> roleTypeNames) {
				BratEventType result = bratTypesCfg.getType(typeName, BratEventType.class);
				checkRoleMappings(result, roleTypeNames);
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

	@Override
	public boolean hasNext() throws IOException, CollectionException {
		return bratDocIter.hasNext();
	}

	@Override
	public void getNext(CAS cas) throws IOException, CollectionException {
		this.cas = cas;
		BratDocument bratDoc = bratDocIter.next();
		currentDocName = bratDoc.getDocumentName();
		// read and set text
		String txt = FileUtils.readFileToString(bratDoc.getTxtFile(), TXT_FILES_ENCODING);
		cas.setDocumentText(txt);
		// set DocumentMetadata
		setDocumentMetadata(cas, bratDoc.getTxtFile().toURI(), txt.length());

		// read Brat annotations
		BratAnnotationContainer bratContainer = new BratAnnotationContainer(bratTypesCfg);
		BufferedReader annReader = new BufferedReader(new InputStreamReader(
				new FileInputStream(bratDoc.getAnnFile()), ANN_FILES_ENCODING));
		try {
			bratContainer.readFrom(annReader);
		} finally {
			IOUtils.closeQuietly(annReader);
		}
		// prepare Mapping context
		mappingCtx = new FromBratMappingContext();
		// map entity types
		for (BratEntityType bType : mapping.getEntityTypes()) {
			Type uType = mapping.getEntityUimaType(bType);
			for (BratEntity bEntity : bratContainer.getEntities(bType)) {
				if (mappingCtx.isMapped(bEntity)) {
					continue;
				}
				AnnotationFS uAnno = cas.createAnnotation(uType,
						bEntity.getBegin(), bEntity.getEnd());
				mappingCtx.mapped(bEntity, uAnno);
				cas.addFsToIndexes(uAnno);
			}
		}
		// map relation types
		for (BratRelationType bType : mapping.getRelationTypes()) {
			BratUimaRelationMapping relMapping = mapping.getRelationMapping(bType);
			for (BratRelation bRelation : bratContainer.getRelations(bType)) {
				AnnotationFS uRelation = mapStructureRoles(bRelation, relMapping);
				List<AnnotationFS> uRelationArgs = getRelationArgs(uRelation,
						relMapping.featureRoles.keySet());
				// set UIMA relation begin to minimal begin offset of arguments
				int uRelationBegin = FSUtils.intMinBy(uRelationArgs, beginFeature);
				uRelation.setIntValue(beginFeature, uRelationBegin);
				// set UIMA relation end to maximal end offset of arguments
				int uRelationEnd = FSUtils.intMaxBy(uRelationArgs, endFeature);
				uRelation.setIntValue(endFeature, uRelationEnd);
				cas.addFsToIndexes(uRelation);
			}
		}
		// map event types
		for (BratEventType bType : mapping.getEventTypes()) {
			BratUimaEventMapping evMapping = mapping.getEventMapping(bType);
			for (BratEvent bEvent : bratContainer.getEvents(bType)) {
				BratEventTrigger bTrigger = bEvent.getTrigger();
				AnnotationFS uEvent = mapStructureRoles(bEvent, evMapping);
				// set UIMA event begin to trigger begin
				uEvent.setIntValue(beginFeature, bTrigger.getBegin());
				// set UIMA event end to trigger end
				uEvent.setIntValue(endFeature, bTrigger.getEnd());
				cas.addFsToIndexes(uEvent);
			}
		}
		// increase progress counter
		docsRead++;
		// clean per-CAS state
		currentDocName = null;
		mappingCtx = null;
		this.cas = null;
	}

	private void setDocumentMetadata(CAS cas, URI docUri, int docSize) {
		AnnotationFS docMeta = cas.createAnnotation(documentMetadataType, 0, 0);
		docMeta.setLongValue(docMetaSizeFeature, docSize);
		docMeta.setStringValue(docMetaUriFeature, docUri.toString());
		cas.addFsToIndexes(docMeta);
	}

	@Override
	public Progress[] getProgress() {
		return new Progress[] {
				new ProgressImpl(docsRead, totalDocsNum, Progress.ENTITIES)
		};
	}

	private List<AnnotationFS> getRelationArgs(AnnotationFS anno, Set<Feature> argFeatures) {
		List<AnnotationFS> result = Lists.newLinkedList();
		assert argFeatures.size() == 2;
		for (Feature f : argFeatures) {
			AnnotationFS argAnno = (AnnotationFS) anno.getFeatureValue(f);
			if (argAnno == null) {
				throw new IllegalStateException();
			}
			result.add(argAnno);
		}
		return result;
	}

	private <BT extends BratType, BA extends BratStructureAnnotation<BT>> AnnotationFS mapStructureRoles(
			BA bAnno, BratUimaStructureMapping<BT> strMapping) {
		AnnotationFS result = cas.createAnnotation(strMapping.uimaType, 0, 0);
		for (Feature roleFeature : strMapping.featureRoles.keySet()) {
			String roleName = strMapping.featureRoles.get(roleFeature);
			BratAnnotation<?> roleBratAnno = bAnno.getRoleAnnotations().get(roleName);
			if (roleBratAnno == null) {
				continue;
			}
			AnnotationFS roleUimaAnno = mappingCtx.getMapped(roleBratAnno);
			if (roleUimaAnno != null) {
				result.setFeatureValue(roleFeature, roleUimaAnno);
			}
		}
		return result;
	}

	private class FromBratMappingContext {
		private Map<String, AnnotationFS> mappedAnnotations = Maps.newHashMap();

		private boolean isMapped(BratAnnotation<?> bAnno) {
			return mappedAnnotations.containsKey(bAnno.getId());
		}

		private AnnotationFS getMapped(BratAnnotation<?> bAnno) {
			return mappedAnnotations.get(bAnno.getId());
		}

		private void mapped(BratAnnotation<?> bAnno, AnnotationFS uAnno) {
			if (mappedAnnotations.put(bAnno.getId(), uAnno) != null) {
				// sanity check
				throw new IllegalStateException();
			}
		}
	}
}