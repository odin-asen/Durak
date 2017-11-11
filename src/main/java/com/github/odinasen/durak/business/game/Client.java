package com.github.odinasen.durak.business.game;

import com.github.odinasen.durak.dto.ClientDto;

/**
 * <p/>
 * Author: tih<br/>
 * Date: 22.07.2017.
 */
public class Client {
    private ClientDto client;

    public Client(ClientDto client) {
        this.client = client;
    }

    public String getName() {
        if (client != null) {
            return client.getName();
        } else {
            return "";
        }
    }

    public String getId() {
        if (client != null) {
            return client.getUuid();
        } else {
            return "";
        }
    }

    public boolean hasSameIdAs(ClientDto otherClient) {
        return otherClient != null && otherClient.getUuid().equals(this.getId());
    }
}
