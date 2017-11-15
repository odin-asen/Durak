package com.github.odinasen.durak.gui;

import com.github.odinasen.durak.ApplicationStartParameter;
import com.github.odinasen.durak.business.network.server.GameServer;
import com.github.odinasen.durak.i18n.BundleStrings;
import com.github.odinasen.durak.i18n.I18nSupport;
import com.github.odinasen.durak.resources.ResourceGetter;
import com.github.odinasen.durak.util.Assert;
import com.github.odinasen.durak.util.LoggingUtility;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Diese Klasse sollte nur einmal erstellt werden. Statische Zugriffe auf Oberflaechen koennen erst
 * gemacht werden, wenn ein Objekt erstellt wird.
 */
public class MainGUIController {
    private static final Logger LOGGER = LoggingUtility.getLogger(ResourceGetter.class.getName());
    private static MainGUIController mainController;

    /**
     * Das Hauptpanel der Anwendung.
     */
    @FXML
    private Parent root;
    @FXML
    private SplitPane mainSplitPane;
    @FXML
    private HBox serverPanel;
    @FXML
    private HBox clientPanel;
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

    public MainGUIController() {
        if (mainController == null) {
            mainController = this;
        }
    }

    public static void setStatus(StatusType type, final String status) {
        Platform.runLater(() -> mainController.leftStatus.setText(status));
    }

    /**
     * Liefert das Window-Objekt der Anwendung per Scene-Objekt eines GUI Elements ({@link #root})
     */
    public static Window getMainWindow() {
        if (mainController.root != null) {
            Scene scene = mainController.root.getScene();
            if (scene != null) {
                return scene.getWindow();
            }
        }

        return null;
    }

    /**
     * Prueft, ob alle Objektvariablen, die in der fxml-Datei definiert sind, geladen wurden.
     * Initialisiert die Oberflaechen-Komponenten.
     */
    @FXML
    void initialize() {
        checkFXMLElements();

        SplitPane.Divider leftDivider = mainSplitPane.getDividers().get(0);
        OpenHidePanelHandle serverPanelHandle =
                new OpenHideServerPanelHandle(leftDivider, 1.0, 0.0);
        openHideServerPanelMenuItem.setOnAction(serverPanelHandle);

        SplitPane.Divider rightDivider = mainSplitPane.getDividers().get(1);
        OpenHidePanelHandle clientPanelHandle =
                new OpenHideClientPanelHandle(rightDivider, 0.0, 1.0);
        openConnectToServerMenuItem.setOnAction(clientPanelHandle);

        closeMenuItem.setOnAction(actionEvent -> {
            Stage stage = ((Stage) root.getScene().getWindow());
            stage.close();
            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        });

        // Die Anwendung wird mit Startparametern initalisiert
        // TODO soll das irgendwie in ein Interface ausgelagert werden? checkFXMLElements, dann
        // initialise und dann
        // initStartByParameters?
        initByStartParameters();
    }

    /**
     * Setzt Panel-Werte anhand der Start-Parameter der Anwendung
     */
    private void initByStartParameters() {
        ApplicationStartParameter startParameter = ApplicationStartParameter.getInstance();

        if (startParameter.canInitialStartServer()) {
            if (this.openHideServerPanelMenuItem != null) {
                this.openHideServerPanelMenuItem.fire();
            }
        }

        if (startParameter.canInitialConnectToServer()) {
            if (this.openConnectToServerMenuItem != null) {
                this.openConnectToServerMenuItem.fire();
            }
        }
    }

    /**
     * Prueft, ob FXML Element initialisiert sind und gibt eventuell assert-Nachrichten aus.
     */
    private void checkFXMLElements() {
        final String fxmlName = "main_content";

        Assert.assertFXElementNotNull(this.mainSplitPane, "mainSplitPane", fxmlName);
        Assert.assertFXElementNotNull(this.closeMenuItem, "closeMenuItem", fxmlName);
        Assert.assertFXElementNotNull(
                this.openHideServerPanelMenuItem, "openHideServerPanelMenuItem", fxmlName);
        Assert.assertFXElementNotNull(
                this.openConnectToServerMenuItem, "openConnectToServerMenuItem", fxmlName);
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

    public enum StatusType {
        DEFAULT
    }

    /* MenuItem-Event Handles */

    /**
     * Oeffnet und schliesst das Panel mit der ein Server gestartet und gestoppt werden kann.
     */
    private class OpenHideServerPanelHandle
            extends OpenHidePanelHandle {

        OpenHideServerPanelHandle(SplitPane.Divider divider,
                                  double dividerOpenPosition,
                                  double dividerClosedPosition) {
            super(divider, dividerOpenPosition, dividerClosedPosition);
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
            return serverPanel;//getServerPanelContent();
        }
    }

    /**
     * Oeffnet und schliesst das Panel mit der sich ein Client mit einem Server verbinden kann.
     */
    private class OpenHideClientPanelHandle
            extends OpenHidePanelHandle {

        OpenHideClientPanelHandle(SplitPane.Divider divider,
                                  double dividerOpenPosition,
                                  double dividerClosedPosition) {
            super(divider, dividerOpenPosition, dividerClosedPosition);
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
            return clientPanel;//getClientPanelContent();
        }
    }

    /**
     * Fuegt ein Panel dem mainSplitPane hinzu bzw. entfernt es. Die Beschriftung des MenuItems,
     * dass
     * diesen Handler verwendet, wird entsprechend geaendert.
     */
    private abstract class OpenHidePanelHandle
            implements EventHandler<ActionEvent> {

        private Parent panel;
        private SplitPane.Divider divider;
        private double dividerOpenPosition;
        private double dividerClosedPosition;

        OpenHidePanelHandle(SplitPane.Divider divider,
                            double dividerOpenPosition,
                            double dividerClosedPosition) {
            this.panel = getPanel();
            this.divider = divider;
            this.dividerOpenPosition = dividerOpenPosition;
            this.dividerClosedPosition = dividerClosedPosition;
        }

        @Override
        public void handle(ActionEvent actionEvent) {
            if (panel == null) {
                setDividerToClosedPosition();
            } else {
                final MenuItem menuItem = (MenuItem) actionEvent.getSource();

                if (panel.isVisible()) {
                    panel.setVisible(false);
                    menuItem.setText(
                            I18nSupport.getValue(BundleStrings.JAVAFX, this.getI18nOpenText()));
                    setDividerToClosedPosition();
                } else {
                    panel.setVisible(true);
                    menuItem.setText(
                            I18nSupport.getValue(BundleStrings.JAVAFX, this.getI18nHideText()));
                    setDividerToOpenPosition();
                }
            }
        }

        private void setDividerToClosedPosition() {
            divider.setPosition(dividerClosedPosition);
        }

        private void setDividerToOpenPosition() {
            divider.setPosition(dividerOpenPosition);
        }

        /**
         * i18n Key fuer den Menuetext bei ausgeblendetem Panel
         */
        abstract protected String getI18nHideText();

        /**
         * i18n Key fuer den Menuetext bei angezeigtem Panel
         */
        abstract protected String getI18nOpenText();

        /**
         * @return Das Panel, welches geoffnet und geschlossen wird.
         */
        abstract protected Parent getPanel();
    }
}

