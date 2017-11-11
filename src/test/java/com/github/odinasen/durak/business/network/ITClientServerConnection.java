package com.github.odinasen.durak.business.network;

import com.github.odinasen.durak.business.network.client.GameClient;
import com.github.odinasen.durak.business.network.server.GameServer;
import com.github.odinasen.durak.dto.ClientDto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

/**
 * Integrationstests fuer SimonClient-Server-Verbindungen.
 * <p/>
 * Author: tih<br/>
 * Date: 04.12.2016.
 */
public class ITClientServerConnection {
    private static final String SERVER_PWD = "bla";

    private int testPort;
    private GameServer server;

    /**
     * Objekt der zu testenden Klasse
     */
    private GameClient client;

    @Before
    public void setUp() throws Exception {
        this.server = GameServer.getInstance();
        this.testPort = 10000;
        this.server.startServer(this.testPort);
        Assert.assertTrue(this.server.isRunning());

        this.client = GameClient.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        server.stopServer();
        server.setPassword("");
    }

    /**
     * Test fuer eine Verbindung, ohne Passwort.
     */
    @Test
    public void connectWithoutPassword() throws Exception {
        String clientName = "Horst";
        ClientDto clientDto = createNewClientDto(clientName);
        boolean connected = this.client.connect("localhost", this.testPort, "", clientDto);

        Assert.assertTrue(connected);
    }

    /**
     * Test fuer eine Verbindung, ohne Passwort.
     */
    @Test
    public void connectWithPassword() throws Exception {
        this.server.setPassword(SERVER_PWD);

        String clientName = "Horst";
        ClientDto clientDto = createNewClientDto(clientName);

        // Verbindung ohne Passwort soll nicht gelingen
        boolean connected = this.client.connect("localhost", this.testPort, "", clientDto);
        Assert.assertFalse(connected);

        // Verbindung mit richtigem Passwort soll gelingen
        connected = this.client.connect("localhost", this.testPort, SERVER_PWD, clientDto);
        Assert.assertTrue(connected);
    }

    @Test
    public void disconnectServer() throws Exception {
        boolean connected = this.client.connect("localhost", this.testPort, "",
                                                createNewClientDto("Horst"));
        Assert.assertTrue(connected);

        int playerCount = this.server.getPlayers().size();
        Assert.assertEquals(1, playerCount);

        this.server.stopServer();
        playerCount = this.server.getPlayers().size();
        Assert.assertEquals(0, playerCount);
        int spectatorCount = this.server.getSpectators().size();
        Assert.assertEquals(0, spectatorCount);
    }

    private ClientDto createNewClientDto(String clientName) {
        UUID clientID = UUID.randomUUID();
        return new ClientDto(clientID.toString(), clientName);
    }
}
