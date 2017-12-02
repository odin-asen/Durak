package com.github.odinasen.durak.business.network.client;

import com.github.odinasen.durak.business.ObserverNotificator;
import com.github.odinasen.durak.business.exception.GameClientCode;
import com.github.odinasen.durak.business.exception.SystemException;
import com.github.odinasen.durak.business.network.ClientMessageType;
import com.github.odinasen.durak.business.network.SIMONConfiguration;
import com.github.odinasen.durak.business.network.server.exception.LoginFailedException;
import com.github.odinasen.durak.business.network.server.exception.SessionNotFoundException;
import com.github.odinasen.durak.business.network.simon.Callable;
import com.github.odinasen.durak.business.network.simon.ServerInterface;
import com.github.odinasen.durak.business.network.simon.SessionInterface;
import com.github.odinasen.durak.i18n.I18nSupport;
import com.github.odinasen.durak.util.LoggingUtility;
import de.root1.simon.ClosedListener;
import de.root1.simon.Lookup;
import de.root1.simon.Simon;
import de.root1.simon.SimonUnreferenced;
import de.root1.simon.annotation.SimonRemote;
import de.root1.simon.exceptions.EstablishConnectionFailed;
import de.root1.simon.exceptions.LookupFailedException;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import static com.github.odinasen.durak.i18n.BundleStrings.USER_MESSAGES;

/**
 * Client-Klasse, welche sich mit der Server-Klasse verbindet und mit dieser direkt ueber SIMON
 * kommuniziert.
 */
public class GameClient
        extends ObserverNotificator
        implements ClosedListener {

    private static final Logger LOGGER = LoggingUtility.getLogger(GameClient.class.getName());

    /**
     * Ist das Singleton-Objekt der Klasse.
     */
    private static GameClient instance;
    /**
     * Gibt an, ob der Client mit einem Server verbunden ist oder nicht.
     */
    private boolean connected;
    private ServerMessageReceiver messageReceiver;
    private Lookup nameLookup;
    private ServerInterface server;
    private SessionInterface session;

    private GameClient() {
        this.connected = false;
        this.messageReceiver = new ServerMessageReceiver();
        Simon.setDefaultKeepAliveInterval(5);
        Simon.setDefaultKeepAliveTimeout(5);
    }

    public static GameClient getInstance() {
        if (instance == null) {
            instance = new GameClient();
        }

        return instance;
    }

    /**
     * Verbindet den Client mit einem Server.
     *
     * @param serverAddress
     *         Ist die IP- oder DNS-Adresse des Servers.
     * @param serverPort
     *         Port des des Servers.
     * @param password
     *         Passwort des Servers.
     *
     * @return True, wenn der Client mit dem Server verbunden ist, andernfalls false.
     *
     * @throws com.github.odinasen.durak.business.exception.SystemException
     *         Wird geworfen, wenn eine Verbindung nicht
     *         aufgebaut werden konnte. - siehe
     *         {@link GameClientCode}
     */
    public boolean connect(String serverAddress,
                           Integer serverPort, String clientName, String password)
            throws SystemException {

        if (connected) {
            return true;
        }

        final String failedSocketAddress = serverAddress + ":" + serverPort;

        try {
            nameLookup = Simon.createNameLookup(serverAddress, serverPort);
            server = (ServerInterface)nameLookup.lookup(SIMONConfiguration.REGISTRY_NAME_SERVER);

            nameLookup.addClosedListener(server, this);

            try {
                session = server.login(clientName, password, messageReceiver);
                connected = true;
            } catch (LoginFailedException | SessionNotFoundException e) {
                connected = false;
            }

            String socketAddress = this.getSocketAddress();
            if (connected) {
                LoggingUtility.embedInfo(
                        LOGGER, LoggingUtility.STARS, "Connected to " + socketAddress);
            } else {
                final String logMessage =
                        "Failed to connect to " + socketAddress + " without exception";
                LoggingUtility.embedInfo(LOGGER, LoggingUtility.HASHES, logMessage);
            }
        } catch (UnknownHostException e) {
            LOGGER.warning("Failed connection try to " + failedSocketAddress);
            throw new SystemException(GameClientCode.SERVER_NOT_FOUND);
        } catch (EstablishConnectionFailed e) {
            LOGGER.warning(
                    "EstablishConnectionFailed occurred while connecting: " + e.getMessage());
            throw new SystemException(GameClientCode.SERVER_NOT_FOUND);
        } catch (LookupFailedException e) {
            LOGGER.warning("LookupFailedException occurred while connection: " + e.getMessage());
            throw new SystemException(GameClientCode.SERVICE_NOT_FOUND);
        }

        return connected;
    }

    /**
     * Trennt den Client vom Server falls notwendig und baut die Verbindung zum eingegebenen Server
     * auf.
     */
    public boolean reconnect(String serverAddress,
                             Integer serverPort, String clientName, String password)
            throws SystemException {

        if (connected) {
            disconnect();
            try { /* Wait for SIMON after a disconnection before it will be connected. */
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                LOGGER.warning("Thread sleep failed: " + e.getMessage());
            }
        }

        // setup a new connection
        return connect(serverAddress, serverPort, clientName, password);
    }

    public void disconnect() {
        if (connected) {
            connected = false;

            server.logoff(session);
            nameLookup.release(server);

            LoggingUtility.embedInfo(
                    LOGGER, LoggingUtility.STARS, "Disconnected from " + getSocketAddress());
        }
    }

    /**
     * Wird aufgerufen, wenn die Verbindung zum Server getrennt wird.
     */
    @Override
    public void closed() {
        if (connected) {
            nameLookup.release(server);
            LoggingUtility.embedInfo(LOGGER, LoggingUtility.STARS, "Lost server connection");
            connected = false;
            this.setChangedAndNotifyObservers();
        }
    }

    /**
     * Liefert die Adresse, wenn der Client mit dem Server verbunden ist.
     *
     * @return IP- oder DNS-Adresse und Port.
     */
    public String getSocketAddress() {
        if (nameLookup != null) {
            return nameLookup.getServerAddress()
                             .getHostAddress() + ":" + nameLookup.getServerPort();
        } else {
            return I18nSupport.getValue(USER_MESSAGES, "no.address");
        }
    }

    public boolean isConnected() {
        return this.connected;
    }
}

/**
 * Callback-Klasse fuer Nachrichten des Servers.
 */
@SimonRemote(value = {Callable.class})
class ServerMessageReceiver
        implements Callable,
                   SimonUnreferenced,
                   Serializable {

    @Override
    public void sendClientMessage(final ClientMessageType parameter) {
        if (parameter.equals(ClientMessageType.CLIENT_REMOVED_BY_SERVER)) {
            GameClient.getInstance().disconnect();
        }
        //    if(parameter instanceof MessageObject) {
        //      new Thread(new Runnable() {
        //        public void run() {
        //          GameClient.getClient().receiveServerMessage((MessageObject) parameter);
        //        }
        //      }).start();
        //    }
    }

    @Override
    public void unreferenced() {
        LoggingUtility.getLogger(this.getClass())
                      .info("Server unreferenced this client. Good time to update display messages");
    }
}