package com.github.odinasen.durak.business.network;

import com.github.odinasen.durak.business.network.client.GameClient;
import com.github.odinasen.durak.business.network.client.GameClientTest;
import com.github.odinasen.durak.business.network.server.GameServer;
import com.github.odinasen.durak.dto.ClientDto;
import com.github.odinasen.durak.util.LoggingUtility;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ConcurrentModificationException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    /**
     * Objekt der zu testenden Klasse
     */
    private GameClient client;

    @Before
    public void setUp() throws Exception {
        server = GameServer.getInstance();
        testPort = 10000;
        server.startServer(testPort);
        Assert.assertTrue(server.isRunning());

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

        Assert.assertTrue(connected);
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
        Assert.assertFalse(connected);

        // Verbindung mit richtigem Passwort soll gelingen
        connected = client.connect("localhost", testPort, SERVER_PWD, clientDto);
        Assert.assertTrue(connected);
    }

    @Test
    public void disconnectServer() throws Exception {
        boolean connected = client.connect("localhost", testPort, "", createNewClientDto("Horst"));
        Assert.assertTrue(connected);

        int playerCount = server.getPlayers().size();
        Assert.assertEquals(1, playerCount);

        server.stopServer();
        playerCount = server.getPlayers().size();
        Assert.assertEquals(0, playerCount);
        int spectatorCount = server.getSpectators().size();
        Assert.assertEquals(0, spectatorCount);
    }

    private ClientDto createNewClientDto(String clientName) {
        UUID clientID = UUID.randomUUID();
        return new ClientDto(clientID.toString(), clientName);
    }
}
