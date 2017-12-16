package com.github.odinasen.durak.gui.menu;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;

/**
 * Fuegt ein Panel dem mainSplitPane hinzu bzw. entfernt es. Die Beschriftung des MenuItems,
 * dass
 * diesen Handler verwendet, wird entsprechend geaendert.
 */
public abstract class OpenHidePanelHandle
        implements EventHandler<ActionEvent> {

    private Parent panel;

    public OpenHidePanelHandle() {
        panel = getPanel();
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        if (panel != null) {
            final MenuItem menuItem = (MenuItem)actionEvent.getSource();

            setMenuItemText(menuItem);
            togglePanelVisiblity();
        }
    }

    private void setMenuItemText(MenuItem menuItem) {
        if (panel.isVisible()) {
            menuItem.setText(getMenuItemOpenText());
        } else {
            menuItem.setText(getMenuItemHideText());
        }
    }

    private void togglePanelVisiblity() {
        boolean newPanelVisibility = !panel.isVisible();
        panel.setVisible(newPanelVisibility);
    }

    abstract protected String getMenuItemHideText();

    abstract protected String getMenuItemOpenText();

    abstract protected Parent getPanel();
}
