package com.github.odinasen.gui;

import com.github.odinasen.gui.server.ServerPanel;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;

import java.io.IOException;

public class Controller {
  @FXML private SplitPane mainSplitPane;

  /** Fuegt das ServerPanel zur Hauptoberflaeche hinzu. */
  public void openServerPanel() {
    final ServerPanel panel = new ServerPanel();

    try {
      mainSplitPane.getItems().add(0, panel.getContent());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
