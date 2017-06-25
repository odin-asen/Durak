package com.github.odinasen.durak.business.network.server.event;

/**
 * Interface fuer Events, die an den Server gesendet werden.
 * <p/>
 * Author: Timm Herrmann
 * Date: 18.12.2016
 */
public interface ServerEvent<T> {
    /**
     * @return Liefert den Event-Typ.
     */
    Enum getEventType();

    /**
     * @return Liefert das Event-Objekt.
     */
    T getEventObject();
}
