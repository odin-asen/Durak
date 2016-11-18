package com.github.odinasen.durak.gui.server;

import com.github.odinasen.durak.dto.ClientDto;
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
public class ClientListCell
        extends ListCell<ClientDto> {

    private MenuItem removeMenuItem = new MenuItem("Remove");
    private ContextMenu contextMenu = new ContextMenu(removeMenuItem);


    public ClientListCell(EventHandler<ActionEvent> removeItemHandler) {
        removeMenuItem.setOnAction(removeItemHandler);
    }

    @Override
    public void updateItem(ClientDto item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            setText(item.uuid);
            setContextMenu(contextMenu);
        }
    }
}
