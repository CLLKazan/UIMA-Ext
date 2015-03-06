/**
 * 
 */
package ru.kfu.itis.cll.uima.consumer;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toPrettyString;
import static ru.kfu.itis.cll.uima.util.AnnotatorUtils.annotationTypeExist;
import static ru.kfu.itis.cll.uima.util.DocumentUtils.getDocumentUri;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.fit.component.CasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.CasUtil;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import ru.kfu.itis.cll.uima.io.IoUtils;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class IobWriter extends CasAnnotator_ImplBase {

	public static AnalysisEngineDescription createDescription(Iterable<String> encodeTypes,
			File outputDir) throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(IobWriter.class,
				PARAM_ENCODE_TYPES, newArrayList(encodeTypes),
				PARAM_OUTPUT_DIR, outputDir);
	}

	public static AnalysisEngineDescription createDescription(
			Iterable<String> encodeTypes, Iterable<String> encodeTypeLabels,
			File outputDir) throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(IobWriter.class,
				PARAM_ENCODE_TYPES, newArrayList(encodeTypes),
				PARAM_ENCODE_TYPE_LABELS, newArrayList(encodeTypeLabels),
				PARAM_OUTPUT_DIR, outputDir);
	}

	// parameter names
	public static final String PARAM_ENCODE_TYPES = "encodeTypes";
	public static final String PARAM_ENCODE_TYPE_LABELS = "encodeTypeLabels";
	public static final String PARAM_TOKEN_TYPE = "tokenType";
	public static final String PARAM_OUTPUT_DIR = "outputDir";
	//
	public static final String BEGIN_PREFIX = "B-";
	public static final String INSIDE_PREFIX = "I-";
	public static final String OUTSIDE_LABEL = "O";
	//
	public static final String OUTPUT_FILE_EXTENSION = ".iob";

	@ConfigurationParameter(name = PARAM_ENCODE_TYPES, mandatory = true)
	private List<String> encodeTypeNames;
	@ConfigurationParameter(name = PARAM_ENCODE_TYPE_LABELS)
	private List<String> encodeTypeLabels;
	@ConfigurationParameter(name = PARAM_TOKEN_TYPE, defaultValue = "ru.kfu.cll.uima.tokenizer.fstype.Token")
	private String tokenTypeName;
	@ConfigurationParameter(name = PARAM_OUTPUT_DIR, mandatory = true)
	private File outputDir;
	// derived
	private Map<Type, String> encodeTypesMap; // type => label
	private Type tokenType;
	// per-CAS state
	private URI docURI;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		try {
			FileUtils.forceMkdir(outputDir);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
		if (encodeTypeLabels != null && encodeTypeLabels.isEmpty()) {
			encodeTypeLabels = null;
		}
		if (encodeTypeLabels != null && encodeTypeNames.size() != encodeTypeLabels.size()) {
			throw new IllegalArgumentException(
					"encodeTypeLabels must have the same length with encodeTypes");
		}
	}

	@Override
	public void typeSystemInit(TypeSystem ts) throws AnalysisEngineProcessException {
		super.typeSystemInit(ts);
		//
		tokenType = ts.getType(tokenTypeName);
		annotationTypeExist(tokenTypeName, tokenType);
		//
		encodeTypesMap = Maps.newHashMap();
		for (int i = 0; i < encodeTypeNames.size(); i++) {
			String etn = encodeTypeNames.get(i);
			Type et = ts.getType(etn);
			annotationTypeExist(etn, et);
			//
			String etLabel = encodeTypeLabels != null ? encodeTypeLabels.get(i) : getTypeLabel(et);
			encodeTypesMap.put(et, etLabel);
		}
		encodeTypesMap = ImmutableMap.copyOf(encodeTypesMap);
	}

	@Override
	public void process(CAS cas) throws AnalysisEngineProcessException {
		String docURIStr = getDocumentUri(cas);
		if (docURIStr == null) {
			throw new UnsupportedOperationException("Document URI is not specified in a CAS");
		}
		// phase 0 - open output stream
		try {
			docURI = new URI(docURIStr);
		} catch (URISyntaxException e) {
			throw new AnalysisEngineProcessException(e);
		}
		String outFileName = new File(docURI).getName() + OUTPUT_FILE_EXTENSION;
		File outFile = new File(outputDir, outFileName);
		PrintWriter out;
		try {
			out = IoUtils.openPrintWriter(outFile);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
		try {
			process(cas, out);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	private void process(CAS cas, PrintWriter out) {
		AnnotationIndex<AnnotationFS> tokenIdx = cas.getAnnotationIndex(tokenType);
		Multimap<AnnotationFS, String> tokLabelsMap = HashMultimap.create(tokenIdx.size(), 1);
		// phase 1 - initialize map <token => label>
		for (Type et : encodeTypesMap.keySet()) {
			String typeLabel = encodeTypesMap.get(et);
			for (AnnotationFS encAnno : cas.getAnnotationIndex(et)) {
				Iterator<AnnotationFS> encAnnoTokens =
						CasUtil.selectCovered(tokenType, encAnno).iterator();
				if (!encAnnoTokens.hasNext()) {
					getLogger().warn(format("%s: %s does not cover any tokens",
							docURI, toPrettyString(encAnno)));
					continue;
				}
				// handle first token of encAnno
				tokLabelsMap.put(encAnnoTokens.next(), BEGIN_PREFIX + typeLabel);
				// handle other tokens
				while (encAnnoTokens.hasNext()) {
					tokLabelsMap.put(encAnnoTokens.next(), INSIDE_PREFIX + typeLabel);
				}
			}
		}
		// phase 2 - write token records into output stream
		for (AnnotationFS tok : tokenIdx) {
			List<String> recordFields = Lists.newLinkedList();
			recordFields.add(tok.getCoveredText());
			//
			Collection<String> tokLabels = tokLabelsMap.get(tok);
			if (tokLabels.isEmpty()) {
				recordFields.add(OUTSIDE_LABEL);
			} else {
				recordFields.addAll(tokLabels);
			}
			// 
			out.println(tabJoiner.join(recordFields));
		}
	}

	private static final Joiner tabJoiner = Joiner.on('\t');

	protected String getTypeLabel(Type t) {
		return t.getShortName();
	}
}
