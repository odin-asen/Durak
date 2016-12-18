package com.github.odinasen.durak.business.network.server.event;

/**
 * Event Enumerations fuer {@link DurakServiceEvent}
 * <p/>
 * Author: Timm Herrmann
 * Date: 18.12.2016
 */
public class DurakServiceEvent
        implements ServerEvent {

    public enum DurakServiceEventType {
        /**
         * Ein Client hat sich angemeldet.
         * Event-Objekt = Client-Objekt
         */
        CLIENT_LOGIN,
        /**
         * Ein Client hat sich abgemeldet.
         * Event-Objekt = Liste mit UUID von abgemeldeten Clients
         */
        CLIENT_LOGOUT
    }

    private DurakServiceEventType eventType;
    private Object eventObject;

    public DurakServiceEvent(DurakServiceEventType eventType, Object eventObject) {
        this.eventType = eventType;
        this.eventObject = eventObject;
    }

    @Override
    public DurakServiceEventType getEventType() {
        return this.eventType;
    }

    @Override
    public Object getEventObject() {
        return this.eventObject;
    }
}
