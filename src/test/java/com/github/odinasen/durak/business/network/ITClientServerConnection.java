package com.github.odinasen.durak.business.network;

import com.github.odinasen.durak.business.network.client.GameClient;
import com.github.odinasen.durak.business.network.client.GameClientTest;
import com.github.odinasen.durak.business.network.server.GameServer;
import com.github.odinasen.durak.dto.ClientDto;
import com.github.odinasen.durak.util.LoggingUtility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ConcurrentModificationException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Integrationstests fuer SimonClient-Server-Verbindungen.
 * <p/>
 * Author: tih<br/>
 * Date: 04.12.2016.
 */
public class ITClientServerConnection {
    private static final String SERVER_PWD = "bla";
    private static final Logger LOGGER = LoggingUtility.getLogger(ITClientServerConnection.class);

    private int testPort;
    private GameServer server;
    private GameServerTester serverTester;
    private GameClient client;

    @Before
    public void setUp() throws Exception {
        server = GameServer.getInstance();
        serverTester = new GameServerTester(server);
        testPort = 10000;

        server.startServer(testPort);
        assertTrue(server.isRunning());

        client = GameClient.getInstance();
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

    @Test
    public void connectWithoutPassword() throws Exception {
        String clientName = "Horst";
        ClientDto clientDto = createNewClientDto(clientName);
        boolean connected = client.connect("localhost", testPort, "", clientDto);

        assertTrue(connected);
    }

    private ClientDto createNewClientDto(String clientName) {
        UUID clientID = UUID.randomUUID();
        return new ClientDto(clientID.toString(), clientName);
    }

    /**
     * Test fuer eine Verbindung, ohne Passwort.
     */
    @Test
    public void connectWithPassword() throws Exception {
        server.setPassword(SERVER_PWD);

        String clientName = "Horst";
        ClientDto clientDto = createNewClientDto(clientName);

        // Verbindung ohne Passwort soll nicht gelingen
        boolean connected = client.connect("localhost", testPort, "", clientDto);
        assertFalse(connected);

        // Verbindung mit richtigem Passwort soll gelingen
        connected = client.connect("localhost", testPort, SERVER_PWD, clientDto);
        assertTrue(connected);
    }

    @Test
    public void stopServerWithConnectedClients() throws Exception {
        boolean connected = client.connect("localhost", testPort, "", createNewClientDto("Horst"));
        assertTrue(connected);

        serverTester.assertServersidePlayerCount(1);
        serverTester.assertServerHasZeroSpectators();

        server.stopServer();

        serverTester.assertServersidePlayerCount(0);
        serverTester.assertServerHasZeroSpectators();
    }

    @Test
    public void logoutClientAndReconnect() {
        boolean connected = client.connect("localhost", testPort, "", createNewClientDto("Horst"));
        assertTrue(connected);

        serverTester.assertServersidePlayerCount(1);
        serverTester.assertServerHasZeroSpectators();

        client.disconnect();
        assertFalse(client.isConnected());

        assertTrue(server.isRunning());
        serverTester.assertServersidePlayerCount(0);
        serverTester.assertServerHasZeroSpectators();

        connected = client.reconnect("localhost", testPort, "", createNewClientDto("Horst"));
        assertTrue(connected);

        serverTester.assertServersidePlayerCount(1);
        serverTester.assertServerHasZeroSpectators();
    }
}
