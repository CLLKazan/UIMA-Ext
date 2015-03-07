package ru.kfu.itis.issst.cleartk;

import org.cleartk.ml.encoder.features.NameNumber;

import java.io.Serializable;

/**
 * @author Rinat Gareev
 */
public class SerializableNameNumber implements Serializable {

    public SerializableNameNumber(String name, Number number) {
        this.name = name;
        this.number = number;
    }

    public String name;

    public Number number;
}
