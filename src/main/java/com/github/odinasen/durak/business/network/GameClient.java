package com.github.odinasen.durak.business.network;

import com.github.odinasen.durak.util.LoggingUtility;
import com.github.odinasen.durak.business.exception.GameClientCode;
import com.github.odinasen.durak.business.exception.SystemException;
import com.github.odinasen.durak.business.network.simon.Callable;
import com.github.odinasen.durak.business.network.simon.ServerInterface;
import com.github.odinasen.durak.dto.ClientDto;
import com.github.odinasen.durak.i18n.I18nSupport;
import de.root1.simon.ClosedListener;
import de.root1.simon.Lookup;
import de.root1.simon.Simon;
import de.root1.simon.annotation.SimonRemote;
import de.root1.simon.exceptions.EstablishConnectionFailed;
import de.root1.simon.exceptions.LookupFailedException;

import java.net.UnknownHostException;
import java.util.logging.Logger;

import static com.github.odinasen.durak.i18n.BundleStrings.USER_MESSAGES;

/**
 * Client-Klasse, welche sich mit der Server-Klasse verbindet und mit dieser direkt ueber SIMON
 * kommuniziert.
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 06.01.14
 */
@SuppressWarnings("UnusedDeclaration")
public class GameClient implements ClosedListener {

  private static final Logger LOGGER = LoggingUtility.getLogger(GameClient.class.getName());

  /**
   * Gibt an, ob der Client mit einem Server verbunden ist oder nicht.
   */
  private boolean connected;

  private ServerMessageReceiver messageReceiver;
  private Lookup nameLookup;
  private ServerInterface server;

  /****************/
  /* Constructors */

  private GameClient() {
    this.connected = false;
    this.messageReceiver = new ServerMessageReceiver();
    Simon.setDefaultKeepAliveInterval(5);
    Simon.setDefaultKeepAliveTimeout(5);
  }

  /*     End      */
  /****************/

  /***********/
  /* Methods */

  /**
   * Verbindet den Client mit einem Server.
   * @param serverAddress
   *    Ist die IP- oder DNS-Adresse des Servers.
   * @param serverPort
   *    Port des des Servers.
   * @param password
   *    Passwort des Servers.
   * @param clientDto
   *    Client Repraesentation fuer den Login am Server.
   *
   * @return
   *    True, wenn der Client mit dem Server verbunden ist, andernfalls false.
   *
   * @throws com.github.odinasen.durak.business.exception.SystemException
   *    Wird geworfen, wenn eine Verbindung nicht aufgebaut werden konnte.
   */
  public boolean connect(String serverAddress,
                         Integer serverPort,
                         String password,
                         ClientDto clientDto)
    throws SystemException {

    if (connected)
      return true;

    final String failedSocketAddress = serverAddress + ":" + serverPort;

    try {
      nameLookup = Simon.createNameLookup(serverAddress, serverPort);
      server = (ServerInterface) nameLookup.lookup(SIMONConfiguration.REGISTRY_NAME_SERVER);

      nameLookup.addClosedListener(server, this);

      connected = server.login(messageReceiver, clientDto, password);

      final String socketAddress = this.getSocketAddress();

      if (connected) {
        LoggingUtility.embedInfo(LOGGER, LoggingUtility.STARS, "Connected to " + socketAddress);
      } else {
        final String logMessage = "Failed to connect to " + socketAddress + " without exception";
        LoggingUtility.embedInfo(LOGGER, LoggingUtility.HASHES, logMessage);
      }
    } catch (UnknownHostException e) {
      LOGGER.warning("Failed connection try to " + failedSocketAddress);
      throw new SystemException(GameClientCode.SERVER_NOT_FOUND);
    } catch (EstablishConnectionFailed e) {
      LOGGER.warning("EstablishConnectionFailed occurred while connecting: " + e.getMessage());
      throw new SystemException(GameClientCode.SERVER_NOT_FOUND);
    } catch (LookupFailedException e) {
      LOGGER.warning("LookupFailedException occurred while connection: "+e.getMessage());
      throw new SystemException(GameClientCode.SERVICE_NOT_FOUND);
    }

    return connected;
  }

  /**
   * Trennt den Client vom Server falls notwendig und baut die Verbindung zum eingegebenen Server
   * auf.
   *
   * @see #connect(String, Integer, String, com.github.odinasen.durak.dto.ClientDto)
   */
  public boolean reconnect(String serverAddress,
                           Integer serverPort,
                           String password,
                           ClientDto clientDto)
    throws SystemException {

    if(connected) {
      disconnect();
      try { /* Wait for SIMON after a disconnection before it will be connected. */
        Thread.sleep(1000L);
      } catch (InterruptedException e) {
        LOGGER.warning("Thread sleep failed: "+e.getMessage());
      }
    }
    /* setup a new connection */
    return connect(serverAddress, serverPort, password, clientDto);
  }

  /**
   * Trennt die Verbindung zum Server.
   */
  public void disconnect() {
    if(connected) {
      connected = false;

      server.logoff(messageReceiver);
      nameLookup.release(server);

      LoggingUtility.embedInfo(LOGGER, LoggingUtility.STARS, "Disconnected from " + getSocketAddress());
    }
  }

  /**
   * Wird aufgerufen, wenn die Verbindung zum Server getrennt wird.
   */
  @Override
  public void closed() {
    if(connected) {
      nameLookup.release(server);
      LoggingUtility.embedInfo(LOGGER, LoggingUtility.STARS, "Lost server connection");
      connected = false;
//      setChangedAndNotify(new MessageObject(MessageType.LOST_CONNECTION));
    }
  }

  /**
   * Liefert die Adresse, wenn der Client mit dem Server verbunden ist.
   * @return
   *    IP- oder DNS-Adresse und Port.
   */
  public String getSocketAddress() {
    if(nameLookup != null) {
      return nameLookup.getServerAddress().getHostAddress() + ":" + nameLookup.getServerPort();
    } else {
      return I18nSupport.getValue(USER_MESSAGES, "no.address");
    }
  }

  /*   End   */
  /***********/

  /*******************/
  /* Private Methods */
  /*       End       */
  /*******************/

  /*********************/
  /* Getter and Setter */
  /*        End        */
  /*********************/

  /*****************/
  /* Inner classes */
  /*      End      */
  /*****************/
}

/**
 *  Callback-Klasse fuer Nachrichten des Servers.
 */
@SimonRemote(value = {Callable.class})
class ServerMessageReceiver implements Callable {

  @Override
  public void sendClientMessage(final ClientMessageType parameter) {

//    if(parameter instanceof MessageObject) {
//      new Thread(new Runnable() {
//        public void run() {
//          GameClient.getClient().receiveServerMessage((MessageObject) parameter);
//        }
//      }).start();
//    }
  }
}