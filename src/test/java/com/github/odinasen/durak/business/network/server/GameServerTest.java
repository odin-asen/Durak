package com.github.odinasen.durak.business.network.server;

import com.github.odinasen.durak.business.exception.GameServerCode;
import com.github.odinasen.durak.business.exception.SystemException;
import com.github.odinasen.durak.business.network.GameServerTester;
import com.github.odinasen.durak.business.network.client.GameClientTest;
import com.github.odinasen.durak.dto.ClientDto;
import com.github.odinasen.durak.util.LoggingUtility;
import com.github.odinasen.test.UnitTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import static org.junit.Assert.*;

@Category(UnitTest.class)
public class GameServerTest {

    public static final String testClientName = "Horst";
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
            LoggingUtility.getLogger(GameClientTest.class).log(Level.INFO, logMessage.toString(), ex);
        }
        server.setPassword("");
    }

    //TODO auslagern zu Player-Tests
    @Test
    public void playerIdAndClientUUIDAreTheSame() {
        startServerAndAssertRunning();

        ClientDto clientDto = executeAddClientsAndReturnLast(1);

        assertNotNull(clientDto.getUuid());
        assertNotSame(clientDto.getUuid(), "");

        assertEquals(clientDto.getUuid(), getServersPlayerId(0));
    }

    private String getServersPlayerId(int index) {
        return server.getPlayers().get(index).getId();
    }

    private void startServerAndAssertRunning() {
        server.startServer(testPort);
        assertTrue(server.isRunning());
    }

    private ClientDto executeAddClientsAndReturnLast(int amount) {
        ClientDto lastClientAdded = null;
        for (int i = 0; i < amount; i++) {
            lastClientAdded = createClient(testClientName + i);
            server.addClient(lastClientAdded);
        }
        return lastClientAdded;
    }

    private ClientDto createClient(String name) {
        return new ClientDto(UUID.randomUUID().toString(), name);
    }

    @Test
    public void startServerAndStopServerChangeIsRunning() throws Exception {
        assertFalse(server.isRunning());
        startServerAndAssertRunning();
        stopServerAndAssertNotRunning();
    }

    private void stopServerAndAssertNotRunning() {
        server.stopServer();
        assertFalse(server.isRunning());
    }

    @Test(expected = IOException.class)
    public void startServerBlocksPort() throws Exception {
        assertFalse(server.isRunning());

        startServerAndAssertRunning();
        new ServerSocket(testPort);
    }

    @Test
    public void startServerShouldMakePossibleToProperlyUnbindPortWithStopServerMethod() {
        startServerAndAssertRunning();
        try {
            startServerAndAssertRunning();
            fail("Test should not reach this line. Expected SystemException");
        } catch (SystemException ex) {
            server.stopServer();
            assertFalse(server.isRunning());

            assertTestPortCanBeUsedBySocket();

            try {
                startServerAndAssertRunning();
            } catch (SystemException unexpectedException) {
                assertUnexpectedPortUsedException(ex);
            }
        }
    }

    private void assertUnexpectedPortUsedException(SystemException ex) {
        String message =
                "Second start of server should not result in already bound port message " + "after server has been stopped.";
        assertNotSame(message, GameServerCode.PORT_USED, ex.getErrorCode());
    }

    private void assertTestPortCanBeUsedBySocket() {
        try (ServerSocket socket = new ServerSocket(testPort)) {
            socket.close();
        } catch (IOException ex) {
            fail("It should be possible to open a server socket on an unused port.");
        }
    }

    @Test
    public void stopServerShouldUnbindPort() {
        for (int i = 0; i < 5; i++) {
            try {
                startServerAndAssertRunning();
                server.stopServer();
            } catch (SystemException ex) {
                assertUnexpectedPortUsedException(ex);
            }
        }
    }

    @Test
    public void stopServerReleasesSocket() throws Exception {
        assertFalse(server.isRunning());

        startServerAndAssertRunning();

        stopServerAndAssertNotRunning();

        assertTestPortCanBeUsedBySocket();
    }

    @Test
    public void stopServerStopsGame() {
        startServerAndAssertRunning();

        executeAddClientsAndReturnLast(2);
        server.startGame();
        assertTrue(server.gameIsRunning());

        stopServerAndAssertNotRunning();
        assertFalse(server.gameIsRunning());
    }

    @Test
    public void serverDoesNotAddMoreThanSixPlayers() {
        startServerAndAssertRunning();

        ClientDto lastClientAdded = executeAddClientsAndReturnLast(7);

        serverTester.assertServersidePlayerCount(6);

        assertClientAndPlayerHaveNotEmptyAndNotSameId(lastClientAdded, 5);
    }

    private void assertClientAndPlayerHaveNotEmptyAndNotSameId(ClientDto clientDto, int playerIndex) {
        assertNotNull(clientDto.getUuid());
        assertNotSame(clientDto.getUuid(), "");
        assertNotSame(clientDto.getUuid(), getServersPlayerId(playerIndex));
    }

    @Test
    public void cannotStartGameWhenServerIsNotStarted() {
        startGameAndAssertSystemExceptionExpectPlayerProperty(0);
        assertFalse(server.gameIsRunning());
    }

    private void startGameAndAssertSystemExceptionExpectPlayerProperty(int playerValue) {
        try {
            server.startGame();
            fail("Method should throw SystemExcpetion with GameServerCode");
        } catch (SystemException ex) {
            assertEquals(GameServerCode.NOT_ENOUGH_PLAYERS_FOR_GAME, ex.getErrorCode());
            assertEquals(playerValue, ex.get("playerCount"));
        }
    }

    @Test
    public void cannotStartGameWithLessThanTwoPlayers() {
        startServerAndAssertRunning();
        startGameAndAssertSystemExceptionExpectPlayerProperty(0);
        assertFalse(server.gameIsRunning());

        server.addClient(createClient(testClientName));
        startGameAndAssertSystemExceptionExpectPlayerProperty(1);
        assertFalse(server.gameIsRunning());
    }

    @Test
    public void startGameSuccessfulWithTwoPlayers() {
        startServerAndGame(2);
    }

    @Test
    public void startGameSuccessfulWithSixPlayers() {
        startServerAndGame(6);
    }

    private void startServerAndGame(int amountPlayers) {
        startServerAndAssertRunning();

        ClientDto lastClientAdded = executeAddClientsAndReturnLast(amountPlayers);
        server.startGame();
        assertTrue(server.gameIsRunning());
    }

    @Test
    public void runningGameDoesNotAllowToAddPlayers() {
        startServerAndAssertRunning();

        int numberOfPlayers = 3;
        executeAddClientsAndReturnLast(numberOfPlayers);
        server.startGame();
        assertTrue(server.gameIsRunning());

        ClientDto notAddedClient = executeAddClientsAndReturnLast(1);

        assertEquals(numberOfPlayers, server.getPlayers().size());

        int lastPlayerIndex = numberOfPlayers - 1;
        assertClientAndPlayerHaveNotEmptyAndNotSameId(notAddedClient, lastPlayerIndex);
    }

    @Test
    public void stopGameKeepsAllPlayers() {
        int playerAmount = 6;
        startServerAndGame(playerAmount);

        assertEquals(playerAmount, server.getPlayers().size());
        server.stopGame();
        assertEquals(playerAmount, server.getPlayers().size());
    }

    @Test
    public void startServerMultipleTimesMayThrowExeption() {
        startServerAndAssertRunning();
        try {
            server.startServer(testPort);
        } catch (SystemException ex) {
            assertEquals(GameServerCode.PORT_USED, ex.getErrorCode());
            assertEquals(ex.get("port"), testPort);
        }
    }

    @Test
    public void stopServerRemovesAllPlayers() {
        int playerAmount = 6;
        startServerAndGame(playerAmount);

        assertEquals(playerAmount, server.getPlayers().size());
        server.stopServer();
        assertEquals(0, server.getPlayers().size());
    }

    @Test
    public void testRemoveClients() throws Exception {
        startServerAndAssertRunning();

        List<ClientDto> clients = new ArrayList<>(2);
        clients.add(createClient(testClientName + 1));
        clients.add(createClient(testClientName + 2));

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
    public void setPasswordThrowsNoExceptionBeforeStart() {
        server.setPassword("bla");
    }
}