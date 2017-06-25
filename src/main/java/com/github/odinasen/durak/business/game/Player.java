package com.github.odinasen.durak.business.game;

import com.github.odinasen.durak.dto.ClientDto;

/**
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 06.01.14
 */
public class Player {
    private ClientDto client;
    public Player(ClientDto client) {
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
