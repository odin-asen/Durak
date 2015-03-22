package com.github.odinasen.durak.gui.server;

import com.github.odinasen.durak.Assert;
import com.github.odinasen.durak.business.exception.SystemException;
import com.github.odinasen.durak.business.network.ClientMessageType;
import com.github.odinasen.durak.business.network.GameServer;
import com.github.odinasen.durak.dto.DTOClient;
import com.github.odinasen.durak.gui.FXMLNames;
import com.github.odinasen.durak.gui.MainGUIController;
import com.github.odinasen.durak.gui.notification.DialogPopupFactory;
import com.github.odinasen.durak.i18n.BundleStrings;
import com.github.odinasen.durak.i18n.I18nSupport;
import com.github.odinasen.durak.resources.ResourceGetter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Window;
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
  private static final String ASSERT_SERVER_RUN_BEFORE_GAME = "Server must run before trying to launch a game!";
  private static final String DEFAULT_PORT_STRING = "10000";
  private static final String GAME_NOT_STARTED_MESSAGE = "Muss mit Inhalt gefuellt werden.";

  @FXML private HBox hBoxPortCards;
  @FXML private TextField fieldServerPort;
  @FXML private ChoiceBox<InitialCard> boxInitialCards;
  private Label labelInitialCards;
  @FXML private Button buttonLaunchServer;
  @FXML private Button buttonLaunchGame;
  @FXML private ListView<DTOClient> listLoggedClients;

  private boolean gameRunning;

  private Window mainWindow;

  /****************/
  /* Constructors */
  /*     End      */
  /****************/

  /***********/
  /* Methods */

  /**
   * Laedt die server.fxml Datei und initialisiert Objektvariablen.
   * @return
   *      Den Wurzelknoten der fxml.
   * @throws IOException
   */
  public Parent initContent() throws IOException {
    /* fxml laden, initialize() wird dadurch auch aufgerufen. */
    ResourceBundle resourceBundle =
        ResourceBundle.getBundle(BundleStrings.JAVAFX_BUNDLE_NAME, Locale.getDefault());

    return ResourceGetter.loadFXMLPanel(FXMLNames.SERVER_PANEL, resourceBundle);
  }

  /**
   * Prueft, ob alle Objektvariablen, die in der fxml-Datei definiert sind, geladen wurden.
   * Initialisiert die Oberflaechen-Komponenten.
   */
  @FXML
  void initialize() {
    final String fxmlName = "server";

    Assert.assertFXElementNotNull(this.buttonLaunchGame, "buttonLaunchGame", fxmlName);
    Assert.assertFXElementNotNull(this.buttonLaunchServer, "buttonLaunchServer", fxmlName);
    Assert.assertFXElementNotNull(this.hBoxPortCards, "hBoxPortCards", fxmlName);
    Assert.assertFXElementNotNull(this.fieldServerPort, "fieldServerPort", fxmlName);
    Assert.assertFXElementNotNull(this.boxInitialCards, "boxInitialCards", fxmlName);
    Assert.assertFXElementNotNull(this.listLoggedClients, "listLoggedClients", fxmlName);

    //==============================================================================================
    initListView();
    this.changeGameButton("tooltip.start.game");
    this.changeServerButton("tooltip.server.start.server");
    initInitialCardsComponents();
    this.fieldServerPort.setText(DEFAULT_PORT_STRING);
  }

  private void initListView() {
    ObservableList<DTOClient> clients = FXCollections.observableArrayList(new DTOClient(0));
    for (int i = 0; i < 10; i++) {
      clients.add(new DTOClient(i+1));
    }
    listLoggedClients.setItems(clients);
    listLoggedClients.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    listLoggedClients.setCellFactory(
        new Callback<ListView<DTOClient>, ListCell<DTOClient>>() {
          @Override
          public ListCell<DTOClient> call(ListView<DTOClient> listView) {
            return new ClientListCell(new EventHandler<ActionEvent>() {
              @Override
              public void handle(ActionEvent actionEvent) {
                removeSelectedClients();
              }
            });
          }
        });
  }

  /**
   * Startet den Server, wenn er noch nicht gestartet ist.
   * Stoppt den Server, wenn dieser laeuft.
   * Bei Fehlern werden Dialoge angezeigt.
   */
  public void startStopServer() {
    this.initMainWindow();

    String serverStatus;

    /* Laueft der Server? */
    if(this.isServerRunning()) {

      /* Ja, also Server stoppen. */
      if(this.isGameRunning()) {
        DialogPopupFactory.getFactory().makeDialog(mainWindow, "Halli hallo").show();
        this.stopGame();
      }
      this.stopServer();
      serverStatus = "Server wurde angehalten!";
    } else {

      /* Nein, also Server starten. */
      try {
        this.startServer();
        serverStatus = "Server läuft!";
      } catch (SystemException e) {
        /* Fehlerpopup, da der Server nicht gestartet werden konnte */
        DialogPopupFactory.getFactory().showErrorPopup(
            mainWindow, e.getMessage(), DialogPopupFactory.LOCATION_CENTRE, 8.0);
        serverStatus = "Serverstart ist fehlgeschlagen!";
      }
    }

    MainGUIController.setStatus(MainGUIController.StatusType.DEFAULT, serverStatus);
  }

  /**
   * Startet das Spiel, wenn es noch nicht gestartet wurde und alle Kriterien dafuer erfuellt sind.
   * Stoppt das Spiel, wenn dieses laeuft.
   * Bei Fehlern werden Dialoge angezeigt.
   * Der Server muss gestartet sein, bevor diese Methode aufgerufen wird.
   */
  public void startStopGame() {
    assert isServerRunning() : ASSERT_SERVER_RUN_BEFORE_GAME;

    this.initMainWindow();

    if(isGameRunning()) {
      stopGame();
      MainGUIController.setStatus(MainGUIController.StatusType.DEFAULT, "Das Spiel wurde beendet!");
    } else {
      if(!startGame()) {
        /* Fehlerpopup, da das Spiel nicht gestartet werden konnte */
        DialogPopupFactory.getFactory().showErrorPopup(this.mainWindow,
                                                       GAME_NOT_STARTED_MESSAGE,
                                                       DialogPopupFactory.LOCATION_CENTRE,
                                                       8.0);
        MainGUIController.setStatus(MainGUIController.StatusType.DEFAULT,
                                    "Das Spiel konnte nicht gestartet werden!");
      } else {
        MainGUIController.setStatus(MainGUIController.StatusType.DEFAULT,
                                    "Das Spiel wurde gestartet!");
      }
    }
  }

  /*   End   */
  /***********/

  /*******************/
  /* Private Methods */

  /**
   * Initialisiert das Hauptfenster, falls dies noch nicht gesetzt wurde und falls die Scene schon
   * geladen ist.
   */
  private void initMainWindow() {
    if (this.mainWindow == null)
      if (buttonLaunchGame.getScene() != null)
        this.mainWindow = buttonLaunchGame.getScene().getWindow();
  }

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

  /**
   * Loescht die uebergebenen Clients und gibt die Anzahl
   * der geloeschten Clients zurueck.
   */
  private int removeSelectedClients() {
    ObservableList<DTOClient> loggedClients =
        this.listLoggedClients.getItems();
    int before = loggedClients.size();

    final List<DTOClient> selectedClients =
        this.listLoggedClients.getSelectionModel().getSelectedItems();

    for (int i = selectedClients.size() - 1; i >= 0; i--) {
      this.listLoggedClients.getItems().remove(selectedClients.get(i));
    }

    return before - loggedClients.size();
  }

  /**
   * Startet das Spiel und passt die Toolbar an.
   * @return
   *    True, wenn das Spiel gestartet wurde, andernfalls false.
   */
  private boolean startGame() {
    boolean started = true;

    if(this.listLoggedClients.getItems().size() > 52) {
      this.setGameRunning(true);
      this.setBoxEditable(false);
      this.changeGameButton("tooltip.stop.game");
    } else started = false;

    return started;
  }

  /**
   * Startet den Server und passt die Toolbar an. Kann der Server nicht gestartet werden, wird eine
   * Exception geworfen.
   * @see com.github.odinasen.durak.business.network.GameServer#startServer()
   */
  private void startServer()
      throws SystemException {
    /* Server starten */
    GameServer server = GameServer.getInstance();

    int port = Integer.parseInt(this.fieldServerPort.getText());
    server.setPort(port);
    server.startServer();

    /* GUI veraendern */
    fieldServerPort.setEditable(false);
    this.changeServerButton("tooltip.stop.server");
    buttonLaunchGame.setVisible(true);
  }

  /** Stoppt das Spiel und passt die Toolbar an. */
  private void stopGame() {
    this.setGameRunning(false);
    this.changeGameButton("tooltip.start.game");
    this.setBoxEditable(true);
  }

  /**
   * Benachrichtigt angemeldete Clients, stoppt den Server und passt die Toolbar und ein paar
   * Komponenten der Oberflaeche an.
   */
  private void stopServer() {
    /* Clients benachrichtigen */
    GameServer.getInstance().sendClientMessage(ClientMessageType.SERVER_SHUTDOWN);

    /* Server stoppen */
    GameServer.getInstance().stopServer();

    /* GUI anpassen */
    buttonLaunchGame.setVisible(false);
    this.changeServerButton("tooltip.server.start.server");
    fieldServerPort.setEditable(true);
  }

  /** Aendert die Eigenschaften eines Buttons. */
  private void changeButton(Button button, String oldStyle, String newStyle, String tooltipKey) {
    button.getStyleClass().remove(oldStyle);
    button.getStyleClass().add(newStyle);

    Tooltip tooltip = button.getTooltip();
    if(tooltip == null) {
      tooltip = new Tooltip();
      button.setTooltip(tooltip);
    }
    tooltip.setText(I18nSupport.getValue(BundleStrings.JAVAFX, tooltipKey));
  }

  /** Aendert den {@link #buttonLaunchGame} */
  private void changeGameButton(String tooltipKey) {
    final String oldStyle;
    final String newStyle;
    if (isGameRunning()) {
      oldStyle = "startGameButton";
      newStyle = "stopGameButton";
    } else {
      oldStyle = "stopGameButton";
      newStyle = "startGameButton";
    }
    this.changeButton(this.buttonLaunchGame, oldStyle, newStyle, tooltipKey);
  }

  /** Aendert den {@link #buttonLaunchServer}. */
  private void changeServerButton(String tooltipKey) {
    final String oldStyle;
    final String newStyle;
    if (isServerRunning()) {
      oldStyle = "startServerButton";
      newStyle = "stopServerButton";
    } else {
      oldStyle = "stopServerButton";
      newStyle = "startServerButton";
    }
    this.changeButton(this.buttonLaunchServer, oldStyle, newStyle, tooltipKey);
  }

  /*       End       */
  /*******************/

  /*********************/
  /* Getter and Setter */

  public void setGameRunning(boolean running) {
    gameRunning = this.isServerRunning() && running;
  }

  public boolean isServerRunning() {
    return GameServer.getInstance().isRunning();
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