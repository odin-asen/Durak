package com.github.odinasen.durak.business.network;

import com.github.odinasen.durak.util.Assert;

/**
 * Klasse zur Kommunikation zwischen Client und Server.
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 19.09.14
 */
public class NetworkMessage<T extends Enum<?>> {
    private Object messageObject;
    private T messageType;

    /**
     * Erstellt ein Networkmessage-Objekt. Kein Parameter darf null sein.
     * @param messageObject
     * @param messageType
     */
    public NetworkMessage(Object messageObject, T messageType) {
        Assert.assertNotNull(messageObject);
        Assert.assertNotNull(messageType, Enum.class);
        //===================================================

        this.messageObject = messageObject;
        this.messageType = messageType;
    }

    public Class<?> getMessageTypeClass() {
        return this.messageType.getClass();
    }

    public T getMessageType() {
        return this.messageType;
    }

    public Object getMessageObject() {
        return messageObject;
    }

    public void setMessageObject(Object messageObject) {
        this.messageObject = messageObject;
    }
}
