package com.github.odinasen.durak.gui;

import com.github.odinasen.durak.business.network.server.GameServer;
import com.github.odinasen.durak.gui.menu.MenuController;
import com.github.odinasen.durak.gui.menu.OpenHidePanelHandle;
import com.github.odinasen.durak.i18n.BundleStrings;
import com.github.odinasen.durak.i18n.I18nSupport;
import com.github.odinasen.durak.resources.ResourceGetter;
import com.github.odinasen.durak.util.Assert;
import com.github.odinasen.durak.util.LoggingUtility;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
    private MenuController menuController;

    @FXML
    private Parent root;

    @FXML
    private SplitPane mainSplitPane;
    @FXML
    private HBox serverPanel;
    @FXML
    private HBox clientPanel;

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

        setMenuActions();

        bindDividerStyleToServerClientPanelVisiblity();
    }

    /**
     * Prueft, ob FXML Element initialisiert sind und gibt eventuell assert-Nachrichten aus.
     */
    private void assertFXMLElementsNotNull() {
        final String fxmlName = "main_content";

        Assert.assertFXElementNotNull(this.mainSplitPane, "mainSplitPane", fxmlName);
    }

    private void setMenuActions() {
        menuController.setOpenHideServerAction(new OpenHideServerPanelHandle());
        menuController.setOpenHideClientAction(new OpenHideClientPanelHandle());
        menuController.setCloseAction(actionEvent -> {
            Stage stage = ((Stage)root.getScene().getWindow());
            stage.close();
            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        });
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
