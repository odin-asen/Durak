package com.github.odinasen.gui.server;

import com.github.odinasen.dto.DTOClient;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;

/**
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 11.02.14
 */
public class ClientListCell extends ListCell<DTOClient> {

  private MenuItem removeMenuItem = new MenuItem("Remove");
  private ContextMenu contextMenu = new ContextMenu(removeMenuItem);

  /****************/
  /* Constructors */

  public ClientListCell(EventHandler<ActionEvent> removeItemHandler) {
    removeMenuItem.setOnAction(removeItemHandler);
  }

  /*     End      */
  /****************/

  /***********/
  /* Methods */

  @Override
  public void updateItem(DTOClient item, boolean empty) {
    super.updateItem(item, empty);
    if (item != null) {
      setText(Integer.toString(item.id));
      setContextMenu(contextMenu);
    }
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
