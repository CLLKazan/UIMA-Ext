/**
 *
 */
package ru.kfu.itis.issst.cleartk.crfsuite;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.cleartk.ml.CleartkProcessingException;
import org.cleartk.ml.Feature;
import org.cleartk.ml.encoder.features.FeaturesEncoder;
import org.cleartk.ml.encoder.outcome.OutcomeEncoder;
import org.cleartk.ml.jar.SequenceClassifier_ImplBase;
import ru.kfu.itis.issst.cleartk.Disposable;
import ru.kfu.itis.issst.cleartk.SerializableNameNumber;
import ru.kfu.itis.issst.crfsuite4j.Attribute;
import ru.kfu.itis.issst.crfsuite4j.CrfSuiteTagger;

import java.io.Closeable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rinat Gareev (Kazan Federal University)
 */
public class CRFSuiteStringOutcomeClassifier extends
        SequenceClassifier_ImplBase<ArrayList<SerializableNameNumber>, String, String>
        implements Disposable, Closeable {

    // config fields
    @SuppressWarnings("unused")
    private File modelFile;
    // derived
    private CrfSuiteTagger tagger;

    public CRFSuiteStringOutcomeClassifier(File modelFile,
                                           FeaturesEncoder<ArrayList<SerializableNameNumber>> featuresEncoder,
                                           OutcomeEncoder<String, String> outcomeEncoder) {
        super(featuresEncoder, outcomeEncoder);
        this.modelFile = modelFile;
        tagger = new CrfSuiteTagger(modelFile);
    }

    @Override
    public List<String> classify(List<List<Feature>> srcItemSeq) throws CleartkProcessingException {
        List<List<Attribute>> itemSeq = Lists.newLinkedList();
        for (List<Feature> srcItem : srcItemSeq) {
            List<SerializableNameNumber> encodedSrcItem = featuresEncoder.encodeAll(srcItem);
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

    private static final Function<SerializableNameNumber, Attribute> nameNumber2attributeFunc
            = new Function<SerializableNameNumber, Attribute>() {
        @Override
        public Attribute apply(SerializableNameNumber arg) {
            return new Attribute(arg.name);
        }
    };
}
