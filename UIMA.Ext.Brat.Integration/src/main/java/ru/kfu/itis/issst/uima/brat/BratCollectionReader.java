package ru.kfu.itis.issst.uima.brat;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.*;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.CasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.initializable.InitializableFactory;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.nlplab.brat.ann.*;
import org.nlplab.brat.configuration.*;
import ru.kfu.itis.cll.uima.cas.FSUtils;
import ru.kfu.itis.cll.uima.commons.DocumentMetadata;

import java.io.*;
import java.util.*;

import static org.nlplab.brat.BratConstants.*;
import static ru.kfu.itis.cll.uima.util.AnnotatorUtils.annotationTypeExist;
import static ru.kfu.itis.cll.uima.util.AnnotatorUtils.featureExist;
import static ru.kfu.itis.issst.uima.brat.PUtils.hasCollectionRange;
import static ru.kfu.itis.issst.uima.brat.PUtils.toCompatibleCollection;

/**
 * @author RGareev (Kazan Federal University)
 * @author pathfinder
 */
public class BratCollectionReader extends CasCollectionReader_ImplBase {

    public static final String PARAM_BRAT_COLLECTION_DIR = "BratCollectionDir";
    public static final String PARAM_MAPPING_FACTORY_CLASS = "mappingFactoryClass";

    @ConfigurationParameter(name = PARAM_BRAT_COLLECTION_DIR, mandatory = true)
    private File bratCollectionDir;
    @ConfigurationParameter(name = PARAM_MAPPING_FACTORY_CLASS, mandatory = true)
    private String mappingFactoryClassName;
    // config fields
    private BratTypesConfiguration bratTypesCfg;
    private BratUimaMappingFactory mappingFactory;
    // fields derived from config
    private BratUimaMapping mapping;
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
    private String currentDocName;
    private FromBratMappingContext mappingCtx;
    private CAS cas;
    private BratAnnotationContainer bratContainer;

    @Override
    public void initialize(UimaContext ctx) throws ResourceInitializationException {
        super.initialize(ctx);
        // initialize mappingFactory
        mappingFactory = InitializableFactory.create(ctx, mappingFactoryClassName,
                BratUimaMappingFactory.class);
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
        mappingFactory.setTypeSystem(ts);
        mappingFactory.setBratTypes(bratTypesCfg);
        mapping = mappingFactory.getMapping();
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
        String docName = FilenameUtils.getBaseName(bratDoc.getTxtFile().getPath());
        setDocumentMetadata(cas, docName, txt.length());

        bratContainer = new BratAnnotationContainer(bratTypesCfg);
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
            BratUimaEntityMapping entMapping = mapping.getEntityMapping(bType);
            Type uType = entMapping.uimaType;
            for (BratEntity bEntity : bratContainer.getEntities(bType)) {
                if (mappingCtx.isMapped(bEntity)) {
                    continue;
                }
                AnnotationFS uAnno = cas.createAnnotation(uType,
                        bEntity.getBegin(), bEntity.getEnd());

                mapNote(entMapping, bEntity, uAnno);

                cas.addFsToIndexes(uAnno);
                mappingCtx.mapped(bEntity, uAnno);
            }
        }
        // map relation types
        for (BratRelationType bType : mapping.getRelationTypes()) {
            BratUimaRelationMapping relMapping = mapping.getRelationMapping(bType);
            for (BratRelation bRelation : bratContainer.getRelations(bType)) {
                if (mappingCtx.isMapped(bRelation)) {
                    continue;
                }
                AnnotationFS uRelation = mapStructureRoles(bRelation, relMapping);
                List<AnnotationFS> uRelationArgs = getRelationArgs(uRelation,
                        relMapping.featureRoles.keySet());
                // set UIMA relation begin to minimal begin offset of arguments
                int uRelationBegin = FSUtils.intMinBy(uRelationArgs, beginFeature);
                uRelation.setIntValue(beginFeature, uRelationBegin);
                // set UIMA relation end to maximal end offset of arguments
                int uRelationEnd = FSUtils.intMaxBy(uRelationArgs, endFeature);
                uRelation.setIntValue(endFeature, uRelationEnd);
                // map note
                mapNote(relMapping, bRelation, uRelation);
                // memorize
                cas.addFsToIndexes(uRelation);
                mappingCtx.mapped(bRelation, uRelation);
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
                // map note
                mapNote(evMapping, bEvent, uEvent);
                // memorize
                cas.addFsToIndexes(uEvent);
                mappingCtx.mapped(bEvent, uEvent);
            }
        }
        // increase progress counter
        docsRead++;
        // clean per-CAS state
        currentDocName = null;
        mappingCtx = null;
        this.cas = null;
        bratContainer = null;
    }

    private <BT extends BratType> void mapNote(BratUimaTypeMappingBase<BT> typeMapping,
                                               BratAnnotation<BT> bAnno, AnnotationFS uAnno) {
        BratNoteMapper noteMapper = typeMapping.noteMapper;
        if (noteMapper == null) {
            return;
        }
        Collection<BratNoteAnnotation> notes = bratContainer.getNotes(bAnno);
        for (BratNoteAnnotation note : notes) {
            try {
                noteMapper.parseNote(uAnno, note.getContent());
            } catch (Exception e) {
                throw new IllegalStateException(String.format(
                        "Can't parse note %s in document %s", note, currentDocName));
            }
        }
    }

    private void setDocumentMetadata(CAS cas, String docName, int docSize) {
        AnnotationFS docMeta = cas.createAnnotation(documentMetadataType, 0, 0);
        docMeta.setLongValue(docMetaSizeFeature, docSize);
        docMeta.setStringValue(docMetaUriFeature, docName);
        cas.addFsToIndexes(docMeta);
    }

    @Override
    public Progress[] getProgress() {
        return new Progress[]{
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
            Collection<BratAnnotation<?>> roleBratAnnos = bAnno.getRoleAnnotations().get(roleName);
            if (roleBratAnnos.isEmpty()) {
                continue;
            }
            List<AnnotationFS> roleUimaAnnos = Lists.newLinkedList();
            for (BratAnnotation<?> roleBratAnno : roleBratAnnos) {
                AnnotationFS roleUimaAnno = mappingCtx.getMapped(roleBratAnno);
                if (roleUimaAnno == null) {
                    throw new IllegalStateException(String.format(
                            "Brat annotation %s has not been mapped", roleBratAnno));
                }
                roleUimaAnnos.add(roleUimaAnno);
            }
            FeatureStructure featVal;
            if (hasCollectionRange(roleFeature)) {
                featVal = toCompatibleCollection(cas, roleFeature, roleUimaAnnos);
            } else {
                if (roleUimaAnnos.size() > 1) {
                    getLogger().error(String.format(
                            "Too much role '%s' values in anno %s in doc %s. " +
                                    "Only the first value will be mapped.",
                            roleName, bAnno.getId(), currentDocName));
                }
                featVal = roleUimaAnnos.get(0);
            }
            result.setFeatureValue(roleFeature, featVal);
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