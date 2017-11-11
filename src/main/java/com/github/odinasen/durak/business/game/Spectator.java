package com.github.odinasen.durak.business.game;

import com.github.odinasen.durak.dto.ClientDto;

public class Spectator extends Client {

    public Spectator(ClientDto client) {
        super(client);
        client.setIsSpectator(true);
    }
}
