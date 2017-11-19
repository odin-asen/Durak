package com.github.odinasen.durak.gui.uielement;

import javafx.scene.Node;
import javafx.scene.Scene;
import org.loadui.testfx.GuiTest;

public enum UIElement {
    MenuConnection("#menuConnection"),
    MenuItemOpenCloseServerPanel("#openHideServerPanelMenuItem"),
    MenuItemOpenCloseClientPanel("#openHideClientPanelMenuItem"),
    ClientPanel("#clientPanel"),
    ServerPanel("#serverPanel"),
    MainSplitPane("#mainSplitPane");

    private String idSelector;

    UIElement(String idSelector) {
        this.idSelector = idSelector;
    }

    public String getIdSelector() {
        return idSelector;
    }

    public <T extends Node> T findElement() {
        return GuiTest.find(getIdSelector());
    }

    public boolean isVisible() {
        return findElement().isVisible();
    }

    public <T extends Node> T getElement(Scene scene) {
        return (T)scene.lookup(getIdSelector());
    }
}