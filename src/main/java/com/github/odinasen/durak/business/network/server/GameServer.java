package com.github.odinasen.durak.business.network.server;

import com.github.odinasen.durak.business.exception.GameServerCode;
import com.github.odinasen.durak.business.exception.SystemException;
import com.github.odinasen.durak.business.game.Player;
import com.github.odinasen.durak.business.game.Spectator;
import com.github.odinasen.durak.business.network.*;
import com.github.odinasen.durak.i18n.I18nSupport;
import com.github.odinasen.durak.util.Assert;
import com.github.odinasen.durak.util.LoggingUtility;
import de.root1.simon.Registry;
import de.root1.simon.Simon;
import de.root1.simon.exceptions.NameBindingException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
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
public class GameServer
        implements Observer {
    private static final Logger LOGGER = LoggingUtility.getLogger(GameServer.class.getName());

    /**
     * Objekt, das alle Service-Methoden fuer den Durakserver enthaelt.
     */
    private DurakServerService serverService;

    /** Liste aller Spieler im Server */
    private List<Player> players;

    /** Liste aller Beobachter im Server */
    private List<Spectator> spectators;

    /**
     * Ist das Singleton-Objekt der Klasse.
     */
    private static GameServer instance;

    /**
     * Ist das Registry-Objekt fuer die SIMON-Verbindung.
     */
    private Registry registry;

    private GameServer() {
        this.players = new ArrayList<>(6);
        this.spectators = new ArrayList<>(0);
    }

    public static GameServer getInstance() {
        if (instance == null) {
            instance = new GameServer();
        }

        return instance;
    }

    /**
     * Ueberladung von {@link #startServer(int, String)}. Startet den Server mit leerem Passwort
     * .
     */
    public void startServer(int port) throws SystemException {
        startServer(port, "");
    }

    /**
     * Startet den Server. Kann der Server aus einem Grund nicht gestartet werden, wird eine
     * {@link com.github.odinasen.durak.business.exception.SystemException} geworfen.
     *
     * @param port
     *         ist der Port auf dem der Server gestartet wird.
     * @param password
     *         Passwort des Servers.
     *
     * @throws com.github.odinasen.durak.business.exception.SystemException
     *         <ol> als <b>praesentierbare Nachricht</b> wenn,
     *         <li>der Service schon einmal registriert wurde, also die Methode schon einmal
     *         ausgefuehrt
     *         wurde, ohne {@link #stopServer()} aufzurufen.</li>
     *         <li>die IP-Adresse des Servers nicht gefunden werden konnte.</li>
     *         <li>es ein Problem mit dem Netzwerklayer gibt.</li>
     *         </ol>
     *         Als Exception-Attribut wird "port" gesetzt.
     */
    public void startServer(int port, String password) throws SystemException {
        serverService = new DurakServerService(password);

        try {
            this.registry = Simon.createRegistry(port);
            this.registry.start();
            this.registry.bind(SIMONConfiguration.REGISTRY_NAME_SERVER, this.serverService);

            LoggingUtility.embedInfo(LOGGER, LoggingUtility.STARS, "Server is running");
        } catch (NameBindingException e) {
            this.registry.stop();
            throw SystemException.wrap(e, GameServerCode.SERVICE_ALREADY_RUNNING).set("port", port);
        } catch (UnknownHostException e) {
            this.registry.stop();
            throw SystemException.wrap(e, GameServerCode.NETWORK_ERROR).set("port", port);
        } catch (IOException e) {
            this.registry.stop();
            throw SystemException.wrap(e, GameServerCode.PORT_USED).set("port", port);
        }
    }

    /** Startet das Spiel. */
    public void startGame() {

    }

    /** Stoppt den laufenden Server. Laueft ein Spiel, wird dieses auch geschlossen. */
    public void stopServer() {
        try {
            this.removeAllClients();
        } catch (SystemException e) {
            LOGGER.info(I18nSupport.getException(e.getErrorCode()));
        }

        if (this.registry != null) {
            this.registry.unbind(SIMONConfiguration.REGISTRY_NAME_SERVER);
            this.registry.stop();
            LOGGER.info(LoggingUtility.STARS + " Server shut down " + LoggingUtility.STARS);
        }
    }

    /** Stoppt ein laufendes Spiel. */
    public void stopGame() {

    }

    /**
     * Trennt alle beobachtenden Clients vom Server und entfernt Sie aus der entsprechenden Liste.
     *
     * @return Die Anzahl der Clients, die entfernt wurden.
     */
    public int removeAllSpectators() {
        int removed = this.spectators.size();

        this.spectators.clear();

        return removed;
    }

    /**
     * Trennt alle spielenden Clients vom Server und entfernt Sie aus der entsprechenden Liste.
     *
     * @return Die Anzahl der Clients, die entfernt wurden.
     */
    public int removeAllPlayers() throws SystemException {
        int removed = this.players.size();

        this.players.clear();

        return removed;
    }

    /**
     * Trennt alle Clients vom Server und entfernt Sie aus der Liste.
     *
     * @return Die Anzahl der Clients, die entfernt wurden.
     */
    public int removeAllClients() throws SystemException {
        int removedPlayers = this.removeAllPlayers();
        int removedSpectators = this.removeAllSpectators();

        return removedSpectators + removedPlayers;
    }

    /** Gibt an, ob der Server laueft oder nicht. */
    public boolean isRunning() {
        return (this.registry != null) && this.registry.isRunning();
    }

    /**
     * Schickt eine Nachricht an alle verbundenen Clients.
     *
     * @param clientMessage
     *         Die Nachricht, die an die Clients gesendet wird. Darf nicht null sein.
     */
    public void sendClientMessage(NetworkMessage<ClientMessageType> clientMessage) {
        Assert.assertNotNull(clientMessage, NetworkMessage.class);
        //---------------------------------------------------------------------------

    }

    /** Setzt das Serverpasswort. */
    public void setPassword(String password) {
        this.serverService.setPassword(password);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof NetworkMessage) {
            NetworkMessage<ServerMessageType> message = parseNetworkMessage((NetworkMessage<?>)arg, ServerMessageType.USER_LOGIN);
            if (message != null) {
                // TODO user aus getMessageObject holen und dann in Liste aufnehmen und an Oberfläche weiterleiten
            } else {
                // Nichts machen oder anderen Typ pruefen. Eventuell diesen Teil an einen MessageHandler deligieren
            }
        }
    }

    /**
     * Prueft, ob ein NetworkMessage-Objekt vom erwarteten Typ ist und gibt dieses dann zurueck. Ist es nicht von dem Typ
     * wird null zurueckgeliefert.
     * @param clazz Der erwartete Typ des NetworkMessage-Objekts
     * @param <T>
     *     Die Parametrisierung des NetworkMessage-Objekts.
     * @return Ein NetworkMessage-Objekt vom erwarteten Typ oder null.
     */
    private <T extends Enum<?>>NetworkMessage<T> parseNetworkMessage(NetworkMessage<?> networkMessage, T expectedType) {
        if (networkMessage != null) {
            if (networkMessage.getMessageTypeClass().equals(expectedType.getClass())) {
                return (NetworkMessage<T>)networkMessage;
            }
        }

        return null;
    }
}