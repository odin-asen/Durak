package com.github.odinasen.gui;

import com.github.odinasen.LoggingUtility;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class DurakApplication extends Application implements Observer {
  public static final String BUNDLE_NAME = "com.github.odinasen.i18n.javafx";
  private static final String TITLE = "Durak";
  private static final String MAIN_FXML = "main_content.fxml";

  private GUIMode guiMode;

  public static void main(String[] args) {
    /* init logging class */
    LoggingUtility.setFirstTimeLoggingFile(System.getProperty("user.dir")
        + System.getProperty("file.separator") + "clientLog.txt");

    launch(args);
  }

  /****************/
  /* Constructors */
  /*     End      */
  /****************/

  /***********/
  /* Methods */

  @Override
  public void start(Stage primaryStage) throws Exception {
    ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault());
    Parent root = FXMLLoader.load(getClass().getResource(MAIN_FXML), bundle);
    primaryStage.setTitle(TITLE);
    Scene scene = new Scene(root, 500, 500);
    scene.getStylesheets().add(this.getClass().getResource("/resources/common.css").toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  @Override
  public void update(Observable o, Object arg) {

  }

  /*   End   */
  /***********/

  /*******************/
  /* Private Methods */

  private void refreshHand() {

  }

  private void refreshOpponents() {

  }

  private void refreshTable() {

  }

  /*       End       */
  /*******************/

  /*********************/
  /* Getter and Setter */

  /** Setzt den Modus fuer die Oberflache.
   * Moegliche Werte sind statische Felder dieser Klasse. */
  public void setMode(GUIMode mode) {
    this.guiMode = mode;
  }

  /*        End        */
  /*********************/

  /*****************/
  /* Inner classes */

  public enum GUIMode {
    /** Oberflaeche ignoriert keine Netzwerk-Events. */
    START,
    /** Oberflaeche ist im Spielmodus. */
    PLAYER,
    /** Oberflaeche ist im Beobachtungsmodus. */
    OBSERVER
  }

  /*      End      */
  /*****************/
}