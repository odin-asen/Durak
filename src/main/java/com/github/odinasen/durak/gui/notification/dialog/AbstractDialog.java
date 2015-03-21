package com.github.odinasen.durak.gui.notification.dialog;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

abstract public class AbstractDialog extends Stage {
  private static final String BUNDLE_NAME = "com.github.odinasen.i18n.gui";

  /**
   * Erstellt das Skelett eines Bestaetigungsdialogs.
   */
  public AbstractDialog(String fxml, Object controller) throws IOException {
    super(StageStyle.TRANSPARENT);

    initModality(Modality.WINDOW_MODAL);

    final ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault());
    final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml), bundle);
    fxmlLoader.setController(controller);

    final Parent root = (Parent) fxmlLoader.load();

    final Scene scene = createScene(root, true);
    scene.getRoot().getStyleClass().add("modal-dialog");
    scene.getStylesheets().add(getClass().getResource("modal-dialog.css").toExternalForm());

    setScene(scene);
  }

  private Scene createScene(Parent root, boolean transparent) {
    if(transparent) {
      return new Scene(root, Color.TRANSPARENT);
    } else {
      return new Scene(root);
    }
  }
}