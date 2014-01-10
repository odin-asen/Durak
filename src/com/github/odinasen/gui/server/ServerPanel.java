package com.github.odinasen.gui.server;

import com.github.odinasen.dto.DTOClient;
import com.github.odinasen.gui.DurakApplication;
import com.github.odinasen.resources.ResourceGetter;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.io.IOException;
import java.util.Collections;
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
  private static final String DEFAULT_PORT_STRING = "10000";

  @FXML private HBox hBoxPortCards;
  @FXML private TextField fieldServerPort;
  @FXML private ChoiceBox<InitialCard> boxInitialCards;
  private Label labelInitialCards;
  @FXML private Button buttonLaunchServer;
  @FXML private Button buttonLaunchGame;

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
    assert buttonLaunchGame != null : getInjectionAssertMessage("buttonLaunchGame");
    assert buttonLaunchServer != null : getInjectionAssertMessage("buttonLaunchServer");
    assert hBoxPortCards != null : getInjectionAssertMessage("hBoxPortCards");
    assert fieldServerPort != null : getInjectionAssertMessage("fieldServerPort");
    assert boxInitialCards != null : getInjectionAssertMessage("boxInitialCards");

    buttonLaunchGame.setGraphic(new ImageView(ResourceGetter.getToolbarIcon("toolbar.game.start")));
    buttonLaunchServer.setGraphic(new ImageView(ResourceGetter.getToolbarIcon("toolbar.server.start")));

    initInitialCardsComponents();
    fieldServerPort.setText(DEFAULT_PORT_STRING);
  }

  public void startStopServer() {
    if(isServerRunning()) {
      if(isGameRunning()) {
        //TODO DIalog fragt, ob gestoppt werden soll
        stopGame();
      }
      stopServer();
    } else {
      if(!startServer()) {
        //TODO Fehlerpopup
      }
    }
  }

  public void startStopGame() {
    assert isServerRunning() : ASSERT_SERVER_RUN_BEFORE_GAME;

    if(isGameRunning()) {
      stopGame();
    } else {
      if(!startGame()) {
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

  /** Initialisiert das ChoiceBox- und Label-Objekt für die Anzahl der Karten */
  private void initInitialCardsComponents() {
    ObservableList<InitialCard> cards = boxInitialCards.getItems();
    Collections.addAll(cards, InitialCard.values());
    boxInitialCards.setValue(cards.get(0));

    /* Nicht editierbares Feld initialisieren */
    labelInitialCards = new Label();
    copyHeightsAndWidths(boxInitialCards, labelInitialCards);
    HBox.setHgrow(labelInitialCards, Priority.ALWAYS);
  }

  /**
   * Kopiert die Werte maxHeight, maxWidth, minHeight, minWidth,
   * prefHeight und preifWidth von einem Control-Objekt zum anderen.
   */
  private void copyHeightsAndWidths(Control from, Control to) {
    to.setMaxWidth(from.getMaxWidth());
    to.setMinWidth(from.getMinWidth());
    to.setMaxHeight(from.getMaxHeight());
    to.setMinHeight(from.getMinHeight());
    to.setPrefHeight(from.getPrefHeight());
    to.setPrefWidth(from.getPrefWidth());
  }

  /** Label und Choicebox tauschen Plaetze. */
  private void setBoxEditable(boolean editable) {
    final ObservableList<Node> children = hBoxPortCards.getChildren();

    if(editable) {
      children.remove(labelInitialCards);
      if(!children.contains(boxInitialCards)) {
        children.add(boxInitialCards);
      }
    } else {
      children.remove(boxInitialCards);
      if(!children.contains(labelInitialCards)) {
        children.add(labelInitialCards);
        labelInitialCards.setText(boxInitialCards.getValue().toString());
      }
    }
  }

  private String getInjectionAssertMessage(String name) {
    return "fx:id=\""+name+"\" was not injected: check your FXML file 'server.fxml'.";
  }

  private boolean startGame() {
    boolean started = true;
    if(started) {
      setGameRunning(true);
      setBoxEditable(false);
      buttonLaunchGame.setGraphic(new ImageView(ResourceGetter.getToolbarIcon("toolbar.game.stop")));
    }
    //TODO startet nur, wenn mehr als 2 SPieler angemeldet sind
    return started;
  }

  private boolean startServer() {
    boolean started = true;
    if(started) {
      setServerRunning(started);
      fieldServerPort.setEditable(false);
      buttonLaunchServer.setGraphic(new ImageView(ResourceGetter.getToolbarIcon("toolbar.server.stop")));
      buttonLaunchGame.setVisible(true);
    }

    return started;
  }

  private void stopGame() {
    setGameRunning(false);
    buttonLaunchGame.setGraphic(new ImageView(ResourceGetter.getToolbarIcon("toolbar.game.start")));
    setBoxEditable(true);
  }

  private void stopServer() {
    setServerRunning(false);
    buttonLaunchGame.setVisible(false);
    buttonLaunchServer.setGraphic(new ImageView(ResourceGetter.getToolbarIcon("toolbar.server.start")));
    fieldServerPort.setEditable(true);
  }

  /*       End       */
  /*******************/

  /*********************/
  /* Getter and Setter */

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
