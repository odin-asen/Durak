package com.github.odinasen.durak.gui.uielement;

public enum UIElement {
    MenuConnection("#menuConnection"),
    MenuItemOpenCloseServerPanel("#openHideServerPanelMenuItem"),
    MenuItemOpenCloseClientPanel("#openHideClientPanelMenuItem"),
    ClientPanel("#clientPanel"),
    ServerPanel("#serverPanel"),
    MainSplitPane("#mainSplitPane");

    private String id;

    UIElement(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
