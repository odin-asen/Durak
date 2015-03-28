package com.github.odinasen.durak.business.network;

import com.github.odinasen.durak.business.network.server.GameServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;

public class GameServerTest {

  GameServer server;
  @Before
  public void setUp() throws Exception {
    server = GameServer.getInstance();
  }

  @After
  public void tearDown() throws Exception {
    server.stopServer();
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
    server.startServer();
    Assert.assertTrue(server.isRunning());
    new ServerSocket(server.getPort().getValue());
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
    Assert.assertFalse(server.isRunning());
    server.startServer();
    Assert.assertTrue(server.isRunning());
  }

  @Test
  public void testSendClientMessage() throws Exception {
  }
}