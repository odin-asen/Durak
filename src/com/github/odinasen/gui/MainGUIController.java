package com.github.odinasen.gui;

import com.github.odinasen.Assert;
import com.github.odinasen.LoggingUtility;
import com.github.odinasen.gui.server.ServerPanelController;
import com.github.odinasen.i18n.BundleStrings;
import com.github.odinasen.i18n.I18nSupport;
import com.github.odinasen.resources.ResourceGetter;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Diese Klasse sollte nur einmal erstellt werden. Statische Zugriffe auf Oberflaechen koennen erst
 * gemacht werden, wenn ein Objekt erstellt wird.
 */
public class MainGUIController {
  private static final Logger LOGGER = LoggingUtility.getLogger(ResourceGetter.class.getName());
  private static MainGUIController MAIN_CONTROLLER;

  @FXML
  private SplitPane mainSplitPane;
  @FXML
  private MenuItem openHideServerPanelMenuItem;
  @FXML
  private Label leftStatus;
  @FXML
  private Label rightStatus;

  private ServerPanelController serverPanelController;
  private Parent serverPanelContent;

  public MainGUIController() {
    serverPanelController = new ServerPanelController();
    if (MAIN_CONTROLLER == null)
      MAIN_CONTROLLER = this;
  }

  /***********/
  /* Methods */

  /**
   * Prueft, ob alle Objektvariablen, die in der fxml-Datei definiert sind, geladen wurden.
   * Initialisiert die Oberflaechen-Komponenten.
   */
  @FXML
  void initialize() {
    final String fxmlName = "server";

    Assert.assertFXElementNotNull(this.mainSplitPane, "mainSplitPane", fxmlName);
    Assert.assertFXElementNotNull(this.openHideServerPanelMenuItem,
                                  "openHideServerPanelMenuItem",
                                  fxmlName);

    //==============================================================================================
    openHideServerPanelMenuItem.setOnAction(new OpenHideServerPanelHandle());
  }

  public static void setStatus(StatusType type, String status) {
    MAIN_CONTROLLER.leftStatus.setText(status);
  }

  /*   End   */
  /***********/

  /**
   * ***************
   */
  /* Private Methods */
  private Parent getServerPanelContent() {
    try {
      return serverPanelController.initContent();
    } catch (IOException e) {
      LOGGER.log(Level.WARNING, "Cannot open server panel!\n", e);
    }

    return null;
  }

  /*       End       */
  /*******************/

  /**
   * *************
   */
  /* Inner classes */

  public enum StatusType {
    DEFAULT
  }

  /* MenuItem-Event Handles */
  private class OpenHideServerPanelHandle implements EventHandler<ActionEvent> {
    private boolean open;

    OpenHideServerPanelHandle() {
      this.open = true;
    }

    @Override
    public void handle(ActionEvent actionEvent) {
      final MenuItem menuItem = (MenuItem) actionEvent.getSource();

      if (serverPanelContent == null)
        serverPanelContent = getServerPanelContent();

      if (serverPanelContent != null) {
        if (this.open) {
          mainSplitPane.getItems().add(0, serverPanelContent);
          menuItem.setText(I18nSupport.getValue(BundleStrings.GUI, "menu.item.hide"));
        } else {
          mainSplitPane.getItems().remove(serverPanelContent);
          menuItem.setText(I18nSupport.getValue(BundleStrings.GUI, "menu.item.open"));
        }
        this.open = !this.open;
      }
    }
  }

  /*     End       */
  /*****************/
}

