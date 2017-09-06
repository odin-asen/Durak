package com.github.odinasen.durak.business.game;

import com.github.odinasen.durak.dto.ClientDto;

/**
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 06.01.14
 */
public class Player extends Client {
    private ClientDto client;
    public Player(ClientDto client) {
        super(client);
        client.setIsSpectator(false);
    }
}
