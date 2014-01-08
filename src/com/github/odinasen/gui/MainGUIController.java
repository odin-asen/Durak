package com.github.odinasen.gui;

import com.github.odinasen.LoggingUtility;
import com.github.odinasen.gui.server.ServerPanel;
import com.github.odinasen.i18n.BundleStrings;
import com.github.odinasen.i18n.I18nSupport;
import com.github.odinasen.resources.ResourceGetter;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;

import java.io.IOException;
import java.util.logging.Logger;

public class MainGUIController {
  private static final Logger LOGGER = LoggingUtility.getLogger(ResourceGetter.class.getName());

  @FXML private SplitPane mainSplitPane;
  @FXML private MenuItem openHideServerPanelMenuItem;

  private ServerPanel serverPanel;
  private Parent serverPanelContent;

  public MainGUIController() {
    serverPanel = new ServerPanel();
  }

  @FXML
  void initialize() {
    assert mainSplitPane != null : "fx:id=\"mainSplitPane\" was not injected: check your FXML file 'main_content.fxml'.";
    assert openHideServerPanelMenuItem != null : "fx:id=\"openHideServerPanelMenuItem\" was not injected: check your FXML file 'main_content.fxml'.";

    openHideServerPanelMenuItem.setOnAction(new OpenHideServerPanelHandle());
  }

  private Parent getServerPanel() {
    try {
      return serverPanel.getContent();
    } catch (IOException e) {
      LOGGER.warning("Cannot open server panel!");
    }

    return null;
  }

  /*****************/
  /* Inner classes */

  /* MenuItem-Event Handles */
  private class OpenHideServerPanelHandle implements EventHandler<ActionEvent> {
    private boolean open;

    OpenHideServerPanelHandle() {
      open = true;
    }

    @Override
    public void handle(ActionEvent actionEvent) {
      MenuItem menuItem = (MenuItem) actionEvent.getSource();

      if(serverPanelContent == null)
        serverPanelContent = getServerPanel();

      if(serverPanelContent != null) {
        if(open) {
          mainSplitPane.getItems().add(0, serverPanelContent);
          menuItem.setText(I18nSupport.getValue(BundleStrings.GUI, "menu.item.hide"));
        } else {
          mainSplitPane.getItems().remove(serverPanelContent);
          menuItem.setText(I18nSupport.getValue(BundleStrings.GUI, "menu.item.open"));
        }
        open = !open;
      }
    }
  }

  /*     End       */
  /*****************/
}

