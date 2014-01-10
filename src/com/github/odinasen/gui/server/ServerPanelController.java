package com.github.odinasen.gui.server;

import com.github.odinasen.dto.DTOClient;
import com.github.odinasen.gui.DurakApplication;
import com.github.odinasen.i18n.BundleStrings;
import com.github.odinasen.i18n.I18nSupport;
import com.github.odinasen.resources.ResourceGetter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;

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
public class ServerPanelController {
  private static final String ASSERT_SERVER_GAME_IMPLICATION = "Game is running while server doesn't!";
  private static final String ASSERT_SERVER_RUN_BEFORE_GAME = "Server must run before trying to launch a game!";
  private static final String DEFAULT_PORT_STRING = "10000";

  @FXML private HBox hBoxPortCards;
  @FXML private TextField fieldServerPort;
  @FXML private ChoiceBox<InitialCard> boxInitialCards;
  private Label labelInitialCards;
  @FXML private Button buttonLaunchServer;
  @FXML private Button buttonLaunchGame;
  @FXML private ListView<DTOClient> listLoggedClients;

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
    assert listLoggedClients != null : getInjectionAssertMessage("listLoggedClients");

    initListView();
    changeButton(buttonLaunchGame, "toolbar.start.game", "tooltip.start.game");
    changeButton(buttonLaunchServer, "toolbar.start.server", "tooltip.start.server");
    initInitialCardsComponents();
    fieldServerPort.setText(DEFAULT_PORT_STRING);
  }

  private void initListView() {
    ObservableList<DTOClient> clients = FXCollections.observableArrayList(new DTOClient());
    for (int i = 0; i < 20; i++) {
      clients.add(new DTOClient());
    }
    listLoggedClients.setItems(clients);
    listLoggedClients.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    listLoggedClients.setCellFactory(
        new Callback<ListView<DTOClient>, ListCell<DTOClient>>() {
          @Override
          public ListCell<DTOClient> call(ListView<DTOClient> listView) {
            return new ClientCell();
          }
        });
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

  private String getAssertMessage(String methodName, String parameter, String mustNotBe) {
    return "Parameter \'"+parameter+"\' in method \'"+methodName+"\' must not be "+mustNotBe;
  }

  /**
   * Loescht die uebergebenen Clients und gibt die Anzahl
   * der geloeschten Clients zurueck.
   */
  private int removeClients(List<DTOClient> toRemove) {
    assert toRemove != null : getAssertMessage("removeClients", "toRemove", "null");

    ObservableList<DTOClient> loggedClients = listLoggedClients.getItems();
    int before = loggedClients.size();
    loggedClients.removeAll(toRemove);

    return before - loggedClients.size();
  }

  private boolean startGame() {
    boolean started = true;
    if(started) {
      setGameRunning(true);
      setBoxEditable(false);
      changeButton(buttonLaunchGame, "toolbar.stop.game", "tooltip.stop.game");
    }
    //TODO startet nur, wenn mehr als 2 SPieler angemeldet sind
    return started;
  }

  private boolean startServer() {
    boolean started = true;
    if(started) {
      setServerRunning(started);
      fieldServerPort.setEditable(false);
      changeButton(buttonLaunchServer, "toolbar.server.stop", "tooltip.stop.server");
      buttonLaunchGame.setVisible(true);
    }

    return started;
  }

  private void stopGame() {
    setGameRunning(false);
    changeButton(buttonLaunchGame, "toolbar.start.game", "tooltip.start.game");
    setBoxEditable(true);
  }

  private void stopServer() {
    setServerRunning(false);
    buttonLaunchGame.setVisible(false);
    changeButton(buttonLaunchServer, "toolbar.start.server", "tooltip.start.server");
    fieldServerPort.setEditable(true);
  }

  private void changeButton(Button button, String iconKey, String tooltipKey) {
    button.setGraphic(new ImageView(ResourceGetter.getToolbarIcon(iconKey)));
    Tooltip tooltip = button.getTooltip();
    if(tooltip == null) {
      tooltip = new Tooltip();
      button.setTooltip(tooltip);
    }
    tooltip.setText(I18nSupport.getValue(BundleStrings.GUI, tooltipKey));
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

  static class ClientCell extends ListCell<DTOClient> {
    @Override
    public void updateItem(DTOClient item, boolean empty) {
      super.updateItem(item, empty);
      if (item != null) {
        setText(item.toString());
      }
    }
  }

  /*      End      */
  /*****************/
}