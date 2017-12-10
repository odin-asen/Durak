package com.github.odinasen.durak.gui.server;

import com.github.odinasen.durak.business.network.server.GameServer;
import com.github.odinasen.durak.gui.FXMLNames;
import com.github.odinasen.durak.gui.controller.JavaFXController;
import com.github.odinasen.durak.i18n.BundleStrings;
import com.github.odinasen.durak.util.Assert;
import com.github.odinasen.durak.util.LoggingUtility;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;

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

    public ServerConfigurationController() {
        super(
                FXMLNames.SERVER_CONFIGURATION,
                ResourceBundle.getBundle(BundleStrings.JAVAFX_BUNDLE_NAME, Locale.getDefault()));

        if (controller == null) {
            controller = this;
        }
    }

    @Override
    protected void initializePanel() {
        initInitialCardsComponents();
    }

    @Override
    protected void assertNotNullComponents() {
        final String fxmlName = getFxmlName();
        Assert.assertFXElementNotNull(boxInitialCards, "boxInitialCards", fxmlName);
    }

    /**
     * Abhaenging von Verbindungszustand und Spielzustand, werden Eingabekomponenten aktiviert
     * bzw. deaktiviert.
     */
    private void setEditableValueOnInputElements() {
        boolean enableServerFields = !GameServer.getInstance().isRunning();

        setBoxEditable(true);
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

    /**
     * Label und Choicebox tauschen Plaetze.
     */
    private void setBoxEditable(boolean editable) {
        // Per CSS aus und einblenden
        /*
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
        }*/
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
    }

    public int getInitialCards() {
        return boxInitialCards.getValue().getNumberCards();
    }
}
