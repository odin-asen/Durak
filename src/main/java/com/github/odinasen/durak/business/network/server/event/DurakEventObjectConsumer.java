package com.github.odinasen.durak.business.network.server.event;

import com.github.odinasen.durak.business.network.server.GameServer;
import com.github.odinasen.durak.util.LoggingUtility;

import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Eine Klasse, die die Funktion von Consumer&lt;DurakServiceEvent&gt; klont und dabei das Objekt
 * des Events prueft.
 * <p/>
 * Author: Timm Herrmann
 * Date: 26.05.2017
 */
abstract public class DurakEventObjectConsumer<T> implements Consumer<DurakServiceEvent<T>> {
    private static final Logger LOGGER = LoggingUtility.getLogger(GameServer.class.getName());

    private Class<T> checkClass;
    public DurakEventObjectConsumer(Class<T> checkClass) {
        assert checkClass != null;
        this.checkClass = checkClass;
    }

    public void acceptChecked(DurakServiceEvent event) {
        Object eventObject = event.getEventObject();
        if (this.checkClass.isInstance(eventObject)) {
            this.accept(event);
        } else {
            if (eventObject != null) {
                LOGGER.severe(
                        "Internal mismatch of event object type. Expected " + checkClass
                        + " but found " + eventObject.getClass());
            } else {
                LOGGER.severe("Sent log in event without user information.");
            }
        }
    }
}
