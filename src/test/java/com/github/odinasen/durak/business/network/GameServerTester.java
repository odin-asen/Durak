package com.github.odinasen.durak.business.network;

import com.github.odinasen.durak.business.network.server.GameServer;
import com.github.odinasen.durak.dto.ClientDto;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class GameServerTester {

    GameServer server;

    ClientDto lastCreatedClient;
    String testClientName;

    public GameServerTester(GameServer server, String testClientName) {
        this.server = server;
        this.testClientName = testClientName;
    }

    public void assertServersidePlayerCount(int expectedCount) {
        String messageFormat = "Anzahl der Spieler im Server sollte %d statt %d sein.";

        int actualPlayers = server.getPlayers().size();
        String message = String.format(messageFormat, expectedCount, actualPlayers);
        assertEquals(message, expectedCount, actualPlayers);
    }

    public void assertServerHasZeroSpectators() {
        int actualCount = server.getSpectators().size();
        String message = "Server darf keine Zuschauer haben.";
        assertEquals(message, 0, actualCount);
    }

    public void addClientsToServer(int amount) {
        ClientDto lastClientAdded = null;
        for (int i = 0; i < amount; i++) {
            lastCreatedClient = new ClientDto(UUID.randomUUID().toString(), testClientName + i);
            server.addClient(lastCreatedClient);
        }
    }

    public ClientDto getLastCreatedClient() {
        return lastCreatedClient;
    }
}
