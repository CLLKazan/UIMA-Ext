/**
 *
 */
package ru.kfu.itis.issst.cleartk.crfsuite;

import java.io.Closeable;
import java.io.File;
import java.util.List;

import org.cleartk.classifier.CleartkProcessingException;
import org.cleartk.classifier.Feature;
import org.cleartk.classifier.encoder.features.FeaturesEncoder;
import org.cleartk.classifier.encoder.features.NameNumber;
import org.cleartk.classifier.encoder.outcome.OutcomeEncoder;
import org.cleartk.classifier.jar.SequenceClassifier_ImplBase;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import ru.kfu.itis.issst.cleartk.Disposable;
import ru.kfu.itis.issst.crfsuite4j.Attribute;
import ru.kfu.itis.issst.crfsuite4j.CrfSuiteTagger;

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
public class CRFSuiteStringOutcomeClassifier extends
		SequenceClassifier_ImplBase<List<NameNumber>, String, String>
		implements Disposable, Closeable {

	// config fields
	@SuppressWarnings("unused")
	private File modelFile;
	// derived
	private CrfSuiteTagger tagger;

	public CRFSuiteStringOutcomeClassifier(File modelFile,
			FeaturesEncoder<List<NameNumber>> featuresEncoder,
			OutcomeEncoder<String, String> outcomeEncoder) {
		super(featuresEncoder, outcomeEncoder);
		this.modelFile = modelFile;
		tagger = new CrfSuiteTagger(modelFile);
	}

	@Override
	public List<String> classify(List<List<Feature>> srcItemSeq) throws CleartkProcessingException {
		List<List<Attribute>> itemSeq = Lists.newLinkedList();
		for (List<Feature> srcItem : srcItemSeq) {
			List<NameNumber> encodedSrcItem = featuresEncoder.encodeAll(srcItem);
			List<Attribute> item = Lists.transform(encodedSrcItem, nameNumber2attributeFunc);
			itemSeq.add(item);
		}
		if (itemSeq.size() != srcItemSeq.size()) {
			throw new IllegalStateException();
		}
		// invoke
		List<String> labels = tagger.tag(itemSeq);
		// sanity check
		if (labels.size() != itemSeq.size()) {
			throw new IllegalStateException(
					String.format(
							"CrfSuiteTagger returned labels sequence with size different from given item sequence:\n"
									+
									"labels: %s\n" +
									"items: %s", labels, itemSeq));
		}
		return labels;
	}

	@Override
	protected void finalize() throws Throwable {
		if (tagger != null) {
			tagger.dispose();
			tagger = null;
		}
		super.finalize();
	}

	@Override
	public void dispose() {
		if (tagger != null) {
			tagger.dispose();
			tagger = null;
		}
	}

    @Override
    public void close() {
        dispose();
    }

    private static final Function<NameNumber, Attribute> nameNumber2attributeFunc = new Function<NameNumber, Attribute>() {
		@Override
		public Attribute apply(NameNumber arg) {
			return new Attribute(arg.name);
		}
	};
}
