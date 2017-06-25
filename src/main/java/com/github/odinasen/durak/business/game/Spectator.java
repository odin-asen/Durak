package com.github.odinasen.durak.business.game;

import com.github.odinasen.durak.dto.ClientDto;

/**
 * POJO fuer einen Zuschauer
 * Author: Timm Herrmann
 * Date: 14.12.2016.
 */
public class Spectator {
    private ClientDto client;
    public Spectator(ClientDto client) {
        this.client = client;
    }

    /**
     * @return Den Namen des Zuschauers.
     */
    public String getName() {
        if (client != null) {
            return client.getName();
        } else {
            return "";
        }
    }

    /**
     * @return Die UUID des uebergebenen Clients.
     */
    public String getID() {
        if (client != null) {
            return client.getUuid();
        } else {
            return "";
        }
    }
}
