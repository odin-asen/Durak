package com.github.odinasen.durak.gui.client;

import com.github.odinasen.durak.ApplicationStartParameter;
import com.github.odinasen.durak.business.exception.GameClientCode;
import com.github.odinasen.durak.business.exception.SystemException;
import com.github.odinasen.durak.business.network.client.GameClient;
import com.github.odinasen.durak.dto.ClientDto;
import com.github.odinasen.durak.gui.FXMLNames;
import com.github.odinasen.durak.gui.MainGUIController;
import com.github.odinasen.durak.gui.client.model.ClientPanelModel;
import com.github.odinasen.durak.gui.controller.JavaFXController;
import com.github.odinasen.durak.gui.notification.DialogPopupFactory;
import com.github.odinasen.durak.i18n.BundleStrings;
import com.github.odinasen.durak.i18n.I18nSupport;
import com.github.odinasen.durak.resources.ResourceGetter;
import com.github.odinasen.durak.util.Assert;
import com.github.odinasen.durak.util.LoggingUtility;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.stage.Window;
import javafx.util.converter.NumberStringConverter;

import java.util.*;
import java.util.logging.Logger;

/**
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 06.01.14
 */
public class ClientPanelController
        extends JavaFXController
        implements Observer {
//  private static final String ASSERT_SERVER_RUN_BEFORE_GAME = "Server must run before trying to launch a game!";
//  private static final String DEFAULT_PORT_STRING = "10000";
//  private static final String GAME_NOT_STARTED_MESSAGE = "Muss mit Inhalt gefuellt werden.";

    private static final Logger LOGGER = LoggingUtility.getLogger(ClientPanelController.class.getName());

    @FXML
    private TextField fieldLoginName;

    @FXML
    private TextField fieldPassword;

    @FXML
    private TextField fieldServerPort;

    @FXML
    private TextField fieldServerAddress;

    @FXML
    private Button buttonConnectDisconnect;

    private ClientPanelModel clientModel;

    private boolean connected;

    private Window mainWindow;

    public ClientPanelController() {
        super(FXMLNames.CLIENT_PANEL, ResourceBundle.getBundle(BundleStrings.JAVAFX_BUNDLE_NAME, Locale.getDefault()));
        this.clientModel = new ClientPanelModel();
    }

    @Override
    protected void initializePanel() {
        setupConnectionButton(false);
        this.fieldLoginName.textProperty().bindBidirectional(this.clientModel.getNickname());
        this.fieldPassword.textProperty().bindBidirectional(this.clientModel.getPassword());
        this.fieldServerAddress.textProperty().bindBidirectional(this.clientModel.getServerAddress());
        this.fieldServerPort.textProperty().bindBidirectional(this.clientModel.getPort(), new NumberStringConverter());

        GameClient.getInstance().addObserver(this);

        // Wenn das Panel angezeigt wird, wird es initialisiert mit Startparametern
        initByStartParameters();
    }

    /**
     * Setzt Panel-Werte anhand der Start-Parameter der Anwendung
     */
    private void initByStartParameters() {
        ApplicationStartParameter startParameter = ApplicationStartParameter.getInstance();
        this.clientModel.getPassword().setValue(startParameter.getClientPwd());
        this.clientModel.getPort().setValue(startParameter.getClientPort());
        this.clientModel.getNickname().setValue(startParameter.getClientName());
        this.clientModel.getServerAddress().setValue(startParameter.getClientConnectionAddress());

        if (startParameter.canInitialConnectToServer()) {
            connectDisconnect();
        }
    }

    /**
     * Prueft, ob alle FXML-Felder verfuegbar sind.
     */
    @Override
    protected void assertNotNullComponents() {
        String fxmlName = getFxmlName();
        Assert.assertFXElementNotNull(this.buttonConnectDisconnect, "buttonConnectDisconnect", fxmlName);
        Assert.assertFXElementNotNull(this.fieldPassword, "fieldPassword", fxmlName);
        Assert.assertFXElementNotNull(this.fieldServerAddress, "fieldServerAddress", fxmlName);
        Assert.assertFXElementNotNull(this.fieldServerPort, "fieldServerPort", fxmlName);
    }

    /**
     * Verbindet den Client mit einem Server.
     * Die Verbindungsdaten werden aus dem Model gelesen.
     */
    public void connectDisconnect() {
        Window mainWindow = MainGUIController.getMainWindow();

        this.initMainWindow();

        String clientStatus = null;
        final GameClient gameClient = GameClient.getInstance();

        try {
            ClientDto clientDto = new ClientDto(UUID.randomUUID().toString(),
                                                this.clientModel.getNickname().getValue());
            boolean connected = gameClient.reconnect(this.clientModel.getServerAddress().getValue(),
                                                     this.clientModel.getPort().getValue(),
                                                     this.clientModel.getPassword().getValue(),
                                                     clientDto);
            if (connected) {
                clientStatus = I18nSupport.getValue(BundleStrings.USER_MESSAGES, "status.connected");
                LOGGER.fine("Logged successfully in");
                setConnected(true);
            } else {
                clientStatus = I18nSupport.getValue(BundleStrings.USER_MESSAGES, "status.connection.failed");
                setConnected(false);
            }
        } catch (SystemException e) {
            final String message = e.getMessage();
            if (e.getErrorCode() == GameClientCode.SERVER_NOT_FOUND) {
                LOGGER.info("Connect action failed: Server not found");
            } else if (e.getErrorCode() == GameClientCode.SERVICE_NOT_FOUND) {
                LOGGER.info("Connect action failed: Service not defined");
            }
            DialogPopupFactory.getFactory()
                              .showErrorPopup(mainWindow, e.getMessage(), DialogPopupFactory.LOCATION_CENTRE, 8.0);

        }

        // Hat diese Aktion einen Client-Status produziert?
        if (clientStatus != null) {
            MainGUIController.setStatus(MainGUIController.StatusType.DEFAULT, clientStatus);
        }
    }

    /**
     * Initialisiert das Hauptfenster, falls dies noch nicht gesetzt wurde und falls die Scene schon
     * geladen ist.
     */
    private void initMainWindow() {
        if (this.mainWindow == null) {
            if (buttonConnectDisconnect.getScene() != null) {
                this.mainWindow = buttonConnectDisconnect.getScene().getWindow();
            }
        }
    }

    /**
     * Aendert die Eigenschaften eines Buttons.
     */
    private void changeButton(Button button, String iconKey, String tooltipKey) {
        button.setGraphic(new ImageView(ResourceGetter.getToolbarIcon(iconKey)));
        Tooltip tooltip = button.getTooltip();
        if (tooltip == null) {
            tooltip = new Tooltip();
            button.setTooltip(tooltip);
        }
        tooltip.setText(I18nSupport.getValue(BundleStrings.JAVAFX, tooltipKey));
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        /* Button anpassen, wenn sich der Wert veraendert hat */
        if (this.connected != connected) {
            setupConnectionButton(connected);
        }
        this.connected = connected;
    }

    /**
     * Veraendert den Connection Button in Abhaengigkeit des Verbindungsstatus.
     * @param connected
     *  true - Eine Verbindung besteht, Connection Button wird zum "Verbindung trennen"-Button<br/>
     *  false - Keine Verbindung aufgebaut, Connection Button wird zum "Verbindung aufbauen"-Button
     */
    private void setupConnectionButton(boolean connected) {
        if (connected) {
            changeButton(this.buttonConnectDisconnect, "toolbar.network.close", "tooltip.client.disconnect.to.server");
        } else {
            changeButton(this.buttonConnectDisconnect, "toolbar.client.connect.to.server", "tooltip.client.connect.to.server");
        }
    }
    /**
     * Update fuer GameClient-Meldungen
     */
    @Override
    public void update(Observable o, Object arg) {
        /* Prueft die aktuellen Einstellungen des GameClients und aktualisiert die eigenen Werte */
        if (o instanceof GameClient) {
            GameClient client = (GameClient) o;
            if (this.isConnected() && !client.isConnected()) {
                String clientStatus = I18nSupport.getValue(BundleStrings.USER_MESSAGES, "status.has.been.disconnected");
                MainGUIController.setStatus(MainGUIController.StatusType.DEFAULT, clientStatus);
                this.setConnected(false);
            }
        }
    }
}