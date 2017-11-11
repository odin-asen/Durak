package com.github.odinasen.durak.gui.server;

import com.github.odinasen.durak.dto.ClientDto;
import com.github.odinasen.durak.resources.ResourceGetter;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 11.02.14
 */
public class ClientListCell
        extends ListCell<ClientDto> {

    private MenuItem removeMenuItem = new MenuItem("Remove");
    //TODO Context Menü mit Umbenennungsfunktion ausstatten, nur der Name. Update an alle
    private ContextMenu contextMenu = new ContextMenu(removeMenuItem);
    private ImageView image = new ImageView();
    private final Image IMAGE_EYE = ResourceGetter.getUserIcon("user.spectator.eye");

    //TODO irgendwo im Server abfangen. Wenn JavaVM geschlossen wird, müssen alle Verbindungen zu den Clients geprüft werden und die Client liste updaten
    public ClientListCell(EventHandler<ActionEvent> removeItemHandler) {
        removeMenuItem.setOnAction(removeItemHandler);
    }

    @Override
    public void updateItem(ClientDto item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            setText(item.getName());
            if (item.isSpectator()) {
                this.image.setImage(IMAGE_EYE);
                setGraphic(this.image);
            }
            setTooltip(new Tooltip(item.getUuid()));
            setContextMenu(contextMenu);
        } else {
            clearEmptyCell();
        }
    }

    private void clearEmptyCell() {
        setText("");
    }
}
