package com.github.odinasen.gui;

import com.github.odinasen.Assert;
import com.github.odinasen.LoggingUtility;
import com.github.odinasen.gui.client.ClientPanelController;
import com.github.odinasen.gui.server.ServerPanelController;
import com.github.odinasen.i18n.BundleStrings;
import com.github.odinasen.i18n.I18nSupport;
import com.github.odinasen.resources.ResourceGetter;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;

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

  @FXML
  private SplitPane mainSplitPane;
  @FXML
  private MenuItem openHideServerPanelMenuItem;
  @FXML
  private MenuItem openConnectToServerMenuItem;

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

  /***********/
  /* Methods */

  /**
   * Prueft, ob alle Objektvariablen, die in der fxml-Datei definiert sind, geladen wurden.
   * Initialisiert die Oberflaechen-Komponenten.
   */
  @FXML
  void initialize() {
    final String fxmlName = "main_content";

    Assert.assertFXElementNotNull(this.mainSplitPane, "mainSplitPane", fxmlName);
    Assert.assertFXElementNotNull(this.openHideServerPanelMenuItem,
                                  "openHideServerPanelMenuItem",
                                  fxmlName);
    Assert.assertFXElementNotNull(this.openConnectToServerMenuItem,
                                  "openConnectToServerMenuItem",
                                  fxmlName);
    //==============================================================================================
    openHideServerPanelMenuItem.setOnAction(new OpenHideServerPanelHandle());
    openConnectToServerMenuItem.setOnAction(new OpenConnectToServerHandle());
  }

  public static void setStatus(StatusType type, String status) {
    MAIN_CONTROLLER.leftStatus.setText(status);
  }

  public void reloadGUI() {
    ResourceBundle resourceBundle =
        ResourceBundle.getBundle(DurakApplication.BUNDLE_NAME, Locale.getDefault());

    try {
      FXMLLoader.load(getClass().getResource("main_content.fxml"), resourceBundle);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  /*   End   */
  /***********/

  /*******************/
  /* Private Methods */

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

  /*       End       */
  /*******************/

  /*****************/
  /* Inner classes */

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
      super(OpenHidePanelHandle.POSITION_BEGIN, "menu.item.hide.server.panel", "menu.item.open.server.panel");
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
      super(OpenHidePanelHandle.POSITION_END, "menu.item.hide.client.panel", "menu.item.open.client.panel");
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
    private String i18nHideText;
    private String i18nOpenText;

    protected OpenHidePanelHandle(String position, String i18nHideText, String i18nOpenText) {
      this.panelOpen = true;
      this.panelPosition = position;
      this.i18nHideText = i18nHideText;
      this.i18nOpenText = i18nOpenText;
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
            menuItem.setText(I18nSupport.getValue(BundleStrings.JAVAFX, this.i18nHideText));
          }
        } else {
          switchPanel = true;
          mainSplitPane.getItems().remove(this.panel);
          menuItem.setText(I18nSupport.getValue(BundleStrings.JAVAFX, this.i18nOpenText));
        }

        if (switchPanel)
          this.panelOpen = !this.panelOpen;
      }
    }

    /**
     * @return
     *    Das Panel, welches geoffnet und geschlossen wird.
     */
    abstract protected Parent getPanel();
  }

  /*     End       */
  /*****************/
}

