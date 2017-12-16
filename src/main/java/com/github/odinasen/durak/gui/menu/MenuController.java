package com.github.odinasen.durak.gui.menu;

import com.github.odinasen.durak.ApplicationStartParameter;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class MenuController {

    @FXML
    private Menu menuConnection;

    @FXML
    private MenuItem openHideServerPanelMenuItem;

    @FXML
    private MenuItem openHideClientPanelMenuItem;

    @FXML
    private MenuItem closeMenuItem;

    @FXML
    void initialize() {
        initByStartParameters();
    }

    /**
     * Setzt Panel-Werte anhand der Start-Parameter der Anwendung
     */
    private void initByStartParameters() {
        ApplicationStartParameter startParameter = ApplicationStartParameter.getInstance();

        if (startParameter.canInitialStartServer()) {
            if (openHideServerPanelMenuItem != null) {
                openHideServerPanelMenuItem.fire();
            }
        }

        if (startParameter.canInitialConnectToServer()) {
            if (openHideClientPanelMenuItem != null) {
                openHideClientPanelMenuItem.fire();
            }
        }
    }

    public void setOpenHideServerAction(OpenHidePanelHandle actionHandle) {
        openHideServerPanelMenuItem.setOnAction(actionHandle);
    }

    public void setOpenHideClientAction(OpenHidePanelHandle actionHandle) {
        openHideClientPanelMenuItem.setOnAction(actionHandle);
    }

    public void setCloseAction(EventHandler<ActionEvent> actionEvent) {
        closeMenuItem.setOnAction(actionEvent);
    }
}
