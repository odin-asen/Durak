package com.github.odinasen.durak.gui.panels;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/**
 * Das ist das Panel fuer die Hand und Tischkarten. Der Benutzer kann ueber dieses Panel das
 * Spielgeschehen steuern (Karten nehmen, etc...).
 */
public class GamePanel extends Pane {

    public GamePanel() {
    final GridPane tablePanel = new GridPane();
    final HBox handCardPanel = new HBox();

    this.getChildren().add(tablePanel);
    this.getChildren().add(handCardPanel);
  }
}
