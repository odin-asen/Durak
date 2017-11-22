package com.github.odinasen.durak.business.network.server;

import com.github.odinasen.durak.business.network.GameServerTester;
import com.github.odinasen.durak.business.network.client.GameClientTest;
import com.github.odinasen.durak.dto.ClientDto;
import com.github.odinasen.durak.util.LoggingUtility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import static org.junit.Assert.*;

public class GameServerTest {

    private int testPort;
    private GameServer server;
    private GameServerTester serverTester;

    @Before
    public void setUp() throws Exception {
        server = GameServer.getInstance();
        serverTester = new GameServerTester(server);
        testPort = 10000;
    }

    @After
    public void tearDown() throws Exception {
        try {
            server.stopServer();
        } catch (ConcurrentModificationException ex) {
            StringBuilder logMessage = new StringBuilder(256);
            logMessage.append("Die Exception wird geworfen, weil irgendetwas nicht in SIMON ")
                      .append("stimmt in der Registry.stop Methode. Das ist hier aber nicht ")
                      .append("relevant und auch nicht schlimm, da der Server ja sowieso die ")
                      .append("Verbindung zu den Clients trennt.");
            LoggingUtility.getLogger(GameClientTest.class)
                          .log(Level.INFO, logMessage.toString(), ex);
        }
        server.setPassword("");
    }

    /**
     * Ablauf:
     * Server laeuft nicht.
     * Server starten.
     * Server laueft. Port ist belegt.
     */
    @Test(expected = IOException.class)
    public void testStartServer() throws Exception {
        assertFalse(server.isRunning());

        server.startServer(testPort);
        assertTrue(server.isRunning());
        new ServerSocket(testPort);
    }

    @Test
    public void testStartGame() throws Exception {
    }

    @Test
    public void testStopServer() throws Exception {
        assertFalse(server.isRunning());

        server.startServer(testPort);
        assertTrue(server.isRunning());

        server.stopServer();
        assertFalse(server.isRunning());

        try (ServerSocket socket = new ServerSocket(testPort)) {
            socket.close();
        } catch (IOException ex) {
            fail("It should be possible to open a server socket on an unused port.");
        }
    }

    @Test
    public void testStopGame() throws Exception {
    }

    @Test
    public void testRemoveAllSpectators() throws Exception {
    }

    @Test
    public void testRemoveAllPlayers() throws Exception {
    }

    @Test
    public void removeClients() throws Exception {
        server.startServer(testPort);

        List<ClientDto> clients = new ArrayList<>(2);
        clients.add(createClient("horst1"));
        clients.add(createClient("horst2"));

        for (ClientDto client : clients) {
            server.addClient(client);
        }

        serverTester.assertServersidePlayerCount(2);
        serverTester.assertServerHasZeroSpectators();

        server.removeClients(clients);

        serverTester.assertServersidePlayerCount(0);
        serverTester.assertServerHasZeroSpectators();
    }

    @Test
    public void testIsRunning() throws Exception {
        assertFalse(server.isRunning());
        server.startServer(testPort);
        assertTrue(server.isRunning());
    }

    @Test
    public void testSendClientMessage() throws Exception {
    }

    private ClientDto createClient(String name) {
        return new ClientDto(UUID.randomUUID().toString(), name);
    }
}