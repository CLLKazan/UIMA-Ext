package ru.kfu.itis.cll.uima.util;

/**
 * @author Rinat Gareev
 */
public interface ResourceTicketProducer {
    /**
     * Notify this resource about a new client.
     */
    ResourceTicket acquire();
}
