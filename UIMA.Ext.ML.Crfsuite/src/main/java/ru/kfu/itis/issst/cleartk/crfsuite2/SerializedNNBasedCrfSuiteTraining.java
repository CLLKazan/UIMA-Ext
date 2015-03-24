package ru.kfu.itis.issst.cleartk.crfsuite2;

import ru.kfu.itis.issst.cleartk.SerializableNameNumber;
import ru.kfu.itis.issst.crfsuite4j.AbstractCrfSuiteTraining;
import ru.kfu.itis.issst.crfsuite4j.Attribute;
import ru.kfu.itis.issst.crfsuite4j.CrfSuiteTrainer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static ru.kfu.itis.issst.cleartk.SequenceSerializedDataWriter_ImplBase.isDataEnd;
import static ru.kfu.itis.issst.cleartk.SequenceSerializedDataWriter_ImplBase.isSequenceEnd;

/**
 * @author Rinat Gareev
 */
public class SerializedNNBasedCrfSuiteTraining extends AbstractCrfSuiteTraining {
    private ObjectInputStream in;

    public ObjectInputStream getIn() {
        return in;
    }

    public void setIn(ObjectInputStream in) {
        this.in = in;
    }

    @Override
    protected void validateConfig() {
        super.validateConfig();
        if (in == null) {
            throw new IllegalStateException("Input ObjectStream is null");
        }
    }

    @Override
    protected void appendTrainingData(CrfSuiteTrainer trainer) throws IOException {
        log.info("Parsing training data...");
        List<List<Attribute>> items = newArrayList();
        List<String> labels = newArrayList();
        int instancesCounter = 0;
        //
        Object curObj;
        while (!isDataEnd(curObj = readObject())) {
            if (isSequenceEnd(curObj)) {
                if (items.isEmpty()) {
                    log.warn("Empty instance");
                } else {
                    trainer.append(items, labels, 0);
                    instancesCounter++;
                    items.clear();
                    labels.clear();
                }
            } else {
                // read List of NameNumber with one label
                @SuppressWarnings("unchecked")
                List<SerializableNameNumber> nnList = (List<SerializableNameNumber>) curObj;
                String label = (String) readObject();
                //
                items.add(toAttributes(nnList));
                // TODO should we wrap null ref into "null" string?
                labels.add(String.valueOf(label));
            }
        }
        if (!items.isEmpty()) {
            trainer.append(items, labels, 0);
            instancesCounter++;
        }
        // report
        log.info("{} instances have been read", instancesCounter);
    }

    private Object readObject() throws IOException {
        try {
            return in.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }

    private static List<Attribute> toAttributes(List<SerializableNameNumber> nnList) {
        List<Attribute> resultList = newArrayList();
        for (SerializableNameNumber nn : nnList) {
            Attribute attr = new Attribute(nn.name, nn.number.doubleValue());
            resultList.add(attr);
        }
        return resultList;
    }
}
