package com.github.odinasen.durak.business.network.server;

import com.github.odinasen.durak.business.ObserverNotificator;
import com.github.odinasen.durak.business.exception.GameServerCode;
import com.github.odinasen.durak.business.exception.SystemException;
import com.github.odinasen.durak.business.game.ClientUtils;
import com.github.odinasen.durak.business.game.Player;
import com.github.odinasen.durak.business.network.ClientMessageType;
import com.github.odinasen.durak.business.network.NetworkMessage;
import com.github.odinasen.durak.business.network.SIMONConfiguration;
import com.github.odinasen.durak.business.network.server.event.DurakEventObjectConsumer;
import com.github.odinasen.durak.business.network.server.event.DurakServiceEvent;
import com.github.odinasen.durak.business.network.server.event.DurakServiceEventHandler;
import com.github.odinasen.durak.dto.ClientDto;
import com.github.odinasen.durak.model.ServerUserModel;
import com.github.odinasen.durak.util.Assert;
import com.github.odinasen.durak.util.LoggingUtility;
import de.root1.simon.Registry;
import de.root1.simon.Simon;
import de.root1.simon.exceptions.NameBindingException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import static com.github.odinasen.durak.business.network.server.event.DurakServiceEvent
        .DurakServiceEventType;

/**
 * Das ist der Spieleserver. Er verwaltet ein laufendes Spiel und wertet Aktionen aus, um Sie dann
 * allen Spielern zu kommunizieren.<br/>
 * Methoden werden über ein Singleton-Objekt aufgerufen.<br/>
 * Standardmaessig laueft der Server auf Port 10000.<br/>
 * <p>
 * Die Klasse erbt von {@link java.util.Observable} und meldet allen registrierten
 * {@link java.util.Observer} eine Veraenderung mit {@link DurakServiceEvent} als
 * Informationsobjekt.
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 21.06.14
 */
public class GameServer
        extends ObserverNotificator
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
        userModel = new ServerUserModel();
        gameIsRunning = false;

        eventHandler = new DurakServiceEventHandler();
        eventHandler.registerEventFunction(
                DurakServiceEventType.CLIENT_LOGIN, new ClientLoginHandler());
        eventHandler.registerEventFunction(
                DurakServiceEventType.CLIENT_LOGOUT, new ClientLogoutHandler());
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
     *         with error code,
     *         GameServerCode.SERVICE_ALREADY_RUNNING, NETWORK_ERROR, PORT_USED.
     *         Each Exception contains the property "port".
     */
    public void startServer(int port, String password) throws SystemException {
        ServerService newServerService = ServerService.createService(password);
        Registry newRegistry;
        try {
            newRegistry = Simon.createRegistry(port);
            newRegistry.start();
            newRegistry.bind(SIMONConfiguration.REGISTRY_NAME_SERVER, newServerService);

            registry = newRegistry;
            serverService = newServerService;

            // Wichtig, ansonsten kommen keine Infos im Server an, von Clients, die den
            // SIMON-Service
            // verwenden wegen An- und Abmeldung
            serverService.addObserver(this);

            LoggingUtility.embedInfo(LOGGER, LoggingUtility.STARS, "Server is running");
        } catch (NameBindingException | IOException e) {
            stopRegistry(registry);
            new GameServerExceptionHandler(e, port).handleException();
        }
    }

    public void startGame() throws SystemException {
        List<Player> players = getPlayers();
        if (players.size() > 1 && isRunning()) {
            gameIsRunning = true;
        } else {
            throw new SystemException(GameServerCode.NOT_ENOUGH_PLAYERS_FOR_GAME).set(
                    "playerCount", getPlayers().size());
        }
    }

    /**
     * Stoppt den laufenden Server. Laueft ein Spiel, wird dieses auch geschlossen.
     */
    public void stopServer() {
        removeAllPlayers();

        stopGame();

        if (isRunning()) {
            stopRegistry(registry);
            LOGGER.info(LoggingUtility.STARS + " Server shut down " + LoggingUtility.STARS);
        }


    }

    private void stopRegistry(Registry registry) {
        if (registry != null) {
            registry.unbind(SIMONConfiguration.REGISTRY_NAME_SERVER);
            registry.stop();
        }
    }

    public void stopGame() {
        gameIsRunning = false;
    }

    /**
     * Fuegt einen Client zum Spiel als Spieler oder Beobachter hinzu. Je nach dem, ob das Spiel
     * bereits laueft oder nicht. Es kann auch sein, dass ein Client gar nicht hinzugefuegt wird.
     *
     * @param client
     *         Der Client, entweder als Spieler oder Zuschauer hinzugefuegt werden soll.
     */
    public synchronized void addClient(ClientDto client) {
        // Darf nur etwas gemacht werden, wenn der Server auch laeuft
        if (isRunning()) {
            List<Player> players = userModel.getPlayers();
            // Hat das Spiel begonnen oder gibt es mehr als 5 Spieler?
            if (!gameIsRunning && (players.size() <= 5)) {
                players.add(new Player(client));
            }
        }
    }

    /**
     * Trennt alle spielenden Clients vom Server und entfernt Sie aus der entsprechenden Liste.
     *
     * @return Die Anzahl der Clients, die entfernt wurden.
     */
    public int removeAllPlayers() {
        List<Player> players = userModel.getPlayers();
        int removed = players.size();

        players.clear();

        return removed;
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
     * @param clientMessage
     *         Die Nachricht, die an die Clients gesendet wird. Darf nicht null sein.
     */
    public void sendClientMessage(NetworkMessage<ClientMessageType> clientMessage) {
        Assert.assertNotNull(clientMessage, NetworkMessage.class);
        //---------------------------------------------------------------------------
    }

    /**
     * Setzt das Serverpasswort.
     */
    public void setPassword(String password) {
        if (serverService != null) {
            serverService.setServerPassword(password);
        }
    }

    public List<Player> getPlayers() {
        return userModel.getPlayers();
    }

    /**
     * Update-Methode aus dem Observable-Pattern. Wird aufgerufen, wenn einer der registrierten
     * Observable-Objekte die notify-Methode aufruft.
     *
     * @param observable
     *         ist das Observable-Objekt, dass die notify-Methode aufgerufen hat.
     * @param o
     *         Ein zusatzliches Informations-Objekt fuer weitere verarbeiten.
     */
    @Override
    public void update(Observable observable, Object o) {
        if (o instanceof DurakServiceEvent) {
            DurakServiceEvent event = (DurakServiceEvent)o;

            this.eventHandler.handleEvent(event);
        }
    }

    public void removeClients(List<ClientDto> clients) {
        if (clients != null && !clients.isEmpty()) {
            List<Player> players = userModel.getPlayers();

            List<String> toBeRemovedClientIds = new ArrayList<>(clients.size());
            players.removeIf(player -> {
                boolean listContainsId = ClientUtils.listHasClientWithPlayerId(clients, player);
                if (listContainsId) {
                    toBeRemovedClientIds.add(player.getId());
                }
                return listContainsId;
            });

            toBeRemovedClientIds.forEach(
                    clientId -> serverService.removeClientBySessionId(clientId));
        }
    }

    public boolean gameIsRunning() {
        return gameIsRunning;
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
            List clientList = event.getEventObject();

            // Clients aus dem Server entfernen, ggflls. Spiel beenden und andere Spieler
            // informieren
            GameServer.this.removeClients((List<ClientDto>)clientList);

            GameServer.this.setChangedAndNotifyObservers(event);
        }
    }
}