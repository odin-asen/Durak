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
    public static final String SERVER_PWD = "bla";
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
        String clientName = "Horst";
        ClientDto clientDto = createNewClientDto(clientName);

        boolean connected = this.client.connect("localhost", this.testPort, "", clientDto);
        Assert.assertFalse(connected);

        connected = this.client.connect("localhost", this.testPort, SERVER_PWD, clientDto);
        Assert.assertTrue(connected);
    }

    /**
     * Testet die Wiederverbindung. Ist der Client verbunden
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
     * Die closed-Methode soll nur den Client trennen.
     */
    @Test
    public void closed() throws Exception {
        String clientName = "Horst";
        ClientDto clientDto = createNewClientDto(clientName);

        // Hier pruefen, ob die connect bzw. disconnect-Methode
        boolean connected = this.client.connect("localhost", this.testPort, "", clientDto);
        Assert.assertTrue(connected);

        this.client.closed();
        Assert.assertFalse(this.client.isConnected());
    }

    /**
     * Teste, ob die Socketadresse in unterschiedlichen Zustaenden (Verbunden, Getrennt) die richtige Meldung zurueck
     * gibt.
     */
    @Test
    public void getSocketAddress() throws Exception {
        String host = "localhost";
        String messageGetrennt = "Keine Adresse verfügbar!";
        Assert.assertSame("Verbindung getrennt - Socketadresse falsch",
                          messageGetrennt,
                          this.client.getSocketAddress());

        String socketAddress = host + ":" + this.testPort;
        boolean connected = this.client.connect(host, this.testPort, "", null);
        Assert.assertTrue(connected);
        Assert.assertSame("Verbindung besteht - Socketadresse falsch", socketAddress, this.client.getSocketAddress());
    }

    private ClientDto createNewClientDto(String clientName) {
        UUID clientID = UUID.randomUUID();
        return new ClientDto(clientID.toString(), clientName);
    }
}