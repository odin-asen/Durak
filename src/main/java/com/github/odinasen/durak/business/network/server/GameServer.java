package com.github.odinasen.durak.business.network.server;

import com.github.odinasen.durak.business.ExtendedObservable;
import com.github.odinasen.durak.business.exception.GameServerCode;
import com.github.odinasen.durak.business.exception.SystemException;
import com.github.odinasen.durak.business.game.Player;
import com.github.odinasen.durak.business.game.Spectator;
import com.github.odinasen.durak.business.network.ClientMessageType;
import com.github.odinasen.durak.business.network.NetworkMessage;
import com.github.odinasen.durak.business.network.SIMONConfiguration;
import com.github.odinasen.durak.business.network.server.event.DurakEventObjectConsumer;
import com.github.odinasen.durak.business.network.server.event.DurakServiceEvent;
import com.github.odinasen.durak.business.network.server.event.DurakServiceEventHandler;
import com.github.odinasen.durak.dto.ClientDto;
import com.github.odinasen.durak.i18n.I18nSupport;
import com.github.odinasen.durak.model.ServerUserModel;
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
import java.util.function.Predicate;
import java.util.logging.Logger;

import static com.github.odinasen.durak.business.network.server.event.DurakServiceEvent.DurakServiceEventType;

/**
 * Das ist der Spieleserver. Er verwaltet ein laufendes Spiel und wertet Aktionen aus, um Sie dann
 * allen Spielern zu kommunizieren.<br/>
 * Methoden werden über ein Singleton-Objekt aufgerufen.<br/>
 * Standardmaessig laueft der Server auf Port 10000.<br/>
 * <p>
 * Die Klasse erbt von {@link java.util.Observable} und meldet allen registrierten
 * {@link java.util.Observer} eine Veraenderung mit {@link DurakServiceEvent} als Informationsobjekt.
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 21.06.14
 */
public class GameServer
        extends ExtendedObservable
        implements Observer {
    private static final Logger LOGGER = LoggingUtility.getLogger(GameServer.class.getName());
    /**
     * Ist das Singleton-Objekt der Klasse.
     */
    private static GameServer instance;
    private DurakServiceEventHandler eventHandler;
    /**
     * Objekt, das alle Service-Methoden fuer den Durakserver enthaelt.
     */
    private ServerService serverService;
    private ServerUserModel userModel;
    /**
     * Indikator, ob ein Spiel laueft oder nicht.
     */
    private boolean gameIsRunning;
    /**
     * Ist das Registry-Objekt fuer die SIMON-Verbindung.
     */
    private Registry registry;

    private GameServer() {
        this.userModel = new ServerUserModel();
        this.gameIsRunning = false;

        this.eventHandler = new DurakServiceEventHandler();
        this.eventHandler.registerEventFunction(DurakServiceEventType.CLIENT_LOGIN, new ClientLoginHandler());
        this.eventHandler.registerEventFunction(DurakServiceEventType.CLIENT_LOGOUT, new ClientLogoutHandler());
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
     * @param port     ist der Port auf dem der Server gestartet wird.
     * @param password Passwort des Servers.
     * @throws com.github.odinasen.durak.business.exception.SystemException <ol> als <b>praesentierbare Nachricht</b>
     *                                                                      wenn,
     *                                                                      <li>der Service schon einmal registriert
     *                                                                      wurde, also die Methode schon einmal
     *                                                                      ausgefuehrt
     *                                                                      wurde, ohne {@link #stopServer()}
     *                                                                      aufzurufen.</li>
     *                                                                      <li>die IP-Adresse des Servers nicht
     *                                                                      gefunden werden konnte.</li>
     *                                                                      <li>es ein Problem mit dem Netzwerklayer
     *                                                                      gibt.</li>
     *                                                                      </ol>
     *                                                                      Als Exception-Attribut wird "port" gesetzt.
     */
    public void startServer(int port, String password) throws SystemException {
        this.serverService = ServerService.createService(password);
        // Wichtig, ansonsten kommen keine Infos im Server an, von Clients, die den SIMON-Service
        // verwenden wegen An- und Abmeldung
        this.serverService.addObserver(this);

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

    public void startGame() {
        this.gameIsRunning = true;
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
            LOGGER.info(LoggingUtility.STARS + " Server shut down " + LoggingUtility.STARS);
        }
    }

    public void stopGame() {
        this.gameIsRunning = false;
    }

    /**
     * Fuegt einen Client zum Spiel als Spieler oder Beobachter hinzu. Je nach dem, ob das Spiel
     * bereits laueft oder nicht. Es kann auch sein, dass ein Client gar nicht hinzugefuegt wird.
     *
     * @param client Der Client, entweder als Spieler oder Zuschauer hinzugefuegt werden soll.
     */
    public synchronized void addClient(ClientDto client) {
        // Darf nur etwas gemacht werden, wenn der Server auch laeuft
        if (isRunning()) {
            List<Player> players = this.userModel.getPlayers();
            // Hat das Spiel begonnen oder gibt es mehr als 5 Spieler?
            if (this.gameIsRunning || (players.size() > 5)) {
                // Ja
                this.userModel.getSpectators().add(new Spectator(client));
            } else {
                players.add(new Player(client));
            }
        }
    }

    /**
     * Trennt alle beobachtenden Clients vom Server und entfernt Sie aus der entsprechenden Liste.
     *
     * @return Die Anzahl der Clients, die entfernt wurden.
     */
    public int removeAllSpectators() {
        List<Spectator> spectators = this.userModel.getSpectators();
        int removed = spectators.size();

        spectators.clear();

        return removed;
    }

    /**
     * Trennt alle spielenden Clients vom Server und entfernt Sie aus der entsprechenden Liste.
     *
     * @return Die Anzahl der Clients, die entfernt wurden.
     */
    public int removeAllPlayers() throws SystemException {
        List<Player> players = this.userModel.getPlayers();
        int removed = players.size();

        players.clear();

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

    /**
     * Gibt an, ob der Server laueft oder nicht.
     */
    public boolean isRunning() {
        return (this.registry != null) && this.registry.isRunning();
    }

    /**
     * Schickt eine Nachricht an alle verbundenen Clients.
     *
     * @param clientMessage Die Nachricht, die an die Clients gesendet wird. Darf nicht null sein.
     */
    public void sendClientMessage(NetworkMessage<ClientMessageType> clientMessage) {
        Assert.assertNotNull(clientMessage, NetworkMessage.class);
        //---------------------------------------------------------------------------
    }

    /**
     * Setzt das Serverpasswort.
     */
    public void setPassword(String password) {
        this.serverService.setServerPassword(password);
    }

    public List<Player> getPlayers() {
        return this.userModel.getPlayers();
    }

    public List<Spectator> getSpectators() {
        return this.userModel.getSpectators();
    }

    /**
     * Update-Methode aus dem Observable-Pattern. Wird aufgerufen, wenn einer der registrierten
     * Observable-Objekte die notify-Methode aufruft.
     *
     * @param observable ist das Observable-Objekt, dass die notify-Methode aufgerufen hat.
     * @param o          Ein zusatzliches Informations-Objekt fuer weitere verarbeiten.
     */
    @Override
    public void update(Observable observable, Object o) {
        if (o instanceof DurakServiceEvent) {
            DurakServiceEvent event = (DurakServiceEvent) o;

            this.eventHandler.handleEvent(event);
        }
    }

    public void removeClients(List<? extends ClientDto> removedClients) {
        if (removedClients != null && !removedClients.isEmpty()) {
            List<Player> players = this.userModel.getPlayers();

            List<String> toBeRemovedClientIds = new ArrayList<>(removedClients.size());
            players.removeIf(player -> {
                long clientsWithSameIdCount =
                        removedClients.stream().filter((Predicate<ClientDto>) player::hasSameIdAs).count();
                if (clientsWithSameIdCount > 0) {
                    toBeRemovedClientIds.add(player.getId());
                    return true;
                } else {
                    return false;
                }
            });

            List<Spectator> spectators = this.userModel.getSpectators();
            spectators.removeIf(spectator -> {
                long clientsWithSameIdCount =
                        removedClients.stream().filter((Predicate<ClientDto>) spectator::hasSameIdAs).count();
                if (clientsWithSameIdCount > 0) {
                    toBeRemovedClientIds.add(spectator.getId());
                    return true;
                } else {
                    return false;
                }
            });

            toBeRemovedClientIds.forEach(clientId -> this.serverService.removeClientByIdSendNotification(clientId));
        }
    }

    /**
     * Handler fuer Clients, die sich einloggen.
     */
    class ClientLoginHandler
            extends DurakEventObjectConsumer<ClientDto> {

        public ClientLoginHandler() {
            super(ClientDto.class);
        }

        @Override
        public void accept(DurakServiceEvent<ClientDto> event) {
            ClientDto client = event.getEventObject();
            /* Client in die Liste hinzufuegen. Server regelt das selbst. */
            GameServer.this.addClient(client);

            /* Observer informieren */
            GameServer.this.setChangedAndNotifyObservers(event);
        }
    }

    /**
     * Handler fuer Clients, die sich ausloggen.
     */
    class ClientLogoutHandler
            extends DurakEventObjectConsumer<List> {

        public ClientLogoutHandler() {
            super(List.class);
        }

        @Override
        public void accept(DurakServiceEvent<List> event) {
            List idList = event.getEventObject();

            // Clients aus dem Server entfernen, ggflls. Spiel beenden und andere Spieler
            // informieren
            //GameServer.this.removeClients(clients);

            GameServer.this.setChangedAndNotifyObservers(event);
        }
    }
}