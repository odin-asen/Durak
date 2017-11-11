package com.github.odinasen.durak.business.network.server.event;

import com.github.odinasen.durak.util.LoggingUtility;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class DurakServiceEventHandler {
    Logger LOGGER = LoggingUtility.getLogger(DurakServiceEventHandler.class);

    /** Map fuer Funktionen, die bestimmten Events zugeordnet sind */
    private Map<DurakServiceEvent.DurakServiceEventType, DurakEventObjectConsumer> eventFunctionMap;

    public DurakServiceEventHandler() {
        /* Initiale Map Groesse ist die Anzahl der vorhandenen Eventtypen */
        eventFunctionMap = new HashMap<>(DurakServiceEvent.DurakServiceEventType.values().length);
    }

    public void registerEventFunction(DurakServiceEvent.DurakServiceEventType type, DurakEventObjectConsumer<?> function) {
        if (type != null) {
            eventFunctionMap.put(type, function);
        }
    }

    public void handleEvent(DurakServiceEvent event) {
        DurakServiceEvent.DurakServiceEventType type = event.getEventType();
        DurakEventObjectConsumer consumer = eventFunctionMap.get(type);
        /* Gibt es ein Objekt fuer die angeforderte Funktion? */
        if (consumer != null) {
            consumer.acceptChecked(event);
            LOGGER.fine("Executed consumer for type " + type);
        } else {
            LOGGER.fine("Could not find consumer for event type " + type);
        }
    }
}
