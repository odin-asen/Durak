package com.github.odinasen.durak.business.network.server;

import com.github.odinasen.durak.Assert;
import com.github.odinasen.durak.LoggingUtility;
import com.github.odinasen.durak.business.exception.GameServerCode;
import com.github.odinasen.durak.business.exception.SystemException;
import com.github.odinasen.durak.business.network.ClientMessageType;
import com.github.odinasen.durak.business.network.DurakServerService;
import com.github.odinasen.durak.business.network.SIMONConfiguration;
import com.github.odinasen.durak.data.model.server.GameServerModel;
import com.github.odinasen.durak.i18n.I18nSupport;
import de.root1.simon.Registry;
import de.root1.simon.Simon;
import de.root1.simon.exceptions.NameBindingException;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Logger;

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
   * Model mit allen Attributen zum Server, wie Clients, Port, Passwort, etc...
   */
  private GameServerModel gameServerModel;

  /****************/
  /* Constructors */

  private GameServer() {
    this.gameServerModel = new GameServerModel();
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
   * {@link com.github.odinasen.durak.business.exception.SystemException} geworfen.
   * @throws com.github.odinasen.durak.business.exception.SystemException
   *    <ol> als <b>praesentierbare Nachricht</b> wenn,
   *      <li>der Service schon einmal registriert wurde, also die Methode schon einmal ausgefuehrt
   *      wurde, ohne {@link #stopServer()} aufzurufen.</li>
   *      <li>die IP-Adresse des Servers nicht gefunden werden konnte.</li>
   *      <li>es ein Problem mit dem Netzwerklayer gibt.</li>
   *    </ol>
   *    Als Exception Attribut wird "port" gesetzt.
   */
  public void startServer()
    throws SystemException {
    serverService = new DurakServerService();

    final int port = this.gameServerModel.getPort().getValue();

    try {
      this.registry = Simon.createRegistry(port);
      this.registry.bind(SIMONConfiguration.REGISTRY_NAME_SERVER, this.serverService);

      LoggingUtility.embedInfo(LOGGER, LoggingUtility.STARS, "Server is running");
    } catch (NameBindingException e) {
      throw SystemException.wrap(e, GameServerCode.SERVICE_ALREADY_RUNNING)
                           .set("port", port);
    } catch (UnknownHostException e) {
      throw SystemException.wrap(e, GameServerCode.NETWORK_ERROR)
                           .set("port", port);
    } catch (IOException e) {
      throw SystemException.wrap(e, GameServerCode.PORT_USED)
                           .set("port", port);
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
    } catch (SystemException e) {
      LOGGER.info(I18nSupport.getException(e.getErrorCode()));
    }
    if (this.registry != null) {
      this.registry.unbind(SIMONConfiguration.REGISTRY_NAME_SERVER);
      this.registry.stop();
      LOGGER.info(LoggingUtility.STARS+" Server shut down "+LoggingUtility.STARS);
    }
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
    // TODO Alle Beobachter entfernen, dazu müssen SPieler erst hinzugefügt werden
    return 0;
  }

  /**
   * Trennt alle spielenden Clients vom Server und entfernt Sie aus der entsprechenden Liste.
   * @return
   *    Die Anzahl der Clients, die entfernt wurden.
   */
  public int removeAllPlayers() throws SystemException {
    // TODO Alle Spieler entfernen, dazu müssen SPieler erst hinzugefügt werden
    return 0;
  }

  /**
   * Trennt alle Clients vom Server und entfernt Sie aus der Liste.
   * @return
   *    Die Anzahl der Clients, die entfernt wurden.
   */
  public int removeAllClients() throws SystemException {
    int removedPlayers = this.removeAllPlayers();
    int removedSpectators = this.removeAllSpectators();

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
   *    den Port des Servers als JavaFX-Property.
   */
  public IntegerProperty getPort() {
    return this.gameServerModel.getPort();
  }

  /**
   * @return
   *    das Passwort des Servers als JavaFX-Property.
   */
  public StringProperty getPassword() {
    return this.gameServerModel.getPassword();
  }

  /**
   * Schickt eine Nachricht an alle verbundenen Clients.
   *
   * @param clientMessageType
   *      Der Nachrichtentyp, der an die Clients gesendet wird. Darf nicht null sein.
   */
  public void sendClientMessage(ClientMessageType clientMessageType) {
    Assert.assertNotNull(clientMessageType, ClientMessageType.class);
    //----------------------------------------------------------------------------------------------
  }

  /*        End        */
  /*********************/

  /*****************/
  /* Inner classes */
  /*      End      */
  /*****************/
}