package ru.kfu.itis.cll.uima.util;

import java.io.Closeable;

/**
 * @author Rinat Gareev
 */
public interface ResourceTicket extends Closeable {
    @Override
    void close();
}
