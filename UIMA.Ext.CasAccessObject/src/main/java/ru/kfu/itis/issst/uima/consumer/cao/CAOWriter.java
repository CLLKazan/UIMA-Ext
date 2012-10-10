/**
 * 
 */
package ru.kfu.itis.issst.uima.consumer.cao;

import static java.lang.System.currentTimeMillis;
import static ru.kfu.itis.cll.uima.util.AnnotatorUtils.annotationTypeExist;
import static ru.kfu.itis.cll.uima.util.AnnotatorUtils.featureExist;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.uimafit.component.CasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.descriptor.OperationalProperties;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
@OperationalProperties(modifiesCas = false)
public class CAOWriter extends CasAnnotator_ImplBase {

	public static final String PARAM_SPAN_TYPE = "SpanAnnotationType";
	public static final String RESOURCE_DAO = "CasAccessObject";
	public static final String PARAM_TYPES_TO_PERSIST = "TypesToPersist";
	public static final String PARAM_DOC_METADATA_TYPE = "DocumentMetadataType";
	public static final String PARAM_DOC_METADATA_URI_FEATURE = "MetadataUriFeature";
	private static final String UIMA_ANNOTATION_TYPE = "uima.tcas.Annotation";
	public static final String PARAM_DOC_METADATA_START_PROCESSING_TIME = "MetadataStartProcessingTimeFeature";
	public static final String PARAM_DOC_METADATA_DOCUMENT_SIZE = "MetadataDocumentSizeFeature";

	@ExternalResource(key = RESOURCE_DAO)
	private CasAccessObject dao;
	@ConfigurationParameter(name = PARAM_TYPES_TO_PERSIST, mandatory = true)
	private String[] persistingTypeNames;
	@ConfigurationParameter(name = PARAM_SPAN_TYPE, mandatory = true)
	private String spanTypeName;
	@ConfigurationParameter(name = PARAM_DOC_METADATA_TYPE,
			defaultValue = "ru.kfu.itis.cll.uima.commons.DocumentMetadata")
	private String docMetaTypeName;
	@ConfigurationParameter(name = PARAM_DOC_METADATA_URI_FEATURE,
			defaultValue = "sourceUri")
	private String docMetaUriFeatureName;
	@ConfigurationParameter(name = PARAM_DOC_METADATA_START_PROCESSING_TIME)
	private String docMetaStartProcessingTimeFeatureName;
	@ConfigurationParameter(name = PARAM_DOC_METADATA_DOCUMENT_SIZE)
	private String docMetaDocumentSizeFeatureName;

	// derived
	private Long launchId;
	private List<Type> persistingTypes;
	private Type spanType;
	private Type docMetaType;
	private Feature metaUriFeature;
	private Feature metaDocumentSizeFeature;
	private Feature metaStartProcessingTimeFeature;
	private AnnotationPersisterFactory persisterFactory = new AnnotationPersisterFactory();

	// deployment-wide state
	private long timeTaken;
	private int casSuccessfullyProcessed;
	private TypeSystem typeSystem;

	// per-CAS state
	// HAS TO BE CLEARED
	private FSIterator<Annotation> spanIterator;
	private PersistenceContext persistenceContext;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);

		launchId = dao.persistLaunch(new Date());

		info("CAOWriter has been initialized. Launch id = %s", launchId);
	}

	@Override
	public void typeSystemInit(TypeSystem ts) throws AnalysisEngineProcessException {
		super.typeSystemInit(ts);
		this.typeSystem = ts;
		spanType = ts.getType(spanTypeName);
		annotationTypeExist(spanTypeName, spanType);

		persistingTypes = new ArrayList<Type>(persistingTypeNames.length);
		for (String curTypeName : persistingTypeNames) {
			Type curType = ts.getType(curTypeName);
			annotationTypeExist(curTypeName, curType);
			persistingTypes.add(curType);
			// preload persister
			persisterFactory.makeAndRegisterPersister(curType);
		}

		docMetaType = ts.getType(docMetaTypeName);
		annotationTypeExist(docMetaTypeName, docMetaType);
		metaUriFeature = featureExist(docMetaType, docMetaUriFeatureName);
		if (docMetaStartProcessingTimeFeatureName != null) {
			metaStartProcessingTimeFeature = featureExist(
					docMetaType, docMetaStartProcessingTimeFeatureName);
		}
		if (docMetaDocumentSizeFeatureName != null) {
			metaDocumentSizeFeature = featureExist(
					docMetaType, docMetaDocumentSizeFeatureName);
		}
	}

	@Override
	public void process(CAS cas) throws AnalysisEngineProcessException {
		try {
			process(cas.getJCas());
		} catch (CASException e) {
			throw new AnalysisEngineProcessException(e);
		} finally {
			// clear state
			persistenceContext = null;
			spanIterator = null;
		}
	}

	private void process(JCas cas) {
		long timeBefore = currentTimeMillis();

		Annotation docMeta = getDocumentMeta(cas);
		if (docMeta == null) {
			warn("Document meta is NULL. Skipping entire document...");
			return;
		}
		String docURI = docMeta.getStringValue(metaUriFeature);
		Long size = null;
		if (metaDocumentSizeFeature != null) {
			size = docMeta.getLongValue(metaDocumentSizeFeature);
			if (size == 0) {
				size = null;
			}
		}
		Long processingTime = null;
		if (metaStartProcessingTimeFeature != null) {
			long startTime = docMeta.getLongValue(metaStartProcessingTimeFeature);
			if (startTime != 0) {
				processingTime = System.nanoTime() - startTime;
			}
		}
		info("Persisting annotations from %s", docURI);
		long docId = dao.persistDocument(launchId, docURI, size, processingTime);

		AnnotationIndex<Annotation> spanIndex = cas.getAnnotationIndex(spanType);
		if (spanIndex.size() == 0) {
			warn("Span annotations index is EMPTY. Skipping entire document...");
			return;
		}
		spanIterator = spanIndex.iterator();
		persistenceContext = new PersistenceContext();

		for (Type type : persistingTypes) {
			AnnotationIndex<Annotation> annoIndex = cas.getAnnotationIndex(type);
			for (Annotation anno : annoIndex) {
				if (persistenceContext.getId(anno) == null) {
					AnnotationPersister persister = persisterFactory.getPersister(anno.getType());
					if (persister == null) {
						throw new IllegalStateException(
								"Null persister for type: " + anno.getType());
					}
					long spanId = getPersistedEnclosingSpan(docId, anno);
					persister.execute(spanId, anno);
				}
			}
		}

		// timing
		long timeElapsed = currentTimeMillis() - timeBefore;
		timeTaken += timeElapsed;
		casSuccessfullyProcessed++;
		info("Persisting finished for %s. Time elapsed: %s ms. In average: %s ms",
				docURI, timeElapsed, timeTaken / casSuccessfullyProcessed);
	}

	private Annotation getDocumentMeta(JCas cas) {
		AnnotationIndex<Annotation> metaIndex = cas
				.getAnnotationIndex(docMetaType);
		FSIterator<Annotation> iter = metaIndex.iterator();
		iter.moveToFirst();
		if (iter.isValid()) {
			return iter.get();
		} else {
			return null;
		}
	}

	private long getPersistedEnclosingSpan(long docId, Annotation anno) {
		Annotation span = getEnclosingSpan(anno);
		Long spanId = persistenceContext.getId(span);
		if (spanId == null) {
			spanId = dao.persistSpan(docId, span.getCoveredText());
			persistenceContext.persisted(span, spanId);
		}
		return spanId;
	}

	private Annotation getEnclosingSpan(Annotation target) {
		Annotation span;
		spanIterator.moveTo(target);
		if (!spanIterator.isValid()) {
			spanIterator.moveToPrevious();
			if (spanIterator.isValid()) {
				span = spanIterator.get();
			} else {
				throw new IllegalStateException("Span iterator seems to be empty");
			}
		} else {
			// check for equality
			span = spanIterator.get();
			if (span.getBegin() != target.getBegin()) {
				// sanity check
				if (!(span.getBegin() > target.getBegin())) {
					throw new IllegalStateException("Assertion failed.");
				}
				// so this span is greater than 'target'
				// should get previous span if any
				spanIterator.moveToPrevious();
				if (!spanIterator.isValid()) {
					throw new IllegalStateException("Asserton failed.");
				} else {
					span = spanIterator.get();
				}
			}
		}
		// sanity check
		if (!(span.getBegin() <= target.getBegin() && span.getEnd() >= target.getEnd())) {
			throw new IllegalStateException(
					String.format("Span extraction is failed.\nTarget: %s\nSpan: %s",
							target.getCoveredText(), span.getCoveredText()));
		}
		return span;
	}

	private void info(String msg, Object... args) {
		getContext().getLogger().log(Level.INFO, String.format(msg, args));
	}

	private void warn(String msg, Object... args) {
		getContext().getLogger().log(Level.WARNING, String.format(msg, args));
	}

	private class AnnotationPersisterFactory {
		private Map<Type, AnnotationPersister> persisters = new LinkedHashMap<Type, AnnotationPersister>();

		AnnotationPersister getPersister(Type type) {
			AnnotationPersister persister = persisters.get(type);
			if (persister == null) {
				persister = makePersister(type);
				persisters.put(type, persister);
			}
			return persister;
		}

		void makeAndRegisterPersister(Type type) {
			persisters.put(type, makePersister(type));
		}

		AnnotationPersister makePersister(Type type) {
			List<FeaturePersister> featurePersisters = new LinkedList<FeaturePersister>();
			for (Feature f : type.getFeatures()) {
				if (isAnnotationType(f.getRange())) {
					featurePersisters.add(new AnnotationFeaturePersister(f));
				} else {
					warn("Feature %s with domain %s and range %s will not be persisted",
							f.getName(), f.getDomain(), f.getRange());
				}
			}
			return new AnnotationPersister(type, featurePersisters);
		}

		private boolean isAnnotationType(Type type) {
			Type annoBaseType = typeSystem.getType(UIMA_ANNOTATION_TYPE);
			return typeSystem.subsumes(annoBaseType, type);
		}
	}

	private class AnnotationPersister {
		private Type type;
		private List<FeaturePersister> featurePersisters;

		AnnotationPersister(Type type, List<FeaturePersister> featurePersisters) {
			this.type = type;
			this.featurePersisters = featurePersisters;
		}

		long execute(long spanId, Annotation anno) {
			persistenceContext.startPersisting(anno);
			// sanity check
			if (!anno.getCAS().getTypeSystem().subsumes(type, anno.getType())) {
				throw new IllegalStateException(
						String.format(
								"Illegal annotation persister invocation\nPersister type: %s\nAnno type: %s",
								type.getName(), anno.getType().getName()));
			}
			long annoId = dao.persistAnnotation(
					type.getShortName(), spanId, anno.getCoveredText(),
					anno.getBegin(), anno.getEnd());
			for (FeaturePersister fe : featurePersisters) {
				fe.execute(spanId, annoId, anno);
			}
			persistenceContext.persisted(anno, annoId);
			return annoId;
		}
	}

	private interface FeaturePersister {
		void execute(long spanId, long annoId, Annotation anno);
	}

	private class AnnotationFeaturePersister implements FeaturePersister {
		private Feature feature;

		AnnotationFeaturePersister(Feature feature) {
			this.feature = feature;
		}

		@Override
		public void execute(long spanId, long annoId, Annotation anno) {
			FeatureStructure featureValue = anno.getFeatureValue(feature);
			if (featureValue == null) {
				return;
			}
			// sanity check
			if (!(featureValue instanceof Annotation)) {
				throw new IllegalStateException(String.format(
						"featureValue is not subtype of Annotation\nvalue: %s\nobject class: %s",
						featureValue, featureValue.getClass()));
			}
			// persist value anno if required
			Annotation valueAnno = (Annotation) featureValue;
			Long valueAnnoId = persistenceContext.getId(valueAnno);
			if (valueAnnoId == null) {
				AnnotationPersister valueAnnoPersister = persisterFactory.getPersister(
						valueAnno.getType());
				if (valueAnnoPersister == null) {
					throw new IllegalStateException(String.format("No persister for Type: %s",
							valueAnno.getType()));
				}
				valueAnnoId = valueAnnoPersister.execute(spanId, valueAnno);
			}
			// persist feature
			dao.persistFeature(annoId, feature.getShortName(), valueAnnoId);
		}
	}

	private class PersistenceContext {
		private Map<Annotation, Long> persistedAnnotations = new HashMap<Annotation, Long>();

		void startPersisting(Annotation anno) {
			// null means that the persisting for given anno will be started immediately after calling this method
			// this is necessary to deal with RECURSIVE annotation references
			persistedAnnotations.put(anno, null);
		}

		void persisted(Annotation anno, long annoId) {
			persistedAnnotations.put(anno, annoId);
		}

		Long getId(Annotation anno) {
			Long result = persistedAnnotations.get(anno);
			if (result != null) {
				return result;
			}
			if (persistedAnnotations.containsKey(anno)) {
				throw new IllegalStateException("Seems to be recursion on annotation: " + anno);
			}
			return null;
		}
	}
}