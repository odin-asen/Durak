package com.github.odinasen.durak.business.network;

import com.github.odinasen.durak.business.network.server.GameServer;

import static org.junit.Assert.assertEquals;

public class GameServerTester {

    GameServer server;

    public GameServerTester(GameServer server) {
        this.server = server;
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
}
