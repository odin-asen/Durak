package com.github.odinasen.durak.gui.server;

import com.github.odinasen.durak.ApplicationStartParameter;
import com.github.odinasen.durak.util.Assert;
import com.github.odinasen.durak.business.exception.SystemException;
import com.github.odinasen.durak.business.network.ClientMessageType;
import com.github.odinasen.durak.business.network.server.GameServer;
import com.github.odinasen.durak.dto.ClientDto;
import com.github.odinasen.durak.gui.FXMLNames;
import com.github.odinasen.durak.gui.MainGUIController;
import com.github.odinasen.durak.gui.controller.JavaFXController;
import com.github.odinasen.durak.gui.notification.DialogPopupFactory;
import com.github.odinasen.durak.gui.server.model.GameServerModel;
import com.github.odinasen.durak.i18n.BundleStrings;
import com.github.odinasen.durak.i18n.I18nSupport;
import com.github.odinasen.durak.util.LoggingUtility;
import com.sun.javaws.Main;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.converter.NumberStringConverter;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 06.01.14
 */
public class ServerPanelController extends JavaFXController {
    private static final Logger LOGGER = LoggingUtility.getLogger(ServerPanelController.class);

    private static final String ASSERT_SERVER_RUN_BEFORE_GAME = "Server must run before trying to launch a game!";

    private static final String GAME_NOT_STARTED_MESSAGE = "Muss mit Inhalt gefuellt werden.";

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

    //================================================================================================
    // Constructors

    public ServerPanelController() {
        super(FXMLNames.SERVER_PANEL,
                ResourceBundle.getBundle(BundleStrings.JAVAFX_BUNDLE_NAME, Locale.getDefault()));
        this.gameServerModel = new GameServerModel();
    }

    //================================================================================================
    // Methods

    @Override
    protected void initializePanel() {
        initListView();
        this.changeGameButton("tooltip.start.game");
        this.changeServerButton("tooltip.server.start.server");
        initInitialCardsComponents();

        this.fieldServerPort.textProperty().bindBidirectional(this.gameServerModel.getPort(),
                new NumberStringConverter());
        this.fieldPassword.textProperty().bindBidirectional(this.gameServerModel.getPassword());

        // Wenn das Panel angezeigt wird, wird es initialisiert mit Startparametern
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
                new Callback<ListView<ClientDto>, ListCell<ClientDto>>() {
                    @Override
                    public ListCell<ClientDto> call(ListView<ClientDto> listView) {
                        return new ClientListCell(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent actionEvent) {
                                removeSelectedClients();
                            }
                        });
                    }
                });
    }

    /**
     * Startet den Server, wenn er noch nicht gestartet ist.
     * Stoppt den Server, wenn dieser laeuft.
     * Bei Fehlern werden Dialoge angezeigt.
     */
    public void startStopServer() {
        Window mainWindow = MainGUIController.getMainWindow();

        String serverStatus;

        // Laueft der Server?
        if (this.isServerRunning()) {

            // Ja, also Server stoppen
            if (this.gameServerModel.isGameRunning()) {
                DialogPopupFactory.getFactory().makeDialog(mainWindow, "Halli hallo").show();
                this.stopGame();
            }
            this.stopServer();
            serverStatus = "Server wurde angehalten!";
        } else {

            // Nein, also Server starten
            try {
                this.startServer();
                serverStatus = "Server läuft!";
            } catch (SystemException e) {
                // Fehlerpopup, da der Server nicht gestartet werden konnte
                DialogPopupFactory.getFactory().showErrorPopup(
                        mainWindow, e.getMessage(), DialogPopupFactory.LOCATION_CENTRE, 8.0);
                serverStatus = "Serverstart ist fehlgeschlagen!";
            }
        }

        MainGUIController.setStatus(MainGUIController.StatusType.DEFAULT, serverStatus);
    }

    /**
     * Startet das Spiel, wenn es noch nicht gestartet wurde und alle Kriterien dafuer erfuellt sind.
     * Stoppt das Spiel, wenn dieses laeuft.
     * Bei Fehlern werden Dialoge angezeigt.
     * Der Server muss gestartet sein, bevor diese Methode aufgerufen wird.
     */
    public void startStopGame() {
        assert isServerRunning() : ASSERT_SERVER_RUN_BEFORE_GAME;

        Window mainWindow = MainGUIController.getMainWindow();

        if (gameServerModel.isGameRunning()) {
            stopGame();
            MainGUIController.setStatus(MainGUIController.StatusType.DEFAULT, "Das Spiel wurde beendet!");
        } else {
            if (!startGame()) {
                // Fehlerpopup, da das Spiel nicht gestartet werden konnte
                DialogPopupFactory.getFactory().showErrorPopup(
                        mainWindow,
                        GAME_NOT_STARTED_MESSAGE,
                        DialogPopupFactory.LOCATION_CENTRE,
                        8.0);
                MainGUIController.setStatus(MainGUIController.StatusType.DEFAULT,
                        "Das Spiel konnte nicht gestartet werden!");
            } else {
                MainGUIController.setStatus(MainGUIController.StatusType.DEFAULT, "Das Spiel wurde gestartet!");
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

        // Nicht editierbares Feld initialisieren
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
        ObservableList<ClientDto> loggedClients =
                this.listLoggedClients.getItems();
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

        //TODO Kriterium hängt eigentlich von der ANzahl der Karten ab und jeder sollte mindestens
        // 6 Karten bekommen
        if (this.listLoggedClients.getItems().size() > 52) {
            this.setGameRunning(true);
            this.setBoxEditable(false);
            this.changeGameButton("tooltip.stop.game");
        } else started = false;

        return started;
    }

    /**
     * Startet den Server und passt die Toolbar an. Kann der Server nicht gestartet werden, wird eine
     * Exception geworfen.
     *
     * @see com.github.odinasen.durak.business.network.server.GameServer#startServer(int)
     */
    private void startServer()
            throws SystemException {
        // Server starten
        GameServer server = GameServer.getInstance();

        // Der Port wird ueber Databinding im Textfeld gesetzt
        server.startServer(this.gameServerModel.getPort().getValue());

        // GUI veraendern
        fieldServerPort.setEditable(false);
        this.changeServerButton("tooltip.stop.server");
        buttonLaunchGame.setVisible(true);
    }

    /**
     * Stoppt das Spiel und passt die Toolbar an.
     */
    private void stopGame() {
        this.setGameRunning(false);
        this.changeGameButton("tooltip.start.game");
        this.setBoxEditable(true);
    }

    /**
     * Benachrichtigt angemeldete Clients, stoppt den Server und passt die Toolbar und ein paar
     * Komponenten der Oberflaeche an.
     */
    private void stopServer() {
        // Clients benachrichtigen
        GameServer.getInstance().sendClientMessage(ClientMessageType.SERVER_SHUTDOWN);

        // Server stoppen
        GameServer.getInstance().stopServer();

        // GUI anpassen
        buttonLaunchGame.setVisible(false);
        this.changeServerButton("tooltip.server.start.server");
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
        this.changeButton(this.buttonLaunchGame, oldStyle, newStyle, tooltipKey);
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
        this.changeButton(this.buttonLaunchServer, oldStyle, newStyle, tooltipKey);
    }

    public void setGameRunning(boolean running) {
        this.gameServerModel.setGameRunning(this.isServerRunning() && running);
    }

    public boolean isServerRunning() {
        return GameServer.getInstance().isRunning();
    }
}