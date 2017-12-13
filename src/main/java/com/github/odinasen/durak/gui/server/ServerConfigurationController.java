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
import javafx.scene.control.Control;
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

    private static ServerConfigurationController controller;

    @FXML
    private ChoiceBox<InitialCard> boxInitialCards;
    @FXML
    private Label disabledInitialCards;

    @FXML
    private TextField portField;

    private ServerConfigurationModel configurationModel;

    public ServerConfigurationController() {
        super(
                FXMLNames.SERVER_CONFIGURATION,
                ResourceBundle.getBundle(BundleStrings.JAVAFX_BUNDLE_NAME, Locale.getDefault()));

        configurationModel = new ServerConfigurationModel();

        // Instanz ist quatsch an dieser Stelle. Vom Parent Panel muss man irgendwie auf den Controller kommen
        if (controller == null) {
            controller = this;
        }
    }

    @Override
    protected void initializePanel() {
        initInitialCardsComponents();

        portField.textProperty()
                 .bindBidirectional(configurationModel.getPort(), new NumberStringConverter());
        initByStartParameters();
    }

    /**
     * Setzt Panel-Werte anhand der Start-Parameter der Anwendung
     */
    private void initByStartParameters() {
        ApplicationStartParameter startParameter = ApplicationStartParameter.getInstance();
        configurationModel.getPort().setValue(startParameter.getServerPort());
    }

    @Override
    protected void assertNotNullComponents() {
        final String fxmlName = getFxmlName();
        Assert.assertFXElementNotNull(boxInitialCards, "boxInitialCards", fxmlName);
        Assert.assertFXElementNotNull(portField, "portField", fxmlName);
    }

    /**
     * Initialisiert das ChoiceBox- und Label-Objekt für die Anzahl der Karten
     */
    private void initInitialCardsComponents() {
        ObservableList<InitialCard> cards = boxInitialCards.getItems();
        Collections.addAll(cards, InitialCard.values());
        boxInitialCards.setValue(cards.get(0));

        /* Nicht editierbares Feld initialisieren */
        /*labelInitialCards = new Label();
        copyHeightsAndWidths(boxInitialCards, labelInitialCards);
        GridPane.setMargin(labelInitialCards, GridPane.getMargin(boxInitialCards));
        GridPane.setColumnIndex(labelInitialCards, GridPane.getColumnIndex(boxInitialCards));
        GridPane.setRowIndex(labelInitialCards, GridPane.getRowIndex(boxInitialCards));
        */
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

    public static ServerConfigurationController getInstance() {
        return controller;
    }

    public void toggleEnableStateForStartedServer(boolean serverStarted) {
        if (serverStarted) {
            String selectedCardsCountText =
                    Integer.toString(boxInitialCards.getValue().getNumberCards());
            disabledInitialCards.setText(selectedCardsCountText);
            disabledInitialCards.setVisible(true);
            boxInitialCards.setVisible(false);
        } else {
            disabledInitialCards.setVisible(false);
            boxInitialCards.setVisible(true);
        }

        portField.setEditable(serverStarted);
    }

    public int getInitialCards() {
        return boxInitialCards.getValue().getNumberCards();
    }

    public int getPort() {
        return configurationModel.getPort().getValue();
    }
}
