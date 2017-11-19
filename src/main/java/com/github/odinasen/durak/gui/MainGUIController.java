package com.github.odinasen.durak.gui;

import com.github.odinasen.durak.ApplicationStartParameter;
import com.github.odinasen.durak.business.network.server.GameServer;
import com.github.odinasen.durak.i18n.BundleStrings;
import com.github.odinasen.durak.i18n.I18nSupport;
import com.github.odinasen.durak.resources.ResourceGetter;
import com.github.odinasen.durak.util.Assert;
import com.github.odinasen.durak.util.LoggingUtility;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.util.logging.Logger;

/**
 * Diese Klasse sollte nur einmal erstellt werden. Statische Zugriffe auf Oberflaechen koennen erst
 * gemacht werden, wenn ein Objekt erstellt wird.
 */
public class MainGUIController {
    private static final Logger LOGGER = LoggingUtility.getLogger(ResourceGetter.class.getName());
    public static final String STYLE_CLASS_HIDDEN_DIVIDER = "hiddenDivider";

    private static MainGUIController mainController;

    @FXML
    private Parent root;

    @FXML
    private Menu menuConnection;
    @FXML
    private SplitPane mainSplitPane;
    @FXML
    private HBox serverPanel;
    @FXML
    private HBox clientPanel;
    @FXML
    private MenuItem openHideServerPanelMenuItem;
    @FXML
    private MenuItem openHideClientPanelMenuItem;
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

    /**
     * Prueft, ob alle Objektvariablen, die in der fxml-Datei definiert sind, geladen wurden.
     * Initialisiert die Oberflaechen-Komponenten.
     */
    @FXML
    void initialize() {
        assertFXMLElementsNotNull();

        clientPanel.managedProperty().bind(clientPanel.visibleProperty());
        serverPanel.managedProperty().bind(serverPanel.visibleProperty());

        setServerClientPanelsOpenActions();
        bindDividerStyleToServerClientPanelVisiblity();
        setCloseMenuAction();

        // Die Anwendung wird mit Startparametern initalisiert
        // TODO soll das irgendwie in ein Interface ausgelagert werden?
        // assertFXMLElementsNotNull, dann
        // initialise und dann
        // initStartByParameters?
        initByStartParameters();
    }

    /**
     * Prueft, ob FXML Element initialisiert sind und gibt eventuell assert-Nachrichten aus.
     */
    private void assertFXMLElementsNotNull() {
        final String fxmlName = "main_content";

        Assert.assertFXElementNotNull(this.mainSplitPane, "mainSplitPane", fxmlName);
        Assert.assertFXElementNotNull(this.closeMenuItem, "closeMenuItem", fxmlName);
        Assert.assertFXElementNotNull(
                this.openHideServerPanelMenuItem, "openHideServerPanelMenuItem", fxmlName);
        Assert.assertFXElementNotNull(
                this.openHideClientPanelMenuItem, "openHideClientPanelMenuItem", fxmlName);
    }

    private void setServerClientPanelsOpenActions() {
        OpenHidePanelHandle serverPanelHandle = new OpenHideServerPanelHandle();
        openHideServerPanelMenuItem.setOnAction(serverPanelHandle);

        OpenHidePanelHandle clientPanelHandle = new OpenHideClientPanelHandle();
        openHideClientPanelMenuItem.setOnAction(clientPanelHandle);
    }

    private void bindDividerStyleToServerClientPanelVisiblity() {
        SplitPane.Divider divider = mainSplitPane.getDividers().get(0);

        serverPanel.visibleProperty().addListener((observable, oldValue, newValue) -> {
            toggleSplitPaneDividerVisiblity(divider, newValue);
        });
        clientPanel.visibleProperty().addListener((observable, oldValue, newValue) -> {
            toggleSplitPaneDividerVisiblity(divider, newValue);
        });
    }

    private void toggleSplitPaneDividerVisiblity(SplitPane.Divider divider,
                                                 boolean visiblityValue) {
        ObservableList<String> styleClasses = mainSplitPane.getStyleClass();

        if (visiblityValue || serverPanel.isVisible() || clientPanel.isVisible()) {
            boolean containsHiddenStyleClass = styleClasses.contains(STYLE_CLASS_HIDDEN_DIVIDER);

            if (containsHiddenStyleClass) {
                styleClasses.removeAll(STYLE_CLASS_HIDDEN_DIVIDER);
            }

            divider.setPosition(1.0);
        } else {
            styleClasses.add(STYLE_CLASS_HIDDEN_DIVIDER);
            divider.setPosition(0.0);
        }
    }

    private void setCloseMenuAction() {
        closeMenuItem.setOnAction(actionEvent -> {
            Stage stage = ((Stage)root.getScene().getWindow());
            stage.close();
            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        });
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
            if (this.openHideClientPanelMenuItem != null) {
                this.openHideClientPanelMenuItem.fire();
            }
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
        @Override
        protected String getMenuItemHideText() {
            return I18nSupport.getValue(BundleStrings.JAVAFX, "menu.item.hide.server.panel");
        }

        @Override
        protected String getMenuItemOpenText() {
            if (GameServer.getInstance().isRunning()) {
                return I18nSupport.getValue(BundleStrings.JAVAFX, "menu.item.show.server.panel");
            } else {
                return I18nSupport.getValue(BundleStrings.JAVAFX, "menu.item.open.server.panel");
            }
        }

        @Override
        protected Parent getPanel() {
            return serverPanel;
        }
    }

    /**
     * Oeffnet und schliesst das Panel mit der sich ein Client mit einem Server verbinden kann.
     */
    private class OpenHideClientPanelHandle
            extends OpenHidePanelHandle {
        @Override
        protected String getMenuItemHideText() {
            return I18nSupport.getValue(BundleStrings.JAVAFX, "menu.item.hide.client.panel");
        }

        @Override
        protected String getMenuItemOpenText() {
            return I18nSupport.getValue(BundleStrings.JAVAFX, "menu.item.open.client.panel");
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

        OpenHidePanelHandle() {
            panel = getPanel();
        }

        @Override
        public void handle(ActionEvent actionEvent) {
            if (panel != null) {
                final MenuItem menuItem = (MenuItem) actionEvent.getSource();

                setMenuItemText(menuItem);
                togglePanelVisiblity();
            }
        }

        private void setMenuItemText(MenuItem menuItem) {
            if (panel.isVisible()) {
                menuItem.setText(getMenuItemOpenText());
            } else {
                menuItem.setText(getMenuItemHideText());
            }
        }

        private void togglePanelVisiblity() {
            boolean newPanelVisibility = !panel.isVisible();
            panel.setVisible(newPanelVisibility);
        }

        abstract protected String getMenuItemHideText();

        abstract protected String getMenuItemOpenText();

        abstract protected Parent getPanel();
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
}