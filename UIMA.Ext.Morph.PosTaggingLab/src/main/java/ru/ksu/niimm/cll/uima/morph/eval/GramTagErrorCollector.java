package ru.ksu.niimm.cll.uima.morph.eval;

import static ru.kfu.itis.issst.uima.morph.model.MorphConstants.POST;

import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.kfu.itis.cll.uima.eval.event.TypedPrintingEvaluationListener;
import ru.kfu.itis.issst.uima.morph.dictionary.MorphDictionaryAPIFactory;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.GramModel;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.collect.Table;

/**
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GramTagErrorCollector extends TypedPrintingEvaluationListener {

	private final Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private TypeSystem ts;
	// derived
	private MorphEvalHelper casHelper;
	private Map<String, Set<String>> gramCategoryContent;
	private Map<String, String> gram2Cat;
	// state fields
	private Table<String, String, MutableInt> errorTable = HashBasedTable.create();

	@Override
	public void onPartialMatch(final AnnotationFS goldAnno, final AnnotationFS sysAnno) {
		final FeatureStructure goldWf = casHelper.getWordform(goldAnno);
		final FeatureStructure sysWf = casHelper.getWordform(sysAnno);
		final Set<String> goldGrams = casHelper.getGrammems(goldWf);
		final String goldGC = getGramClass(goldWf, goldGrams);
		final Set<String> sysGrams = casHelper.getGrammems(sysWf);
		final String sysGC = getGramClass(sysWf, sysGrams);
		// check gram class
		checkTags(goldGC, sysGC);
		//
		Set<String> sysGramsHandled = Sets.newHashSet();
		sysGramsHandled.add(sysGC);
		Set<String> goldGramsHandled = Sets.newHashSet();
		goldGramsHandled.add(goldGC);
		// iterate through gold grammems
		for (String goldGr : goldGrams) {
			if (goldGramsHandled.contains(goldGr)) {
				continue;
			}
			// gold grams of the same category with goldGr
			Set<String> goldGrs = getGramsOfTheSameCat(goldGrams, goldGr);
			// sys grams of the same category with goldGr
			Set<String> sysGrs = getGramsOfTheSameCat(sysGrams, goldGr);
			// check
			checkTags(toTag(goldGC, goldGrs), toTag(sysGC, sysGrs));
			// mark as handled
			if (sysGrs != null) {
				sysGramsHandled.addAll(sysGrs);
			}
			goldGramsHandled.addAll(goldGrs);
		}
		// report spurious grammems
		Set<String> nonHandledSys = Sets.newHashSet(Sets.difference(sysGrams, sysGramsHandled));
		for (String sysGr : nonHandledSys) {
			if (sysGramsHandled.contains(sysGr)) {
				continue;
			}
			// sys grams of the same category with sysGr
			Set<String> sysGrs = getGramsOfTheSameCat(sysGrams, sysGr);
			checkTags(toTag(goldGC, null), toTag(sysGC, sysGrs));
			sysGramsHandled.addAll(sysGrs);
		}
	}

	private Set<String> getGramsOfCat(Set<String> srcGrams, String cat) {
		Set<String> catGrams = gramCategoryContent.get(cat);
		if (catGrams == null) {
			throw new IllegalStateException();
		}
		SetView<String> resultSet = Sets.intersection(srcGrams, catGrams);
		if (resultSet.isEmpty()) {
			return null;
		}
		return resultSet;
	}

	private Set<String> getGramsOfTheSameCat(Set<String> srcGrams, String refGr) {
		String cat = gram2Cat.get(refGr);
		if (cat == null) {
			return srcGrams.contains(refGr) ? Collections.singleton(refGr) : null;
		} else {
			return getGramsOfCat(srcGrams, cat);
		}
	}

	private static Joiner grJoiner = Joiner.on('_');

	private String toTag(String gramClass, Set<String> grams) {
		gramClass = String.valueOf(gramClass);
		String gramsStr;
		if (grams == null || grams.isEmpty()) {
			gramsStr = "null";
		} else if (grams.size() == 1) {
			gramsStr = grams.iterator().next();
		} else {
			List<String> sortedGrams = Lists.newArrayList(grams);
			Collections.sort(sortedGrams);
			gramsStr = grJoiner.join(sortedGrams);
		}
		return new StringBuilder(gramClass).append('_').append(gramsStr).toString();
	}

	private void checkTags(String goldTag, String sysTag) {
		// avoid nulls
		goldTag = String.valueOf(goldTag);
		sysTag = String.valueOf(sysTag);
		if (!Objects.equal(goldTag, sysTag)) {
			MutableInt errCounter = errorTable.get(goldTag, sysTag);
			if (errCounter == null) {
				errCounter = new MutableInt();
				errorTable.put(goldTag, sysTag, errCounter);
			}
			errCounter.increment();
		}
	}

	private String getGramClass(FeatureStructure fs, Set<String> grams) {
		Set<String> gramClassSet = getGramsOfCat(grams, POST);
		if (gramClassSet == null || gramClassSet.isEmpty()) {
			return null;
		} else if (gramClassSet.size() > 1) {
			throw new IllegalStateException(String.format(
					"Too much gram class grammems in %s", fs));
		} else {
			return gramClassSet.iterator().next();
		}
	}

	@PostConstruct
	@Override
	protected void init() throws Exception {
		casHelper = new MorphEvalHelper(ts);
		//
		GramModel gm = MorphDictionaryAPIFactory.getMorphDictionaryAPI().getGramModel();
		initGramTree(gm);

		super.init();
	}

	private void initGramTree(GramModel gm) {
		gramCategoryContent = Maps.newHashMap();
		gram2Cat = Maps.newHashMap();
		for (String cat : gm.getTopGrammems()) {
			BitSet catBS = gm.getGrammemWithChildrenBits(cat, true);
			List<String> catGrams = gm.toGramSet(catBS);
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