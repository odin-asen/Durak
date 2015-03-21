package com.github.odinasen.durak.gui.notification.dialog.message;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 20.02.14
 */
public class MessageDialogController {
  @FXML
  private Button dialogOkayButton;
  @FXML
  private Label messageContainer;

  private String message;

  /****************/
  /* Constructors */

  public MessageDialogController(String message) {
    this.message = message;
  }

  /*     End      */
  /****************/

  /***********/
  /* Methods */

  @FXML
  protected void initialize() {
    dialogOkayButton.setText("No schliim!");
    dialogOkayButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        handleButtons();
      }
    });
    messageContainer.setText(this.message);
  }

  protected void handleButtons() {
    System.out.println("Hallo Button");
  }

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
