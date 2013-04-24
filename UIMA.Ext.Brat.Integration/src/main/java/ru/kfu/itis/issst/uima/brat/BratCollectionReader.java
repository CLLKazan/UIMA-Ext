package ru.kfu.itis.issst.uima.brat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.nlplab.brat.ann.BratAnnotation;
import org.nlplab.brat.ann.BratAnnotationContainer;
import org.nlplab.brat.ann.BratEntity;
import org.nlplab.brat.ann.BratRelation;
import org.nlplab.brat.configuration.BratEntityType;
import org.nlplab.brat.configuration.BratRelationType;
import org.nlplab.brat.configuration.BratTypesConfiguration;
import org.uimafit.component.CasCollectionReader_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;

import com.google.common.collect.Maps;

import static ru.kfu.itis.issst.uima.brat.BratConstants.*;

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

	@ConfigurationParameter(name = PARAM_BRAT_COLLECTION_DIR, mandatory = true)
	private File bratCollectionDir;

	// config fields
	// TODO init
	private BratTypesConfiguration bratTypesCfg;
	private BratUimaMapping mapping;
	// state fields
	private Iterator<BratDocument> bratDocIter;
	// per-CAS state fields
	private String currentDocName;
	private FromBratMappingContext mappingCtx;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		// TODO make bratDocIter 
	}

	@Override
	public void typeSystemInit(TypeSystem ts) throws ResourceInitializationException {
		// TODO Auto-generated method stub
		super.typeSystemInit(ts);
	}

	@Override
	public boolean hasNext() throws IOException, CollectionException {
		return bratDocIter.hasNext();
	}

	@Override
	public void getNext(CAS cas) throws IOException, CollectionException {
		BratDocument bratDoc = bratDocIter.next();
		// read and set text
		String txt = FileUtils.readFileToString(bratDoc.getTxtFile(), TXT_FILES_ENCODING);
		cas.setDocumentText(txt);

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
			Type uType = mapping.getRelationUimaType(bType);
			for (BratRelation bRelation : bratContainer.getRelations(bType)) {
				// TODO
			}
		}
		// map event types
		// TODO
		// clean per-CAS state
		currentDocName = null;
		mappingCtx = null;
	}

	@Override
	public Progress[] getProgress() {
		// TODO Auto-generated method stub
		return null;
	}

	private class FromBratMappingContext {
		private Map<String, AnnotationFS> mappedAnnotations = Maps.newHashMap();

		private boolean isMapped(BratAnnotation<?> bAnno) {
			return mappedAnnotations.containsKey(bAnno.getId());
		}

		private void mapped(BratAnnotation<?> bAnno, AnnotationFS uAnno) {
			if (mappedAnnotations.put(bAnno.getId(), uAnno) != null) {
				// sanity check
				throw new IllegalStateException();
			}
		}
	}
}