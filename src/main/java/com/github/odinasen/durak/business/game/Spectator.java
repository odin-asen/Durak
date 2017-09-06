package com.github.odinasen.durak.business.game;

import com.github.odinasen.durak.dto.ClientDto;

/**
 * POJO fuer einen Zuschauer
 * Author: Timm Herrmann
 * Date: 14.12.2016.
 */
public class Spectator extends Client {

    public Spectator(ClientDto client) {
        super(client);
        client.setIsSpectator(true);
    }
}
