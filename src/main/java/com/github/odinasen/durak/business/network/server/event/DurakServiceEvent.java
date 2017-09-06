package com.github.odinasen.durak.business.network.server.event;

import com.github.odinasen.durak.business.network.server.ServerService;

/**
 * Event Enumerations fuer {@link ServerService}
 * <p/>
 * Author: Timm Herrmann
 * Date: 18.12.2016
 */
public class DurakServiceEvent<T>
        implements ServerEvent<T> {

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
    private T eventObject;

    public DurakServiceEvent(DurakServiceEventType eventType, T eventObject) {
        this.eventType = eventType;
        this.eventObject = eventObject;
    }

    @Override
    public DurakServiceEventType getEventType() {
        return this.eventType;
    }

    @Override
    public T getEventObject() {
        return this.eventObject;
    }
}
