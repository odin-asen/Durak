package com.github.odinasen.durak.business.network.server;

import com.github.odinasen.durak.business.network.client.GameClientTest;
import com.github.odinasen.durak.util.LoggingUtility;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ConcurrentModificationException;
import java.util.logging.Level;

public class GameServerTest {

    private int testPort;
    private GameServer server;

    @Before
    public void setUp() throws Exception {
        server = GameServer.getInstance();
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
        Assert.assertFalse(server.isRunning());

        server.startServer(this.testPort);
        Assert.assertTrue(server.isRunning());
        new ServerSocket(this.testPort);
    }

    @Test
    public void testStartGame() throws Exception {
    }

    @Test
    public void testStopServer() throws Exception {
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
    public void testRemoveAllClients() throws Exception {
    }

    @Test
    public void testIsRunning() throws Exception {
        Assert.assertFalse(this.server.isRunning());
        server.startServer(this.testPort);
        Assert.assertTrue(this.server.isRunning());
    }

    @Test
    public void testSendClientMessage() throws Exception {
    }
}