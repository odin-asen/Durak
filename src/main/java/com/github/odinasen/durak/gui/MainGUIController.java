package com.github.odinasen.durak.gui;

import com.github.odinasen.durak.Assert;
import com.github.odinasen.durak.LoggingUtility;
import com.github.odinasen.durak.business.network.server.GameServer;
import com.github.odinasen.durak.gui.client.ClientPanelController;
import com.github.odinasen.durak.gui.server.ServerPanelController;
import com.github.odinasen.durak.i18n.BundleStrings;
import com.github.odinasen.durak.i18n.I18nSupport;
import com.github.odinasen.durak.resources.ResourceGetter;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Diese Klasse sollte nur einmal erstellt werden. Statische Zugriffe auf Oberflaechen koennen erst
 * gemacht werden, wenn ein Objekt erstellt wird.
 */
public class MainGUIController {
    private static final Logger LOGGER = LoggingUtility.getLogger(ResourceGetter.class.getName());
    private static MainGUIController MAIN_CONTROLLER;

    /**
     * Das Hauptpanel der Anwendung.
     */
    @FXML
    private Parent root;

    @FXML
    private SplitPane mainSplitPane;
    @FXML
    private MenuItem openHideServerPanelMenuItem;
    @FXML
    private MenuItem openConnectToServerMenuItem;
    @FXML
    private MenuItem closeMenuItem;

    @FXML
    private Label leftStatus;
    @FXML
    private Label rightStatus;

    private ServerPanelController serverPanelController;

    private ClientPanelController clientPanelController;

    public MainGUIController() {
        serverPanelController = new ServerPanelController();
        clientPanelController = new ClientPanelController();

        if (MAIN_CONTROLLER == null)
            MAIN_CONTROLLER = this;
    }

    /**
     * Prueft, ob alle Objektvariablen, die in der fxml-Datei definiert sind, geladen wurden.
     * Initialisiert die Oberflaechen-Komponenten.
     */
    @FXML
    void initialize() {
        this.checkFXMLElements();
        //==============================================================================================
        openHideServerPanelMenuItem.setOnAction(new OpenHideServerPanelHandle());
        openConnectToServerMenuItem.setOnAction(new OpenConnectToServerHandle());
        closeMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Stage stage = ((Stage) root.getScene().getWindow());
                stage.close();
                stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
            }
        });
    }

    /**
     * Prueft, ob FXML Element initialisiert sind und gibt eventuell assert-Nachrichten aus.
     */
    private void checkFXMLElements() {
        final String fxmlName = "main_content";

        Assert.assertFXElementNotNull(this.mainSplitPane, "mainSplitPane", fxmlName);
        Assert.assertFXElementNotNull(this.closeMenuItem, "closeMenuItem", fxmlName);
        Assert.assertFXElementNotNull(this.openHideServerPanelMenuItem,
                "openHideServerPanelMenuItem",
                fxmlName);
        Assert.assertFXElementNotNull(this.openConnectToServerMenuItem,
                "openConnectToServerMenuItem",
                fxmlName);
    }

    public static void setStatus(StatusType type, String status) {
        MAIN_CONTROLLER.leftStatus.setText(status);
    }

    public void reloadGUI() {
        ResourceBundle resourceBundle =
                ResourceBundle.getBundle(BundleStrings.JAVAFX_BUNDLE_NAME, Locale.getDefault());

        try {
            ResourceGetter.loadFXMLPanel(FXMLNames.MAIN_PANEL, resourceBundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Parent getServerPanelContent() {
        try {
            return serverPanelController.initContent();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Cannot open server panel!\n", e);
        }

        return null;
    }

    private Parent getClientPanelContent() {
        try {
            return clientPanelController.initContent();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Cannot open client panel!\n", e);
        }

        return null;
    }

    public enum StatusType {
        DEFAULT
    }

    /* MenuItem-Event Handles */

    /**
     * Oeffnet und schliesst das Panel mit der ein Server gestartet und gestoppt werden kann.
     */
    private class OpenHideServerPanelHandle
            extends OpenHidePanelHandle {

        OpenHideServerPanelHandle() {
            super(OpenHidePanelHandle.POSITION_BEGIN);
        }

        @Override
        protected String getI18nHideText() {
            return "menu.item.hide.server.panel";
        }

        @Override
        protected String getI18nOpenText() {
            if (GameServer.getInstance().isRunning()) {
                return "menu.item.show.server.panel";
            } else {
                return "menu.item.open.server.panel";
            }
        }

        @Override
        protected Parent getPanel() {
            return getServerPanelContent();
        }
    }

    /**
     * Oeffnet und schliesst das Panel mit der sich ein Client mit einem Server verbinden kann.
     */
    private class OpenConnectToServerHandle
            extends OpenHidePanelHandle {

        OpenConnectToServerHandle() {
            super(OpenHidePanelHandle.POSITION_END);
        }

        @Override
        protected String getI18nHideText() {
            return "menu.item.hide.client.panel";
        }

        @Override
        protected String getI18nOpenText() {
            return "menu.item.open.client.panel";
        }

        @Override
        protected Parent getPanel() {
            return getClientPanelContent();
        }
    }

    /**
     * Fuegt ein Panel dem mainSplitPane hinzu bzw. entfernt es. Die Beschriftung des MenuItems, dass
     * diesen Handler verwendet, wird entsprechend geaendert.
     */
    private abstract class OpenHidePanelHandle
            implements EventHandler<ActionEvent> {

        private static final String POSITION_BEGIN = "BEGIN";
        private static final String POSITION_END = "END";

        private boolean panelOpen;
        private Parent panel;
        private String panelPosition;

        OpenHidePanelHandle(String position) {
            this.panelOpen = true;
            this.panelPosition = position;
        }

        @Override
        public void handle(ActionEvent actionEvent) {
            final MenuItem menuItem = (MenuItem) actionEvent.getSource();

            if (this.panel == null)
                this.panel = this.getPanel();

            boolean switchPanel;

            if (this.panel != null) {
                if (this.panelOpen) {
                    Integer posNumber;

                    if (this.panelPosition.equals(POSITION_BEGIN)) {
                        posNumber = 0;
                        switchPanel = true;
                    } else if (this.panelPosition.equals(POSITION_END)) {
                        posNumber = mainSplitPane.getItems().size();
                        switchPanel = true;
                    } else {
                        try {
                            posNumber = Integer.parseInt(this.panelPosition);
                            switchPanel = true;
                        } catch (NumberFormatException ex) {
                            LOGGER.warning("GUI Position wurde nicht interpretierbar übergeben.");
                            switchPanel = false;
                            posNumber = null;
                        }
                    }

                    if (switchPanel) {
                        mainSplitPane.getItems().add(posNumber, this.panel);
                        menuItem.setText(I18nSupport.getValue(BundleStrings.JAVAFX, this.getI18nHideText()));
                    }
                } else {
                    switchPanel = true;
                    mainSplitPane.getItems().remove(this.panel);
                    menuItem.setText(I18nSupport.getValue(BundleStrings.JAVAFX, this.getI18nOpenText()));
                }

                if (switchPanel)
                    this.panelOpen = !this.panelOpen;
            }
        }

        /** i18n Key fuer den Menuetext bei ausgeblendetem Panel */
        abstract protected String getI18nHideText();

        /** i18n Key fuer den Menuetext bei angezeigtem Panel */
        abstract protected String getI18nOpenText();

        /**
         * @return Das Panel, welches geoffnet und geschlossen wird.
         */
        abstract protected Parent getPanel();
    }
}

