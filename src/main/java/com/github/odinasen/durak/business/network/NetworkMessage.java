package com.github.odinasen.durak.business.network;

import com.github.odinasen.durak.util.Assert;

/**
 * Klasse zur Kommunikation zwischen Client und Server.
 */
public class NetworkMessage<T extends Enum<?>> {
    private Object messageObject;
    private T messageType;

    /**
     * Kein Parameter darf null sein.
     */
    public NetworkMessage(Object messageObject, T messageType) {
        Assert.assertNotNull(messageObject);
        Assert.assertNotNull(messageType);

        this.messageObject = messageObject;
        this.messageType = messageType;
    }

    public Class<?> getMessageTypeClass() {
        return messageType.getClass();
    }

    public T getMessageType() {
        return messageType;
    }

    public Object getMessageObject() {
        return messageObject;
    }

    public void setMessageObject(Object messageObject) {
        this.messageObject = messageObject;
    }
}
