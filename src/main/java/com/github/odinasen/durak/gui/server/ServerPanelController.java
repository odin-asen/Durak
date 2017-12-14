package com.github.odinasen.durak.gui.server;

import com.github.odinasen.durak.ApplicationStartParameter;
import com.github.odinasen.durak.business.exception.SystemException;
import com.github.odinasen.durak.business.network.ClientMessageType;
import com.github.odinasen.durak.business.network.NetworkMessage;
import com.github.odinasen.durak.business.network.server.GameServer;
import com.github.odinasen.durak.business.network.server.event.DurakEventObjectConsumer;
import com.github.odinasen.durak.business.network.server.event.DurakServiceEvent;
import com.github.odinasen.durak.business.network.server.event.DurakServiceEventHandler;
import com.github.odinasen.durak.dto.ClientDto;
import com.github.odinasen.durak.gui.FXMLNames;
import com.github.odinasen.durak.gui.MainGUIController;
import com.github.odinasen.durak.gui.controller.JavaFXController;
import com.github.odinasen.durak.gui.notification.DialogPopupFactory;
import com.github.odinasen.durak.gui.server.model.GameServerModel;
import com.github.odinasen.durak.i18n.BundleStrings;
import com.github.odinasen.durak.i18n.I18nSupport;
import com.github.odinasen.durak.util.Assert;
import com.github.odinasen.durak.util.LoggingUtility;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;

import java.util.*;
import java.util.logging.Logger;

public class ServerPanelController
        extends JavaFXController
        implements Observer {
    private static final Logger LOGGER = LoggingUtility.getLogger(ServerPanelController.class);

    private static final String ASSERT_SERVER_RUN_BEFORE_GAME =
            "Server must run before trying " + "to launch a game!";

    private static final String GAME_NOT_STARTED_MESSAGE = "Muss mit Inhalt gefuellt werden.";

    private DurakServiceEventHandler eventHandler;

    @FXML
    private Parent root;

    @FXML
    private GridPane configurationPanel;

    @FXML
    private Button buttonLaunchServer;
    @FXML
    private Button buttonLaunchGame;
    @FXML
    private ListView<ClientDto> listLoggedClients;

    @FXML
    private ServerConfigurationController configurationPanelController;

    /**
     * Model mit allen Attributen zum Server, wie Clients, Port, Passwort, etc...
     */
    private GameServerModel gameServerModel;

    public ServerPanelController() {
        super(FXMLNames.SERVER_PANEL,
              ResourceBundle.getBundle(BundleStrings.JAVAFX_BUNDLE_NAME, Locale.getDefault()));
        gameServerModel = new GameServerModel();

        initEventHandler();
    }

    private void initEventHandler() {
        eventHandler = new DurakServiceEventHandler();
        eventHandler.registerEventFunction(DurakServiceEvent.DurakServiceEventType.CLIENT_LOGIN,
                                           new ClientLoginHandler());
        eventHandler.registerEventFunction(DurakServiceEvent.DurakServiceEventType.CLIENT_LOGOUT,
                                           new ClientLogoutHandler());
    }

    @Override
    protected void initializePanel() {
        initListView();
        changeGameButton("tooltip.start.game");
        changeServerButton("tooltip.server.start.server");

        GameServer.getInstance().addObserver(this);

        initByStartParameters();
    }

    /**
     * Setzt Panel-Werte anhand der Start-Parameter der Anwendung
     */
    private void initByStartParameters() {
        ApplicationStartParameter startParameter = ApplicationStartParameter.getInstance();

        if (startParameter.canInitialStartServer()) {
            startStopServer();
        }
    }

    @Override
    protected void assertNotNullComponents() {
        final String fxmlName = getFxmlName();
        Assert.assertFXElementNotNull(root, "root", fxmlName);
        Assert.assertFXElementNotNull(buttonLaunchGame, "buttonLaunchGame", fxmlName);
        Assert.assertFXElementNotNull(buttonLaunchServer, "buttonLaunchServer", fxmlName);
        Assert.assertFXElementNotNull(configurationPanel, "configurationPanel", fxmlName);
        Assert.assertFXElementNotNull(listLoggedClients, "listLoggedClients", fxmlName);
    }

    private void initListView() {
        listLoggedClients.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listLoggedClients.setCellFactory(
                listView -> new ClientListCell(actionEvent -> removeSelectedClients()));

        listLoggedClients.setItems(gameServerModel.getClients());
    }

    /**
     * Loescht die uebergebenen Clients und gibt die Anzahl
     * der geloeschten Clients zurueck.
     */
    private int removeSelectedClients() {
        ObservableList<ClientDto> loggedClients = listLoggedClients.getItems();
        int before = loggedClients.size();

        final List<ClientDto> selectedClients =
                listLoggedClients.getSelectionModel().getSelectedItems();

        for (int i = selectedClients.size() - 1; i >= 0; i--) {
            listLoggedClients.getItems().remove(selectedClients.get(i));
        }

        removeClientsFromServerAndModel(selectedClients);

        return before - loggedClients.size();
    }

    private void removeClientsFromServerAndModel(List<ClientDto> clientsToRemove) {
        GameServer.getInstance().removeClients(clientsToRemove);
        gameServerModel.removeClients(clientsToRemove);
        NetworkMessage<ClientMessageType> networkMessage =
                new NetworkMessage<>(new Object(), ClientMessageType.CLIENT_REMOVED_BY_SERVER);
        GameServer.getInstance().sendClientMessage(networkMessage);
    }

    /**
     * Startet den Server, wenn er noch nicht gestartet ist.
     * Stoppt den Server, wenn dieser laeuft.
     * Bei Fehlern werden Dialoge angezeigt.
     */
    public void startStopServer() {
        Window mainWindow = MainGUIController.getMainWindow();

        String serverStatus;

        /* Laueft der Server? */
        if (isServerRunning()) {

            //TODO test implementieren für diesen Zweig mit GameRunning == true und GameRunning
            // == false
            /* Ja, also Server stoppen */
            if (gameServerModel.isGameRunning()) {
                DialogPopupFactory.getFactory().makeDialog(mainWindow, "Halli hallo").show();
                stopGame();
            }
            gameServerModel.removeAllClients();
            stopServer();
            configurationPanelController.toggleEnableStateForStartedServer(false);
            serverStatus = "Server wurde angehalten!";
        } else {

            /* Nein, also Server starten */
            try {
                startServer();
                serverStatus = "Server läuft!";

                /* Eingabeelemente ausgrauen */
                setEditableValueOnInputElements();
                configurationPanelController.toggleEnableStateForStartedServer(true);
            } catch (SystemException e) {
                // TODO UI Test erstellen, der bei Server Start eine SystemException provoziert
                // Darstellung einer Fehlermeldung prüfen
                /* Fehlerpopup, da der Server nicht gestartet werden konnte */
                DialogPopupFactory.getFactory()
                                  .showErrorPopup(mainWindow, e.getMessage(),
                                                  DialogPopupFactory.LOCATION_CENTRE, 8.0);
                serverStatus = "Serverstart ist fehlgeschlagen!";
            }
        }

        MainGUIController.setStatus(MainGUIController.StatusType.DEFAULT, serverStatus);
    }

    /**
     * Abhaenging von Verbindungszustand und Spielzustand, werden Eingabekomponenten aktiviert
     * bzw. deaktiviert.
     */
    private void setEditableValueOnInputElements() {
        boolean enableServerFields = !GameServer.getInstance().isRunning();
        boolean enableGameFields = !gameServerModel.isGameRunning();
    }

    /**
     * Startet das Spiel, wenn es noch nicht gestartet wurde und alle Kriterien dafuer erfuellt
     * sind.
     * Stoppt das Spiel, wenn dieses laeuft.
     * Bei Fehlern werden Dialoge angezeigt.
     * Der Server muss gestartet sein, bevor diese Methode aufgerufen wird.
     */
    public void startStopGame() {
        Window mainWindow = MainGUIController.getMainWindow();

        if (gameServerModel.isGameRunning()) {
            stopGame();
            MainGUIController.setStatus(MainGUIController.StatusType.DEFAULT,
                                        "Das Spiel wurde beendet!");
        } else {
            if (!startGame()) {
                /* Fehlerpopup, da das Spiel nicht gestartet werden konnte */
                DialogPopupFactory.getFactory()
                                  .showErrorPopup(mainWindow, GAME_NOT_STARTED_MESSAGE,
                                                  DialogPopupFactory.LOCATION_CENTRE, 8.0);
                MainGUIController.setStatus(
                        MainGUIController.StatusType.DEFAULT,
                        "Das Spiel konnte nicht gestartet werden!");
            } else {
                MainGUIController.setStatus(MainGUIController.StatusType.DEFAULT,
                                            "Das Spiel wurde gestartet!");
            }
        }
    }

    /**
     * Startet das Spiel und passt die Toolbar an.
     *
     * @return True, wenn das Spiel gestartet wurde, andernfalls false.
     */
    private boolean startGame() {
        boolean started = true;

        if (canStartGame()) {
            setGameRunning(true);
            changeGameButton("tooltip.stop.game");
            setEditableValueOnInputElements();
        } else {
            started = false;
        }

        return started;
    }

    /**
     * Prueft, ob ein Spiel gestartet werden kann. Haengt intern von der Anzahl der Spieler und
     * Karten ab.
     *
     * @return true, das Spiel kann gestartet werden. false, das Spiel kann <b>nicht</b>
     * gestartet werden.
     */
    public boolean canStartGame() {
        // Mindestens 2 Spieler und genug Karten -> (Anzahl Karten)/6 >= Anzahl Spieler

        int countPlayers = 0;
        for (ClientDto client : gameServerModel.getClients()) {
            if (!client.isSpectator()) {
                countPlayers++;
            }
        }

        int numberCards = configurationPanelController.getInitialCards();
        int cardsPerPlayer = 6;
        boolean enoughCards = numberCards / cardsPerPlayer >= countPlayers;

        return countPlayers > 2 && enoughCards;
    }

    /**
     * Startet den Server und passt die Toolbar an. Kann der Server nicht gestartet werden, wird
     * eine
     * Exception geworfen.
     *
     * @see com.github.odinasen.durak.business.network.server.GameServer#startServer(int)
     */
    private void startServer() throws SystemException {
        /* Server starten */
        GameServer server = GameServer.getInstance();

        /* Der Port wird ueber Databinding im Textfeld gesetzt */
        server.startServer(
                configurationPanelController.getPort(), configurationPanelController.getPassword());

        /* GUI veraendern */
        configurationPanelController.toggleEnableStateForStartedServer(true);
        changeServerButton("tooltip.stop.server");
        buttonLaunchGame.setVisible(true);
    }

    /**
     * Stoppt das Spiel und passt die Toolbar an.
     */
    private void stopGame() {
        setGameRunning(false);
        changeGameButton("tooltip.start.game");
        setEditableValueOnInputElements();
    }

    /**
     * Benachrichtigt angemeldete Clients, stoppt den Server und passt die Toolbar und ein paar
     * Komponenten der Oberflaeche an.
     */
    private void stopServer() {
        GameServer.getInstance()
                  .sendClientMessage(
                          new NetworkMessage<>(new Object(), ClientMessageType.SERVER_SHUTDOWN));

        GameServer.getInstance().stopServer();

        /* GUI anpassen */
        buttonLaunchGame.setVisible(false);
        changeServerButton("tooltip.server.start.server");

        configurationPanelController.toggleEnableStateForStartedServer(false);
    }

    /**
     * Aendert die Eigenschaften eines Buttons.
     */
    private void changeButton(Button button, String oldStyle, String newStyle, String tooltipKey) {
        button.getStyleClass().remove(oldStyle);
        button.getStyleClass().add(newStyle);

        Tooltip tooltip = button.getTooltip();
        if (tooltip == null) {
            tooltip = new Tooltip();
            button.setTooltip(tooltip);
        }
        tooltip.setText(I18nSupport.getValue(BundleStrings.JAVAFX, tooltipKey));
    }

    /**
     * Aendert den {@link #buttonLaunchGame}
     */
    private void changeGameButton(String tooltipKey) {
        final String oldStyle;
        final String newStyle;
        if (gameServerModel.isGameRunning()) {
            oldStyle = "startGameButton";
            newStyle = "stopGameButton";
        } else {
            oldStyle = "stopGameButton";
            newStyle = "startGameButton";
        }
        this.changeButton(buttonLaunchGame, oldStyle, newStyle, tooltipKey);
    }

    /**
     * Aendert den {@link #buttonLaunchServer}.
     */
    private void changeServerButton(String tooltipKey) {
        final String oldStyle;
        final String newStyle;
        if (isServerRunning()) {
            oldStyle = "startServerButton";
            newStyle = "stopServerButton";
        } else {
            oldStyle = "stopServerButton";
            newStyle = "startServerButton";
        }
        changeButton(buttonLaunchServer, oldStyle, newStyle, tooltipKey);
    }

    public void setGameRunning(boolean running) {
        gameServerModel.setGameRunning(isServerRunning() && running);
    }

    public boolean isServerRunning() {
        return GameServer.getInstance().isRunning();
    }

    @Override
    public void update(Observable observable, Object o) {
        if (o instanceof DurakServiceEvent) {
            DurakServiceEvent event = (DurakServiceEvent)o;
            eventHandler.handleEvent(event);
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

            Platform.runLater(() -> ServerPanelController.this.gameServerModel.addClient(client));
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
            List clientIds = event.getEventObject();

            Platform.runLater(
                    () -> ServerPanelController.this.gameServerModel.removeClients(clientIds));
        }
    }
}
