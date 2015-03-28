package com.github.odinasen.durak.gui;

import com.github.odinasen.durak.LoggingUtility;
import com.github.odinasen.durak.business.network.server.GameServer;
import com.github.odinasen.durak.i18n.BundleStrings;
import com.github.odinasen.durak.resources.ResourceGetter;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class DurakApplication extends Application implements Observer {
  private static final String TITLE = "Durak";

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
    ResourceBundle bundle = ResourceBundle.getBundle(BundleStrings.JAVAFX_BUNDLE_NAME,
                                                     Locale.getDefault());
    Parent root = ResourceGetter.loadFXMLPanel(FXMLNames.MAIN_PANEL, bundle);
    primaryStage.setTitle(TITLE);
    Scene scene = new Scene(root, 500, 500);
    primaryStage.setScene(scene);
    primaryStage.show();

    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
      @Override
      public void handle(WindowEvent windowEvent) {
        /* Server stoppen */
        GameServer server = GameServer.getInstance();
        if (server.isRunning())
          server.stopServer();
      }
    });
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