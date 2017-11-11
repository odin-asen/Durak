package com.github.odinasen.durak.business.game;

import com.github.odinasen.durak.dto.ClientDto;

public class Player extends Client {
    public Player(ClientDto client) {
        super(client);
        client.setIsSpectator(false);
    }
}
