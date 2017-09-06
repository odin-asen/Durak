package com.github.odinasen.durak.business.game;

import com.github.odinasen.durak.dto.ClientDto;

/**
 * <p/>
 * Author: tih<br/>
 * Date: 22.07.2017.
 */
public class Client {
    protected ClientDto client;

    public Client(ClientDto client) {
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
