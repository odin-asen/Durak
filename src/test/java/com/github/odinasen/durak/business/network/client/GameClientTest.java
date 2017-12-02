package com.github.odinasen.durak.business.network.client;

import com.github.odinasen.durak.business.network.server.GameServer;
import com.github.odinasen.durak.dto.ClientDto;
import com.github.odinasen.durak.util.LoggingUtility;
import com.github.odinasen.test.UnitTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ConcurrentModificationException;
import java.util.UUID;
import java.util.logging.Level;

@Category(UnitTest.class)
public class GameClientTest {

    private int testPort;
    private GameServer server;

    private GameClient client;
    private String testClientName = "Horst";

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
    public void reconnect() throws Exception {
        // Hier pruefen, ob die connect bzw. disconnect-Methode
        boolean connected = client.reconnect("localhost", testPort, testClientName, "");
        Assert.assertTrue(connected);
        Assert.assertTrue(client.isConnected());
    }

    @Test
    public void disconnect() throws Exception {
        // Hier pruefen, ob die connect bzw. disconnect-Methode
        boolean connected = client.connect("localhost", testPort, testClientName, "");
        Assert.assertTrue(connected);

        client.disconnect();
        Assert.assertFalse(client.isConnected());
    }

    /**
     * Die closed-Methode soll nur den SimonClient trennen.
     */
    @Test
    public void closed() throws Exception {
        boolean connected = client.connect("localhost", testPort, testClientName, "");
        Assert.assertTrue(connected);

        client.closed();
        Assert.assertFalse(client.isConnected());
    }

    private ClientDto createNewClientDto(String clientName) {
        UUID clientID = UUID.randomUUID();
        return new ClientDto(clientID.toString(), clientName);
    }
}