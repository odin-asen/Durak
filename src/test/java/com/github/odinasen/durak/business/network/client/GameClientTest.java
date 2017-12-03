package com.github.odinasen.durak.business.network.client;

import com.github.odinasen.durak.business.exception.GameClientCode;
import com.github.odinasen.durak.business.exception.SystemException;
import com.github.odinasen.durak.business.network.server.GameServer;
import com.github.odinasen.durak.i18n.I18nSupport;
import com.github.odinasen.durak.util.LoggingUtility;
import com.github.odinasen.test.UnitTest;
import com.google.common.base.Stopwatch;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ConcurrentModificationException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static com.github.odinasen.durak.i18n.BundleStrings.USER_MESSAGES;
import static org.junit.Assert.*;

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
    public void reconnect() throws Exception {
        // Hier pruefen, ob die connect bzw. disconnect-Methode
        boolean connected = client.reconnect("localhost", testPort, testClientName, "");
        assertTrue(connected);
        assertTrue(client.isConnected());
    }

    @Test
    public void reconnectAlreadyConnectedClient() throws Exception {
        client.connect("localhost", testPort, testClientName, "");
        assertTrue(client.isConnected());

        Stopwatch watch = Stopwatch.createStarted();
        boolean connected = client.reconnect("localhost", testPort, testClientName, "");
        watch.stop();
        assertTrue(connected);

        /* Method should take a break of at least one second. */
        assertTrue(1000L < watch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    public void disconnect() throws Exception {
        boolean connected = client.connect("localhost", testPort, testClientName, "");
        assertTrue(connected);

        client.disconnect();
        Assert.assertFalse(client.isConnected());
    }

    /**
     * Die closed-Methode soll nur den SimonClient trennen.
     */
    @Test
    public void closed() throws Exception {
        boolean connected = client.connect("localhost", testPort, testClientName, "");
        assertTrue(connected);

        client.closed();
        Assert.assertFalse(client.isConnected());
    }

    @Test
    public void connectTwiceThrowsException() {
        String serverAddress = "127.0.0.1";
        boolean connected = client.connect(serverAddress, testPort, testClientName, "");
        assertTrue(connected);

        try {
            client.connect("localhost", testPort, testClientName, "");
            fail("Code should not reach this point");
        } catch (SystemException ex) {
            assertEquals(GameClientCode.ALREADY_CONNECTED, ex.getErrorCode());
            assertEquals(serverAddress, ex.get("server"));
            assertEquals(testPort, ex.get("port"));
        }
    }

    @Test
    public void getSocketAddressCanReturnUserMessage() {
        String expected = I18nSupport.getValue(USER_MESSAGES, "no.address");
        assertEquals(expected, client.getSocketAddress());
    }

    @Test
    public void connectThrowsSystemExceptionForUnknownHost() {
        String unknownAddress = "bjikajhswielf";
        try {
            client.connect(unknownAddress, testPort, testClientName, "");
            fail("Must not reach this line");
        } catch (SystemException ex) {
            assertEquals(GameClientCode.SERVER_NOT_FOUND, ex.getErrorCode());
            assertEquals(0, ex.getProperties().size());
        }
    }
}