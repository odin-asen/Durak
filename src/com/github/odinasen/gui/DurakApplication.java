package com.github.odinasen.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class DurakApplication extends Application {

  private static final String BUNDLE_NAME = "com.github.odinasen.i18n.gui";
  private static final String TITLE = "Hello World";
  private static final String MAIN_FXML = "main_content.fxml";

  @Override
    public void start(Stage primaryStage) throws Exception{
      ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault());
      Parent root = FXMLLoader.load(
          getClass().getResource(MAIN_FXML), bundle);
      primaryStage.setTitle(TITLE);
      primaryStage.setScene(new Scene(root, 300, 275));
      primaryStage.show();
    }

    public static void main(String[] args) {
      launch(args);
    }
}
