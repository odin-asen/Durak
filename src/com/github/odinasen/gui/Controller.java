package com.github.odinasen.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class Controller {
  @FXML private SplitPane mainSplitPane;

  /** Fuegt das ServerPanel zur Hauptoberflaeche hinzu. */
  public void openServerPanel() {
    try {
      Parent serverPanel = FXMLLoader.load(getClass().getResource("server/server.fxml"),
          ResourceBundle.getBundle(DurakApplication.BUNDLE_NAME, Locale.getDefault()));
      mainSplitPane.getItems().add(0, serverPanel);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
