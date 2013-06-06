package ru.kfu.itis.issst.uima.morph.treetagger;

import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toPrettyString;
import static ru.kfu.itis.cll.uima.util.AnnotatorUtils.annotationTypeExist;
import static ru.kfu.itis.cll.uima.util.AnnotatorUtils.featureExist;

import java.io.File;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.uima.cas.ArrayFS;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.StringArrayFS;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.collect.Table;

import ru.kfu.itis.cll.uima.eval.event.PrintingEvaluationListener;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.DictionaryDeserializer;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary;

/**
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GramTagErrorCollector extends PrintingEvaluationListener {

	public static final String MORPH_DICT_HOME_KEY = "opencorpora.home";
	public static final String SERIALIZED_MORPH_DICT_NAME = "dict.opcorpora.ser";

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private TypeSystem ts;
	private File serializedMorphDictFile;
	// derived
	private Map<String, Set<String>> gramCategoryContent;
	private Map<String, String> gram2Cat;
	private Feature wordformsFeat;
	private Feature posFeat;
	private Feature gramsFeat;
	// state fields
	private Table<String, String, MutableInt> errorTable = HashBasedTable.create();

	public void setSerializedMorphDictFile(File serializedMorphDictFile) {
		this.serializedMorphDictFile = serializedMorphDictFile;
	}

	@Override
	public void onPartialMatch(AnnotationFS goldAnno, AnnotationFS sysAnno) {
		FeatureStructure goldWf = getWordform(goldAnno);
		FeatureStructure sysWf = getWordform(sysAnno);
		Set<String> goldGrams = getGrammems(goldWf);
		String goldPos = getPos(goldWf);
		Set<String> sysGrams = getGrammems(sysWf);
		String sysPos = getPos(sysWf);
		// check POS
		checkTags(goldPos, sysPos);
		// iterate through gold grammems
		Set<String> sysGramsHandled = Sets.newHashSet();
		for (String goldGr : goldGrams) {
			String grCat = getGramCat(goldGr);
			String sysGr = getGramOfCat(goldAnno, sysGrams, grCat);
			checkTags(toTag(goldPos, goldGr), toTag(sysPos, sysGr));
			if (sysGr != null) {
				sysGramsHandled.add(sysGr);
			}
		}
		// report spurious grammems
		for (String sysGr : Sets.difference(sysGramsHandled, sysGramsHandled)) {
			checkTags(toTag(goldPos, null), toTag(sysPos, sysGr));
		}
	}

	private String getGramCat(String grId) {
		String cat = gram2Cat.get(grId);
		if (cat == null) {
			throw new IllegalArgumentException(String.format(
					"Unknown grammeme: %s", grId));
		}
		return cat;
	}

	private String getGramOfCat(AnnotationFS word, Set<String> sysGrams, String cat) {
		Set<String> catGrams = gramCategoryContent.get(cat);
		if (catGrams == null) {
			throw new IllegalStateException();
		}
		SetView<String> resultSet = Sets.intersection(sysGrams, catGrams);
		if (resultSet.isEmpty()) {
			return null;
		}
		if (resultSet.size() > 1) {
			log.warn("Several grammems of category '{}' in {}: {}", new Object[] {
					cat, toPrettyString(word), sysGrams });
		}
		return resultSet.iterator().next();
	}

	private String toTag(String pos, String gram) {
		return new StringBuilder(pos).append('_').append(gram).toString();
	}

	private void checkTags(String goldTag, String sysTag) {
		if (!Objects.equal(goldTag, sysTag)) {
			MutableInt errCounter = errorTable.get(goldTag, sysTag);
			if (errCounter == null) {
				errCounter = new MutableInt();
				errorTable.put(goldTag, sysTag, errCounter);
			}
			errCounter.increment();
		}
	}

	private String getPos(FeatureStructure wf) {
		return String.valueOf(wf.getStringValue(posFeat));
	}

	private FeatureStructure getWordform(AnnotationFS word) {
		ArrayFS wfs = (ArrayFS) word.getFeatureValue(wordformsFeat);
		if (wfs == null || wfs.size() == 0) {
			return null;
		}
		if (wfs.size() > 1) {
			log.warn(">1 wordforms for {} in {}", toPrettyString(word), currentDocUri);
		}
		return wfs.get(0);
	}

	private Set<String> getGrammems(FeatureStructure wf) {
		if (wf == null) {
			return ImmutableSet.of();
		}
		StringArrayFS grams = (StringArrayFS) wf.getFeatureValue(gramsFeat);
		if (grams == null) {
			return ImmutableSet.of();
		}
		ImmutableSet.Builder<String> resultBuilder = ImmutableSet.builder();
		for (int i = 0; i < grams.size(); i++) {
			resultBuilder.add(grams.get(i));
		}
		return resultBuilder.build();
	}

	@PostConstruct
	@Override
	protected void init() throws Exception {
		Type wordType = ts.getType(Word.class.getName());
		annotationTypeExist(Word.class.getName(), wordType);
		Type wfType = ts.getType(Wordform.class.getName());
		annotationTypeExist(Wordform.class.getName(), wfType);
		wordformsFeat = featureExist(wordType, "wordforms");
		posFeat = featureExist(wfType, "pos");
		gramsFeat = featureExist(wfType, "grammems");

		if (serializedMorphDictFile == null) {
			String mdHomePath = System.getProperty(MORPH_DICT_HOME_KEY);
			if (mdHomePath != null) {
				File mdHomeDir = new File(mdHomePath);
				if (!mdHomeDir.isDirectory()) {
					throw new IllegalStateException(String.format(
							"%s is not existing directory", mdHomeDir));
				}
				serializedMorphDictFile = new File(mdHomeDir, SERIALIZED_MORPH_DICT_NAME);
				if (!serializedMorphDictFile.isFile()) {
					serializedMorphDictFile = null;
				}
			}
		}
		if (serializedMorphDictFile == null) {
			throw new IllegalStateException(
					"serializedMorphDictFile property has not been set and opencorpora.home system property "
							+ "does not point to directory with " + SERIALIZED_MORPH_DICT_NAME
							+ " file");
		}
		MorphDictionary morphDict = DictionaryDeserializer.from(serializedMorphDictFile);
		initGramTree(morphDict);

		super.init();
	}

	private void initGramTree(MorphDictionary morphDict) {
		gramCategoryContent = Maps.newHashMap();
		gram2Cat = Maps.newHashMap();
		for (String cat : morphDict.getTopGrammems()) {
			BitSet catBS = morphDict.getGrammemWithChildrenBits(cat, true);
			List<String> catGrams = morphDict.toGramSet(catBS);
			gramCategoryContent.put(cat, ImmutableSet.copyOf(catGrams));
			for (String grId : catGrams) {
				gram2Cat.put(grId, cat);
			}
		}
		gramCategoryContent = ImmutableMap.copyOf(gramCategoryContent);
		gram2Cat = ImmutableMap.copyOf(gram2Cat);
		// log gram tree
		log.info("Grammeme categories:\n{}",
				Joiner.on('\n').withKeyValueSeparator(" => ").join(gramCategoryContent));
	}

	@Override
	public void onMissing(AnnotationFS goldAnno) {
	}

	@Override
	public void onExactMatch(AnnotationFS goldAnno, AnnotationFS sysAnno) {
	}

	@Override
	public void onSpurious(AnnotationFS sysAnno) {
	}

	@Override
	public void onEvaluationComplete() {
		Map<String, Map<String, MutableInt>> errorRowMap = errorTable.rowMap();
		for (String goldTag : errorRowMap.keySet()) {
			Map<String, MutableInt> errorTableRow = errorRowMap.get(goldTag);
			for (String sysTag : errorTableRow.keySet()) {
				MutableInt errCounter = errorTableRow.get(sysTag);
				printer.println(String.format("%s\t%s\t%s", goldTag, sysTag, errCounter));
			}
		}
		printer.flush();
		clean();
	}

}