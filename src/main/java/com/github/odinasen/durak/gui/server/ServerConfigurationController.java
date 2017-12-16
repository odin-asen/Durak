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
    private TextField portField;

    @FXML
    private TextField passwordField;

    @FXML
    private VariableAccessChoiceBoxPanel<InitialCard> initialCardsPanel;

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
        Assert.assertFXElementNotNull(initialCardsPanel, "initialCardsPanel", fxmlName);
        Assert.assertFXElementNotNull(passwordField, "passwordField", fxmlName);
        Assert.assertFXElementNotNull(portField, "portField", fxmlName);
    }

    /**
     * Initialisiert das ChoiceBox- und Label-Objekt für die Anzahl der Karten
     */
    private void initInitialCardsComponents() {
        ObservableList<InitialCard> cards = initialCardsPanel.getChoiceBoxItems();
        Collections.addAll(cards, InitialCard.values());
        initialCardsPanel.setValue(cards.get(0));
    }

    public void toggleEnableStateForStartedServer(boolean serverStarted) {
        initialCardsPanel.setChoiceBoxEditable(!serverStarted);
        portField.setEditable(!serverStarted);
        passwordField.setEditable(!serverStarted);
    }

    public int getInitialCards() {
        return initialCardsPanel.getValue().getNumberCards();
    }

    public int getPort() {
        return configurationModel.getPort().getValue();
    }

    public String getPassword() {
        return configurationModel.getPassword().getValue();
    }
}
