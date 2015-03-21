package com.github.odinasen.durak.gui.panels;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/**
 * Das ist das Panel fuer die Hand und Tischkarten. Der Benutzer kann ueber dieses Panel das
 * Spielgeschehen steuern (Karten nehmen, etc...).
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 06.01.14
 */
public class GamePanel extends Pane {
  /****************/
  /* Constructors */
  public GamePanel() {
    final GridPane tablePanel = new GridPane();
    final HBox handCardPanel = new HBox();

    this.getChildren().add(tablePanel);
    this.getChildren().add(handCardPanel);
  }
  /*     End      */
  /****************/

  /***********/
  /* Methods */
  /*   End   */
  /***********/

  /*******************/
  /* Private Methods */
  /*       End       */
  /*******************/

  /*********************/
  /* Getter and Setter */
  /*        End        */
  /*********************/

  /*****************/
  /* Inner classes */
  /*      End      */
  /*****************/
}
