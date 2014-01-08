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
  @FXML Button buttonLaunchServer;
  @FXML Button buttonLaunchGame;

  private int mPort;
  private InitialCard numberCards;

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

  public boolean startGame() {
    return false;
  }

  public boolean startServer() {
    return false;
  }

  public void stopGame() {

  }

  public void stopServer() {

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

  /*******************/
  /* Private Methods */
  /*       End       */
  /*******************/

  /*********************/
  /* Getter and Setter */

  public int getPort() {
    return mPort;
  }

  public void setPort(int port) {
    mPort = port;
  }

  public void setInitialCard(InitialCard numberCards) {
    this.numberCards = numberCards;
  }

  /*        End        */
  /*********************/

  /*****************/
  /* Inner classes */
  /*      End      */
  /*****************/
}
