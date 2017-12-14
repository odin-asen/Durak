package com.github.odinasen.durak.gui.server;

import com.github.odinasen.durak.ApplicationStartParameter;
import com.github.odinasen.durak.gui.FXMLNames;
import com.github.odinasen.durak.gui.controller.JavaFXController;
import com.github.odinasen.durak.gui.server.model.ServerConfigurationModel;
import com.github.odinasen.durak.i18n.BundleStrings;
import com.github.odinasen.durak.util.Assert;
import com.github.odinasen.durak.util.LoggingUtility;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.converter.NumberStringConverter;

import java.util.Collections;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class ServerConfigurationController
        extends JavaFXController {
    private static final Logger LOGGER =
            LoggingUtility.getLogger(ServerConfigurationController.class);

    @FXML
    private ChoiceBox<InitialCard> initialCardsSelect;
    @FXML
    private Label disabledInitialCards;

    @FXML
    private TextField portField;

    @FXML
    private TextField passwordField;

    private ServerConfigurationModel configurationModel;

    public ServerConfigurationController() {
        super(
                FXMLNames.SERVER_CONFIGURATION,
                ResourceBundle.getBundle(BundleStrings.JAVAFX_BUNDLE_NAME, Locale.getDefault()));

        configurationModel = new ServerConfigurationModel();
    }

    @Override
    protected void initializePanel() {
        initInitialCardsComponents();

        portField.textProperty()
                 .bindBidirectional(configurationModel.getPort(), new NumberStringConverter());
        passwordField.textProperty().bindBidirectional(configurationModel.getPassword());

        initByStartParameters();
    }

    /**
     * Setzt Panel-Werte anhand der Start-Parameter der Anwendung
     */
    private void initByStartParameters() {
        ApplicationStartParameter startParameter = ApplicationStartParameter.getInstance();
        configurationModel.getPort().setValue(startParameter.getServerPort());
        configurationModel.getPassword().setValue(startParameter.getServerPwd());
    }

    @Override
    protected void assertNotNullComponents() {
        final String fxmlName = getFxmlName();
        Assert.assertFXElementNotNull(initialCardsSelect, "initialCardsSelect", fxmlName);
        Assert.assertFXElementNotNull(passwordField, "passwordField", fxmlName);
        Assert.assertFXElementNotNull(portField, "portField", fxmlName);
    }

    /**
     * Initialisiert das ChoiceBox- und Label-Objekt für die Anzahl der Karten
     */
    private void initInitialCardsComponents() {
        ObservableList<InitialCard> cards = initialCardsSelect.getItems();
        Collections.addAll(cards, InitialCard.values());
        initialCardsSelect.setValue(cards.get(0));
    }

    public void toggleEnableStateForStartedServer(boolean serverStarted) {
        if (serverStarted) {
            disabledInitialCards.setText(initialCardsSelect.getValue().toString());
            disabledInitialCards.setVisible(true);
            initialCardsSelect.setVisible(false);
        } else {
            //TODO test schreiben, der den Server trennt
            disabledInitialCards.setVisible(false);
            initialCardsSelect.setVisible(true);
        }

        portField.setEditable(!serverStarted);
        passwordField.setEditable(!serverStarted);
    }

    public int getInitialCards() {
        return initialCardsSelect.getValue().getNumberCards();
    }

    public int getPort() {
        return configurationModel.getPort().getValue();
    }

    public String getPassword() {
        return configurationModel.getPassword().getValue();
    }
}
