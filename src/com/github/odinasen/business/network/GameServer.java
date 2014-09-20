package com.github.odinasen.business.network;

import com.github.odinasen.Assert;
import com.github.odinasen.LoggingUtility;
import com.github.odinasen.i18n.I18nSupport;
import de.root1.simon.Registry;
import de.root1.simon.Simon;
import de.root1.simon.exceptions.NameBindingException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import static com.github.odinasen.i18n.BundleStrings.USER_MESSAGES;

/**
 * Das ist der Spieleserver. Er verwaltet ein laufendes Spiel und wertet Aktionen aus, um Sie dann
 * allen Spielern zu kommunizieren.<br/>
 * Methoden werden über ein Singleton-Objekt aufgerufen.<br/>
 * Standardmaessig laueft der Server auf Port 10000.
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 21.06.14
 */
public class GameServer {
  private static final Logger LOGGER = LoggingUtility.getLogger(GameServer.class.getName());

  /**
   * Objekt, das alle Service-Methoden fuer den Durakserver enthaelt.
   */
  private DurakServerService serverService;

  /**
   * Ist das Singleton-Objekt der Klasse.
   */
  private static GameServer instance;

  /**
   * Ist das Registry-Objekt fuer die SIMON-Verbindung.
   */
  private Registry registry;

  /**
   * Ist der Port auf dem der Server laueft.
   */
  private int port;

  /**
   * Ist das Passwort des Servers.
   */
  private String password;

  /****************/
  /* Constructors */

  private GameServer() {
    this.password = "";
    this.port = 10000;
  }

  public static GameServer getInstance() {
    if (instance == null) {
      instance = new GameServer();
    }

    return instance;
  }
  /*     End      */
  /****************/

  /***********/
  /* Methods */

  /**
   * Startet den Server. Kann der Server aus einem Grund nicht gestartet werden, wird eine
   * {@link GameServerException} geworfen.
   * @throws GameServerException
   *    <ol> als <b>praesentierbare Nachricht</b> wenn,
   *      <li>der Service schon einmal registriert wurde, also die Methode schon einmal ausgefuehrt
   *      wurde, ohne {@link #stopServer()} aufzurufen.</li>
   *      <li>die IP-Adresse des Servers nicht gefunden werden konnte.</li>
   *      <li>es ein Problem mit dem Netzwerklayer gibt.</li>
   *    </ol>
   */
  public void startServer()
    throws GameServerException {
    serverService = new DurakServerService();

    try {
      this.registry = Simon.createRegistry(port);
      this.registry.bind(SIMONConfiguration.REGISTRY_NAME_SERVER, this.serverService);

      LoggingUtility.embedInfo(LOGGER, LoggingUtility.STARS, "Server is running");
    } catch (NameBindingException e) {
      LOGGER.warning("Name \"" + SIMONConfiguration.REGISTRY_NAME_SERVER + "\"already bound: " + e.getMessage());
      throw new GameServerException(I18nSupport.getValue(USER_MESSAGES, "service.already.running"));
    } catch (UnknownHostException e) {
      LOGGER.warning("Could not find ip address: "+e.getMessage());
      throw new GameServerException(I18nSupport.getValue(USER_MESSAGES, "network.error"));
    } catch (IOException e) {
      LOGGER.warning("I/O exception: " + e.getMessage());
      throw new GameServerException(I18nSupport.getValue(USER_MESSAGES, "port.might.be.used"));
    }
  }

  /**
   * Startet das Spiel.
   */
  public void startGame() {

  }

  /**
   * Stoppt den laufenden Server. Laueft ein Spiel, wird dieses auch geschlossen.
   */
  public void stopServer() {
    try {
      this.removeAllClients();
    } catch (GameServerException e) {
      final String message =
          "Attempt of stopping non running server caused exception with following message\n"
          + e.getMessage();
      LOGGER.info(message);
    }
    this.registry.unbind(SIMONConfiguration.REGISTRY_NAME_SERVER);
    this.registry.stop();
    LOGGER.info(LoggingUtility.STARS+" Server shut down "+LoggingUtility.STARS);
  }

  /**
   * Stoppt ein laufendes Spiel.
   */
  public void stopGame() {

  }

  /**
   * Trennt alle beobachtenden Clients vom Server und entfernt Sie aus der entsprechenden Liste.
   * @return
   *    Die Anzahl der Clients, die entfernt wurden.
   */
  public int removeAllSpectators() {
    return 0;
  }

  /**
   * Trennt alle spielenden Clients vom Server und entfernt Sie aus der entsprechenden Liste.
   * @return
   *    Die Anzahl der Clients, die entfernt wurden.
   */
  public int removeAllPlayers() throws GameServerException {
    return 0;
  }

  /**
   * Trennt alle Clients vom Server und entfernt Sie aus der Liste.
   * @return
   *    Die Anzahl der Clients, die entfernt wurden.
   */
  public int removeAllClients() throws GameServerException {
    int removedSpectators = this.removeAllSpectators();
    int removedPlayers = this.removeAllPlayers();

    return removedSpectators + removedPlayers;
  }

  /**
   * Gibt an, ob der Server laueft oder nicht.
   */
  public boolean isRunning() {
    return (this.registry != null) && this.registry.isRunning();
  }

  /*   End   */
  /***********/

  /*******************/
  /* Private Methods */
  /*       End       */
  /*******************/

  /*********************/
  /* Getter and Setter */

  /**
   * @return
   *    den {@link #port}.
   */
  public int getPort() {
    return port;
  }

  /**
   * @param port
   *    ist der {@link #port}.
   */
  public void setPort(int port) {
    this.port = port;
  }

  /**
   * @return
   *    das {@link #password}.
   */
  public String getPassword() {
    return password;
  }

  /**
   * @param password
   *    ist der {@link #password}.
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Schickt eine Nachricht an alle verbundenen Clients.
   *
   * @param clientMessageType
   *      Der Nachrichtentyp, der an die Clients gesendet wird. Darf nicht null sein.
   */
  public void sendClientMessage(ClientMessageType clientMessageType) {
    Assert.assertNotNull(clientMessageType, ClientMessageType.class);


  }

  /*        End        */
  /*********************/

  /*****************/
  /* Inner classes */
  /*      End      */
  /*****************/
}