package com.github.odinasen.gui.server;

import com.github.odinasen.dto.DTOClient;
import com.github.odinasen.gui.DurakApplication;
import com.github.odinasen.resources.ResourceGetter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 06.01.14
 */
public class ServerPanel {
  private static final String ASSERT_SERVER_GAME_IMPLICATION = "Game is running while server doesn't!";
  private static final String ASSERT_SERVER_RUN_BEFORE_GAME = "Server must run before trying to launch a game!";
  @FXML Button buttonLaunchServer;
  @FXML Button buttonLaunchGame;

  private int port;
  private InitialCard numberCards;

  /* Loeschen, wenn Server implementiert ist und information von Server holen */
  private boolean serverRunning;
  private boolean gameRunning;

  /****************/
  /* Constructors */
  /*     End      */
  /****************/

  /***********/
  /* Methods */

  public Parent getContent() throws IOException {
    return FXMLLoader.load(getClass().getResource("server.fxml"),
          ResourceBundle.getBundle(DurakApplication.BUNDLE_NAME, Locale.getDefault()));
  }

  @FXML
  void initialize() {
    assert buttonLaunchGame != null : "fx:id=\"buttonLaunchGame\" was not injected: check your FXML file 'server.fxml'.";
    assert buttonLaunchServer != null : "fx:id=\"buttonLaunchServer\" was not injected: check your FXML file 'server.fxml'.";

    buttonLaunchGame.setGraphic(new ImageView(ResourceGetter.getToolbarIcon("toolbar.game.start")));
    buttonLaunchServer.setGraphic(new ImageView(ResourceGetter.getToolbarIcon("toolbar.server.start")));
  }

  public void startStopServer() {
    if(isServerRunning()) {
      if(isGameRunning()) {
        //TODO DIalog fragt, ob gestoppt werden soll
        stopGame();
        buttonLaunchGame.setGraphic(new ImageView(ResourceGetter.getToolbarIcon("toolbar.game.start")));
      }
      stopServer();
      buttonLaunchGame.setVisible(false);
      buttonLaunchServer.setGraphic(new ImageView(ResourceGetter.getToolbarIcon("toolbar.server.start")));
    } else {
      if(startServer()) {
        buttonLaunchServer.setGraphic(new ImageView(ResourceGetter.getToolbarIcon("toolbar.server.stop")));
        buttonLaunchGame.setVisible(true);
      } else {
        //TODO Fehlerpopup
      }
    }
  }

  public void startStopGame() {
    assert isServerRunning() : ASSERT_SERVER_RUN_BEFORE_GAME;

    if(isGameRunning()) {
      stopGame();
      buttonLaunchGame.setGraphic(new ImageView(ResourceGetter.getToolbarIcon("toolbar.game.start")));
    } else {
      if(startGame()) {
        buttonLaunchGame.setGraphic(new ImageView(ResourceGetter.getToolbarIcon("toolbar.game.stop")));
      } else {
        //TODO Fehlerpopup
      }
    }
  }

  /**
   * Loescht die uebergebenen Clients und gibt die Anzahl
   * der geloeschten Clients zurueck.
   */
  public int removeClients(List<DTOClient> toRemove) {
    return 0;
  }

  /*   End   */
  /***********/

  //TODO Im Status soll angezeigt werden, wenn Server laeuft oder beendet wurde

  /*******************/
  /* Private Methods */

  private boolean startGame() {
    setGameRunning(true);
    //TODO startet nur, wenn mehr als 2 SPieler angemeldet sind
    return true;
  }

  private boolean startServer() {
    setServerRunning(true);
    return true;
  }

  private void stopGame() {
    setGameRunning(false);
  }

  private void stopServer() {
    setServerRunning(false);
  }

  /*       End       */
  /*******************/

  /*********************/
  /* Getter and Setter */

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public void setInitialCard(InitialCard numberCards) {
    this.numberCards = numberCards;
  }

  public void setServerRunning(boolean running) {
    serverRunning = running;
    if(!serverRunning)
      setGameRunning(false);

    assert (serverRunning || !gameRunning) : ASSERT_SERVER_GAME_IMPLICATION;
  }

  public void setGameRunning(boolean running) {
    gameRunning = serverRunning && running;
  }

  public boolean isServerRunning() {
    return serverRunning;
  }

  public boolean isGameRunning() {
    return gameRunning;
  }

  /*        End        */
  /*********************/

  /*****************/
  /* Inner classes */
  /*      End      */
  /*****************/
}
