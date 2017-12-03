package com.github.odinasen.durak.business.network;

import com.github.odinasen.durak.business.network.client.GameClient;
import com.github.odinasen.durak.business.network.client.GameClientTest;
import com.github.odinasen.durak.business.network.server.GameServer;
import com.github.odinasen.durak.util.LoggingUtility;
import com.github.odinasen.test.IntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ConcurrentModificationException;
import java.util.logging.Level;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(IntegrationTest.class)
public class ClientServerConnectionTest {
    private static final String SERVER_PWD = "bla";

    private int testPort;
    private GameServer server;
    private GameServerTester serverTester;
    private GameClient client;
    private String testClientName = "Horst";

    @Before
    public void setUp() throws Exception {
        server = GameServer.getInstance();
        serverTester = new GameServerTester(server, testClientName);
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
        boolean connected = client.connect("localhost", testPort, testClientName, "");

        assertTrue(connected);
    }

    /**
     * Test fuer eine Verbindung, ohne Passwort.
     */
    @Test
    public void connectWithPassword() throws Exception {
        server.setPassword(SERVER_PWD);

        // Verbindung ohne Passwort soll nicht gelingen
        boolean connected = client.connect("localhost", testPort, testClientName, "");
        assertFalse(connected);

        // Verbindung mit richtigem Passwort soll gelingen
        connected = client.connect("localhost", testPort, testClientName, SERVER_PWD);
        assertTrue(connected);
    }

    @Test
    public void stopServerWithConnectedClients() throws Exception {
        boolean connected = client.connect("localhost", testPort, testClientName, "");
        assertTrue(connected);

        serverTester.assertServersidePlayerCount(1);

        server.stopServer();

        serverTester.assertServersidePlayerCount(0);
    }

    @Test
    public void logoutClientAndReconnect() {
        boolean connected = client.connect("localhost", testPort, testClientName, "");
        assertTrue(connected);

        serverTester.assertServersidePlayerCount(1);

        client.disconnect();
        assertFalse(client.isConnected());

        assertTrue(server.isRunning());
        serverTester.assertServersidePlayerCount(0);

        connected = client.reconnect("localhost", testPort, testClientName, "");
        assertTrue(connected);

        serverTester.assertServersidePlayerCount(1);
    }
}
