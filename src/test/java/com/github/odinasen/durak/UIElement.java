package com.github.odinasen.durak;

enum UIElement {
    MenuConnection("#menuConnection"),
    MenuItemOpenCloseServerPanel("#openHideServerPanelMenuItem"),
    MenuItemOpenCloseClientPanel("#openHideClientPanelMenuItem"),
    ClientPanel("#clientPanel"),
    ServerPanel("#serverPanel");

    private String id;

    UIElement(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
