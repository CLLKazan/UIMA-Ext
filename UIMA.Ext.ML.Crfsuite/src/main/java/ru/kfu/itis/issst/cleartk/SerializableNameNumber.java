package ru.kfu.itis.issst.cleartk;

import org.cleartk.ml.encoder.features.NameNumber;

import java.io.Serializable;

/**
 * @author Rinat Gareev
 */
public class SerializableNameNumber extends NameNumber implements Serializable {
    public SerializableNameNumber(String name, Number number) {
        super(name, number);
    }
}
