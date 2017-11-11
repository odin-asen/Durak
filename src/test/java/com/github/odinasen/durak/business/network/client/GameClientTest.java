package com.github.odinasen.durak.business.network.client;

import com.github.odinasen.durak.business.network.server.GameServer;
import com.github.odinasen.durak.dto.ClientDto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

/**
 * Testklasse fuer den GameClient.
 * Created by tih on 23.09.2016.
 */
public class GameClientTest {

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
     * Testet die Wiederverbindung. Ist der SimonClient verbunden
     */
    @Test
    public void reconnect() throws Exception {
        String clientName = "Horst";
        ClientDto clientDto = createNewClientDto(clientName);

        // Hier pruefen, ob die connect bzw. disconnect-Methode
        boolean connected = this.client.reconnect("localhost", this.testPort, "", clientDto);
        Assert.assertTrue(connected);
        Assert.assertTrue(this.client.isConnected());
    }

    @Test
    public void disconnect() throws Exception {
        String clientName = "Horst";
        ClientDto clientDto = createNewClientDto(clientName);

        // Hier pruefen, ob die connect bzw. disconnect-Methode
        boolean connected = this.client.connect("localhost", this.testPort, "", clientDto);
        Assert.assertTrue(connected);

        this.client.disconnect();
        Assert.assertFalse(this.client.isConnected());
    }

    /**
     * Die closed-Methode soll nur den SimonClient trennen.
     */
    @Test
    public void closed() throws Exception {
        String clientName = "Horst";
        ClientDto clientDto = createNewClientDto(clientName);

        boolean connected = this.client.connect("localhost", this.testPort, "", clientDto);
        Assert.assertTrue(connected);

        this.client.closed();
        Assert.assertFalse(this.client.isConnected());
    }

    private ClientDto createNewClientDto(String clientName) {
        UUID clientID = UUID.randomUUID();
        return new ClientDto(clientID.toString(), clientName);
    }
}