package com.github.odinasen.durak.business.network.server;

import com.github.odinasen.durak.business.game.Player;
import com.github.odinasen.durak.dto.ClientDto;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class PlayerTest {

    @Test
    public void playerIdAndClientUUIDAreTheSame() {
        String uuidStr = UUID.randomUUID().toString();
        String clientName = "Horst";
        ClientDto clientDto = new ClientDto(uuidStr, clientName);

        Player testPlayer = new Player(clientDto);
        assertEquals(clientDto.getUuid(), testPlayer.getId());
        assertEquals(clientDto.getName(), testPlayer.getName());
    }
}
