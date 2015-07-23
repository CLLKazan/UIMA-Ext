package ru.kfu.itis.issst.uima.brat;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.*;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.component.CasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.OperationalProperties;
import org.apache.uima.fit.factory.initializable.InitializableFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.nlplab.brat.ann.*;
import org.nlplab.brat.configuration.*;
import org.nlplab.brat.configuration.EventRole.Cardinality;
import ru.kfu.itis.cll.uima.commons.DocumentMetadata;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.nlplab.brat.BratConstants.*;
import static ru.kfu.itis.issst.uima.brat.PUtils.toList;

/**
 * TODO adjust this top javadoc
 * <p/>
 * UIMA Annotator is CAS Annotator to convert UIMA annotations to brat standoff
 * format annotations. 1) defines input, ouput files directories 2) reading
 * annotator descriptor file and converts parameters to brat configuration file
 * saved as annotation.conf 3) saves annotations text file using specified file
 * name parameter in DocumentMetadata annotations. 4) reading UIMA annotations
 * and converts them to brat annotation (*.ann files)
 * <p/>
 * T: text-bound annotation R: relation E: event A: attribute M: modification
 * (alias for attribute, for backward compatibility) N: normalization #: note
 * <p/>
 * For event annotation you have to add additional info about event entities
 * into desc file
 *
 * @author Rinat Gareev (Kazan Federal University)
 * @author pathfinder
 */
@OperationalProperties(modifiesCas = false, multipleDeploymentAllowed = false)
public class UIMA2BratAnnotator extends CasAnnotator_ImplBase {

    public final static String BRAT_OUT = "BratOutputDir";
    public final static String ENTITIES_TO_BRAT = "EntitiesToBrat";
    public final static String RELATIONS_TO_BRAT = "RelationsToBrat";
    public final static String EVENTS_TO_BRAT = "EventsToBrat";
    public final static String BRAT_NOTE_MAPPERS = "BratNoteMappers";
    public static final String PARAM_OUTPUT_PATH_FUNCTION = "outputPathFunction";

    // annotator configuration fields
    @ConfigurationParameter(name = BRAT_OUT, mandatory = true)
    private File bratDirectory;
    @ConfigurationParameter(name = ENTITIES_TO_BRAT, mandatory = false)
    private String[] entitiesToBratRaw;
    private List<EntityDefinitionValue> entitiesToBrat;
    @ConfigurationParameter(name = RELATIONS_TO_BRAT, mandatory = false)
    private String[] relationsToBratRaw;
    private List<StructureDefinitionValue> relationsToBrat;
    @ConfigurationParameter(name = EVENTS_TO_BRAT, mandatory = false)
    private String[] eventsToBratRaw;
    private List<StructureDefinitionValue> eventsToBrat;
    @ConfigurationParameter(name = BRAT_NOTE_MAPPERS, mandatory = false)
    private String[] noteMappersDefinitionsRaw;
    private List<NoteMapperDefinitionValue> noteMappersDefinitions;
    @ConfigurationParameter(name = PARAM_OUTPUT_PATH_FUNCTION, mandatory = false,
            defaultValue = "ru.kfu.itis.cll.uima.consumer.DefaultSourceURI2OutputFilePathFunction")
    private Class<? extends Function> outPathFuncClass;

    // derived configuration fields
    private BratTypesConfiguration bratTypesConfig;
    private UimaBratMapping mapping;
    private Function<DocumentMetadata, Path> outPathFunc;

    // state fields
    private TypeSystem ts;

    // per-CAS state fields
    private String currentDocName;
    private BratAnnotationContainer bac;
    private ToBratMappingContext context;

    @Override
    public void initialize(UimaContext ctx)
            throws ResourceInitializationException {
        super.initialize(ctx);

        getLogger().info("Annotator is initializing ...");
        if (entitiesToBratRaw == null) {
            entitiesToBrat = ImmutableList.of();
        } else {
            entitiesToBrat = Lists.newLinkedList();
            for (String valStr : entitiesToBratRaw) {
                entitiesToBrat.add(EntityDefinitionValue.fromString(valStr));
            }
            entitiesToBrat = ImmutableList.copyOf(entitiesToBrat);
        }
        if (relationsToBratRaw == null) {
            relationsToBrat = ImmutableList.of();
        } else {
            relationsToBrat = Lists.newLinkedList();
            for (String valStr : relationsToBratRaw) {
                StructureDefinitionValue val = StructureDefinitionValue.fromString(valStr);
                if (val.roleDefinitions.size() != 2) {
                    throw new IllegalArgumentException(String.format(
                            "Illegal relation definition: %s", valStr));
                }
                relationsToBrat.add(val);
            }
            relationsToBrat = ImmutableList.copyOf(relationsToBrat);
        }
        if (eventsToBratRaw == null) {
            eventsToBrat = ImmutableList.of();
        } else {
            eventsToBrat = Lists.newLinkedList();
            for (String valStr : eventsToBratRaw) {
                eventsToBrat.add(StructureDefinitionValue.fromString(valStr));
            }
            eventsToBrat = ImmutableList.copyOf(eventsToBrat);
        }
        if (noteMappersDefinitionsRaw == null) {
            noteMappersDefinitions = ImmutableList.of();
        } else {
            noteMappersDefinitions = Lists.newLinkedList();
            for (String defStr : noteMappersDefinitionsRaw) {
                noteMappersDefinitions.add(NoteMapperDefinitionValue.fromString(defStr));
            }
            noteMappersDefinitions = ImmutableList.copyOf(noteMappersDefinitions);
        }
        //
        //noinspection unchecked
        outPathFunc = InitializableFactory.create(ctx, outPathFuncClass);
    }

    @Override
    public void typeSystemInit(TypeSystem ts)
            throws AnalysisEngineProcessException {
        super.typeSystemInit(ts);
        this.ts = ts;
        //
        getLogger().info("Reading UIMA types to convert to brat annotations ... ");
        createBratTypesConfiguration();
        Writer acWriter = null;
        try {
            if (!bratDirectory.isDirectory())
                bratDirectory.mkdirs();
            File annotationConfFile = new File(bratDirectory, ANNOTATION_CONF_FILE);
            acWriter = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(annotationConfFile), ANNOTATION_CONF_ENCODING));
            bratTypesConfig.writeTo(acWriter);
        } catch (IOException e) {
            throw new AnalysisEngineProcessException(e);
        } finally {
            IOUtils.closeQuietly(acWriter);
        }
    }

    @Override
    public void process(CAS cas) throws AnalysisEngineProcessException {
        // extract target file name
        currentDocName = extractDocName(cas);
        // prepare paths
        BratDocument bratDoc = new BratDocument(bratDirectory, currentDocName);
        // write doc text
        String txt = cas.getDocumentText();
        try {
            FileUtils.write(bratDoc.getTxtFile(), txt, TXT_FILES_ENCODING);
        } catch (IOException e) {
            throw new AnalysisEngineProcessException(e);
        }

        // populate Brat annotation container
        bac = new BratAnnotationContainer(bratTypesConfig);
        context = new ToBratMappingContext();
        // start with entities
        for (Type uType : mapping.getEntityUimaTypes()) {
            UimaBratEntityMapping entMapping = mapping.getEntityMapping(uType);
            for (AnnotationFS uEntity : cas.getAnnotationIndex(uType)) {
                mapEntity(entMapping, uEntity);
            }
        }
        // then relations
        for (Type uType : mapping.getRelationUimaTypes()) {
            UimaBratRelationMapping relMapping = mapping.getRelationMapping(uType);
            for (AnnotationFS uRelation : cas.getAnnotationIndex(uType)) {
                mapRelation(relMapping, uRelation);
            }
        }
        // then events
        for (Type uType : mapping.getEventUimaTypes()) {
            UimaBratEventMapping evMapping = mapping.getEventMapping(uType);
            for (AnnotationFS uEvent : cas.getAnnotationIndex(uType)) {
                mapEvent(evMapping, uEvent);
            }
        }
        // write .ann file
        Writer annWriter = null;
        try {
            annWriter = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(bratDoc.getAnnFile()), ANN_FILES_ENCODING));
            bac.writeTo(annWriter);
        } catch (IOException e) {
            throw new AnalysisEngineProcessException(e);
        } finally {
            IOUtils.closeQuietly(annWriter);
        }
        // clear per-CAS state
        currentDocName = null;
        bac = null;
        context = null;
    }

    private void mapEntity(UimaBratEntityMapping entMapping, AnnotationFS uEntity) {
        if (context.isMapped(uEntity)) {
            return;
        }
        BratEntityType bType = entMapping.bratType;
        // create brat annotation instance
        BratEntity bEntity = new BratEntity(bType,
                uEntity.getBegin(), uEntity.getEnd(), uEntity.getCoveredText());
        // add to container - it assigns ID
        bEntity = bac.register(bEntity);
        // map to note
        mapNotes(entMapping, bEntity, uEntity);
        // memorize
        context.mapped(uEntity, bEntity);
    }

    private void mapRelation(UimaBratRelationMapping relMapping, AnnotationFS uRelation) {
        if (context.isMapped(uRelation)) {
            return;
        }
        BratRelationType bType = relMapping.bratType;
        Map<String, BratEntity> argMap = makeArgMap(
                uRelation, bType, relMapping.roleFeatures);
        if (argMap == null) {
            return;
        }
        // create
        BratRelation bRelation = new BratRelation(bType, argMap);
        // assign id
        bRelation = bac.register(bRelation);
        // map to note
        mapNotes(relMapping, bRelation, uRelation);
        // memorize
        context.mapped(uRelation, bRelation);
    }

    private void mapEvent(UimaBratEventMapping evMapping, AnnotationFS uEvent) {
        if (context.isMapped(uEvent)) {
            return;
        }
        BratEventType bType = evMapping.bratType;
        // use UIMA event annotation boundaries as Brat event trigger boundaries
        BratEventTrigger trigger = new BratEventTrigger(bType,
                uEvent.getBegin(), uEvent.getEnd(), uEvent.getCoveredText());
        // assign id to trigger
        trigger = bac.register(trigger);
        // fill slots
        Multimap<String, BratAnnotation<?>> roleAnnotations = makeRoleMap(
                uEvent, bType, evMapping.roleFeatures);
        // create
        BratEvent bEvent = new BratEvent(bType, trigger, roleAnnotations);
        // assign id
        bEvent = bac.register(bEvent);
        // map to note
        mapNotes(evMapping, bEvent, uEvent);
        // memorize
        context.mapped(uEvent, bEvent);
    }

    // fill relation roles
    private Map<String, BratEntity> makeArgMap(AnnotationFS uAnno,
                                               BratRelationType bratType, Map<String, Feature> argFeatMap) {
        Map<String, BratEntity> argAnnotations = Maps.newHashMapWithExpectedSize(2);
        for (String argName : argFeatMap.keySet()) {
            Feature argFeat = argFeatMap.get(argName);
            FeatureStructure argFS = uAnno.getFeatureValue(argFeat);
            if (argFS == null) {
                getLogger().warn(String.format(
                        "Can't map %s to Brat relation. Its feature '%s' is not set.",
                        toPrettyString(uAnno), argFeat));
                return null;
            }
            BratEntity argValue = context.demandEntity(argFS);
            argAnnotations.put(argName, argValue);
        }
        return argAnnotations;
    }

    // fill event roles
    private Multimap<String, BratAnnotation<?>> makeRoleMap(AnnotationFS uAnno,
                                                            BratEventType bratType, Map<String, Feature> roleFeatMap) {
        Multimap<String, BratAnnotation<?>> roleAnnotations = LinkedHashMultimap.create();
        for (String roleName : roleFeatMap.keySet()) {
            EventRole roleDesc = bratType.getRole(roleName);
            // check role range types
            boolean entityInRange = isEveryInstanceOf(roleDesc.getRangeTypes(),
                    BratEntityType.class);
            if (!entityInRange && !isEveryInstanceOf(roleDesc.getRangeTypes(), BratEventType.class)) {
                throw new UnsupportedOperationException(String.format(
                        "Mixed entity/event types in role range is not supported: %s", roleDesc));
            }
            //
            Feature roleFeat = roleFeatMap.get(roleName);
            FeatureStructure _roleFS = uAnno.getFeatureValue(roleFeat);
            if (_roleFS == null) {
                continue;
            }
            List<FeatureStructure> roleFSList;
            if (PUtils.hasCollectionRange(roleFeat)) {
                roleFSList = toList(roleFeat, _roleFS);
            } else {
                roleFSList = ImmutableList.of(_roleFS);
            }
            //
            for (FeatureStructure roleFS : roleFSList) {
                BratAnnotation<?> rv;
                if (entityInRange) {
                    rv = context.demandEntity(roleFS);
                } else { // role value should be event
                    rv = context.getEvent(roleFS, false);
                    if (rv == null) {
                        // means that a sub-event has not been mapped yet
                        // TODO implement nested event mapping
                        throw new UnsupportedOperationException(
                                "Nested event mapping is not supported yet");
                    }
                }
                roleAnnotations.put(roleName, rv);
            }
        }
        return roleAnnotations;
    }

    private static boolean isEveryInstanceOf(Iterable<?> srcCol, Class<?> testClass) {
        for (Object e : srcCol) {
            if (!testClass.isInstance(e)) {
                return false;
            }
        }
        return true;
    }

    /*
     * PRECONDITIONS: bAnno must have ID
     */
    private <BT extends BratType> void mapNotes(UimaBratTypeMappingBase<BT> mapping,
                                                BratAnnotation<BT> bAnno, AnnotationFS uAnno) {
        assert bAnno.getId() != null;
        BratNoteMapper noteMapper = mapping.noteMapper;
        if (noteMapper != null) {
            String noteContent = noteMapper.makeNote(uAnno);
            if (noteContent != null) {
                BratNoteAnnotation noteAnno = new BratNoteAnnotation(
                        bratTypesConfig.getUiNoteType(), bAnno, noteContent);
                noteAnno = bac.register(noteAnno);
            }
        }
    }

    private String toPrettyString(AnnotationFS anno) {
        return String.format("<%s, offset %s in %s>", anno.getCoveredText(), anno.getBegin(),
                currentDocName);
    }

    private String toPrettyString(FeatureStructure fs) {
        if (fs instanceof AnnotationFS) {
            return toPrettyString((AnnotationFS) fs);
        }
        return String.valueOf(fs);
    }

    private String extractDocName(CAS cas) {
        JCas jcas = null;
        try {
            jcas = cas.getJCas();
        } catch (CASException e) {
            throw new IllegalStateException(e);
        }
        DocumentMetadata metaAnno = JCasUtil.selectSingle(jcas, DocumentMetadata.class);
        Path outPath = outPathFunc.apply(metaAnno);
        String docName = flattenToSingleFilename(outPath);
        if (StringUtils.isBlank(docName)) {
            throw new IllegalStateException(String.format(
                    "Extracted empty doc name from meta: %s", metaAnno));
        }
        return docName;
    }

    private static final Joiner PATH_ELEM_JOINER = Joiner.on('-');

    private static String flattenToSingleFilename(Path p) {
        return PATH_ELEM_JOINER.join(p);
    }

    private void createBratTypesConfiguration() throws AnalysisEngineProcessException {
        // type configuration builder
        final BratTypesConfiguration.Builder tcBuilder = BratTypesConfiguration.builder();
        /*
         * define mapping initializer that will incrementally build
		 * required Brat type system as side effect
		 */
        UimaBratMappingInitializer initializer = new UimaBratMappingInitializer(ts,
                entitiesToBrat, relationsToBrat, eventsToBrat, noteMappersDefinitions) {

            @Override
            protected BratEntityType getEntityType(String typeName) {
                return tcBuilder.addEntityType(typeName);
            }

            @Override
            protected BratRelationType getRelationType(String typeName,
                                                       Map<String, String> argTypeNames) {
                return tcBuilder.addRelationType(typeName, argTypeNames);
            }

            @Override
            protected BratEventType getEventType(String typeName,
                                                 Map<String, String> roleTypeNames, Set<String> multiValuedRoles) {
                Map<String, Cardinality> roleCardinalities = Maps.newHashMap();
                for (String roleName : roleTypeNames.keySet()) {
                    Cardinality card = multiValuedRoles.contains(roleName)
                            ? Cardinality.ARRAY
                            : Cardinality.OPTIONAL;
                    roleCardinalities.put(roleName, card);
                }
                return tcBuilder.addEventType(typeName,
                        Multimaps.forMap(roleTypeNames), roleCardinalities);
            }
        };
        mapping = initializer.create();
        bratTypesConfig = tcBuilder.build();
    }

    private class ToBratMappingContext {
        private Map<AnnotationFS, BratAnnotation<?>> mappedAnnos = Maps.newHashMap();

        private boolean isMapped(AnnotationFS anno) {
            return mappedAnnos.containsKey(anno);
        }

        private BratEntity demandEntity(FeatureStructure fs) {
            return getMapped(fs, BratEntity.class, true);
        }

        private BratEvent getEvent(FeatureStructure fs, boolean require) {
            return getMapped(fs, BratEvent.class, require);
        }

        @SuppressWarnings("unchecked")
        private <B extends BratAnnotation<?>> B getMapped(
                FeatureStructure fs, Class<B> targetClass, boolean require) {
            BratAnnotation<?> result = mappedAnnos.get(fs);
            if (result == null) {
                if (require) {
                    throw new IllegalStateException(String.format(
                            "Can't find mapped instance for %s in %s",
                            toPrettyString(fs), currentDocName));
                }
                return null;
            }
            if (!targetClass.isInstance(result)) {
                throw new IllegalStateException(String.format(
                        "Unexpected mapped instance type for %s:\n required: %s\n actual:%s",
                        toPrettyString(fs), targetClass.getName(), result.getClass().getName()));
            }
            return (B) result;
        }

        private void mapped(AnnotationFS uAnno, BratAnnotation<?> bAnno) {
            mappedAnnos.put(uAnno, bAnno);
        }
    }
}