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
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import javafx.util.converter.NumberStringConverter;

import java.util.*;
import java.util.logging.Logger;

public class ServerPanelController
        extends JavaFXController
        implements Observer {
    private static final Logger LOGGER = LoggingUtility.getLogger(ServerPanelController.class);

    private static final String ASSERT_SERVER_RUN_BEFORE_GAME =
            "Server must run before trying " + "to launch a game!";

    private static final String GAME_NOT_STARTED_MESSAGE = "Muss mit Inhalt gefuellt werden.";

    private static JavaFXController controller;

    private DurakServiceEventHandler eventHandler;

    @FXML
    private Parent root;

    @FXML
    private GridPane serverConfigPanel;
    @FXML
    private TextField fieldServerPort;
    @FXML
    private ChoiceBox<InitialCard> boxInitialCards;
    private Label labelInitialCards;
    @FXML
    private TextField fieldPassword;
    @FXML
    private Button buttonLaunchServer;
    @FXML
    private Button buttonLaunchGame;
    @FXML
    private ListView<ClientDto> listLoggedClients;

    /**
     * Model mit allen Attributen zum Server, wie Clients, Port, Passwort, etc...
     */
    private GameServerModel gameServerModel;

    public ServerPanelController() {
        super(FXMLNames.SERVER_PANEL,
              ResourceBundle.getBundle(BundleStrings.JAVAFX_BUNDLE_NAME, Locale.getDefault()));
        this.gameServerModel = new GameServerModel();

        initEventHandler();

        if (controller == null) {
            controller = this;
        }
    }

    private void initEventHandler() {
        this.eventHandler = new DurakServiceEventHandler();
        eventHandler.registerEventFunction(DurakServiceEvent.DurakServiceEventType.CLIENT_LOGIN,
                                           new ClientLoginHandler());
        eventHandler.registerEventFunction(DurakServiceEvent.DurakServiceEventType.CLIENT_LOGOUT,
                                           new ClientLogoutHandler());
    }

    @Override
    protected void initializePanel() {
        initListView();
        this.changeGameButton("tooltip.start.game");
        this.changeServerButton("tooltip.server.start.server");
        initInitialCardsComponents();

        this.fieldServerPort.textProperty()
                            .bindBidirectional(this.gameServerModel.getPort(),
                                               new NumberStringConverter());
        this.fieldPassword.textProperty().bindBidirectional(this.gameServerModel.getPassword());

        GameServer.getInstance().addObserver(this);

        initByStartParameters();
    }

    /**
     * Setzt Panel-Werte anhand der Start-Parameter der Anwendung
     */
    private void initByStartParameters() {
        ApplicationStartParameter startParameter = ApplicationStartParameter.getInstance();
        this.gameServerModel.getPassword().setValue(startParameter.getServerPwd());
        this.gameServerModel.getPort().setValue(startParameter.getServerPort());

        if (startParameter.canInitialStartServer()) {
            startStopServer();
        }
    }

    @Override
    protected void assertNotNullComponents() {
        final String fxmlName = this.getFxmlName();
        Assert.assertFXElementNotNull(this.root, "root", fxmlName);
        Assert.assertFXElementNotNull(this.buttonLaunchGame, "buttonLaunchGame", fxmlName);
        Assert.assertFXElementNotNull(this.buttonLaunchServer, "buttonLaunchServer", fxmlName);
        Assert.assertFXElementNotNull(this.serverConfigPanel, "serverConfigPanel", fxmlName);
        Assert.assertFXElementNotNull(this.fieldServerPort, "fieldServerPort", fxmlName);
        Assert.assertFXElementNotNull(this.boxInitialCards, "boxInitialCards", fxmlName);
        Assert.assertFXElementNotNull(this.fieldPassword, "fieldPassword", fxmlName);
        Assert.assertFXElementNotNull(this.listLoggedClients, "listLoggedClients", fxmlName);
    }

    private void initListView() {
        listLoggedClients.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listLoggedClients.setCellFactory(
                listView -> new ClientListCell(actionEvent -> removeSelectedClients()));

        listLoggedClients.getItems().addListener((ListChangeListener<ClientDto>)change -> {
            while (change.next()) {
                if (change.wasRemoved()) {
                    List<? extends ClientDto> clientsToRemove = change.getRemoved();
                    GameServer.getInstance().removeClients(clientsToRemove);
                    ServerPanelController.this.gameServerModel.removeClients(clientsToRemove);
                    GameServer.getInstance()
                              .sendClientMessage(new NetworkMessage<>(new Object(),
                                                                      ClientMessageType
                                                                              .CLIENT_REMOVED_BY_SERVER));
                }
            }
        });

        listLoggedClients.setItems(this.gameServerModel.getClients());
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

            /* Ja, also Server stoppen */
            if (gameServerModel.isGameRunning()) {
                DialogPopupFactory.getFactory().makeDialog(mainWindow, "Halli hallo").show();
                stopGame();
            }
            gameServerModel.removeAllClients();
            stopServer();
            serverStatus = "Server wurde angehalten!";
        } else {

            /* Nein, also Server starten */
            try {
                startServer();
                serverStatus = "Server läuft!";

                /* Eingabeelemente ausgrauen */
                setEditableValueOnInputElements();
            } catch (SystemException e) {
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

        fieldPassword.setEditable(enableServerFields);
        fieldServerPort.setEditable(enableServerFields);
        setBoxEditable(enableGameFields);
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
     * Initialisiert das ChoiceBox- und Label-Objekt für die Anzahl der Karten
     */
    private void initInitialCardsComponents() {
        ObservableList<InitialCard> cards = boxInitialCards.getItems();
        Collections.addAll(cards, InitialCard.values());
        boxInitialCards.setValue(cards.get(0));

        /* Nicht editierbares Feld initialisieren */
        labelInitialCards = new Label();
        copyHeightsAndWidths(boxInitialCards, labelInitialCards);
        GridPane.setMargin(labelInitialCards, GridPane.getMargin(boxInitialCards));
        GridPane.setColumnIndex(labelInitialCards, GridPane.getColumnIndex(boxInitialCards));
        GridPane.setRowIndex(labelInitialCards, GridPane.getRowIndex(boxInitialCards));
    }

    /**
     * Kopiert die Werte maxHeight, maxWidth, minHeight, minWidth,
     * prefHeight und preifWidth von einem Control-Objekt zum anderen.
     */
    private void copyHeightsAndWidths(Control from, Control to) {
        to.setMaxWidth(from.getMaxWidth());
        to.setMinWidth(from.getMinWidth());
        to.setMaxHeight(from.getMaxHeight());
        to.setMinHeight(from.getMinHeight());
        to.setPrefHeight(from.getPrefHeight());
        to.setPrefWidth(from.getPrefWidth());
    }

    /**
     * Label und Choicebox tauschen Plaetze.
     */
    private void setBoxEditable(boolean editable) {
        final ObservableList<Node> children = serverConfigPanel.getChildren();

        if (editable) {
            children.remove(labelInitialCards);
            if (!children.contains(boxInitialCards)) {
                children.add(boxInitialCards);
            }
        } else {
            children.remove(boxInitialCards);
            if (!children.contains(labelInitialCards)) {
                children.add(labelInitialCards);
                labelInitialCards.setText(boxInitialCards.getValue().toString());
            }
        }
    }

    /**
     * Loescht die uebergebenen Clients und gibt die Anzahl
     * der geloeschten Clients zurueck.
     */
    private int removeSelectedClients() {
        ObservableList<ClientDto> loggedClients = this.listLoggedClients.getItems();
        int before = loggedClients.size();

        final List<ClientDto> selectedClients =
                this.listLoggedClients.getSelectionModel().getSelectedItems();

        for (int i = selectedClients.size() - 1; i >= 0; i--) {
            this.listLoggedClients.getItems().remove(selectedClients.get(i));
        }

        return before - loggedClients.size();
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

        int numberCards = boxInitialCards.getValue().getNumberCards();
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
        server.startServer(gameServerModel.getPort().getValue(),
                           gameServerModel.getPassword().getValue());

        /* GUI veraendern */
        fieldServerPort.setEditable(false);
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
        fieldServerPort.setEditable(true);
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

    public Parent getPanel() {
        return root;
    }

    public static JavaFXController getInstance() {
        return controller;
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