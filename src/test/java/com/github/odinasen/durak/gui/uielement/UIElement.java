package com.github.odinasen.durak.gui.uielement;

import javafx.scene.Node;
import org.testfx.api.FxAssert;
import org.testfx.api.FxService;
import org.testfx.service.finder.NodeFinder;
import org.testfx.service.query.NodeQuery;

import java.util.Optional;

public enum UIElement {
    MenuConnection("#menuConnection"),
    MenuItemOpenCloseServerPanel("#openHideServerPanelMenuItem"),
    MenuItemOpenCloseClientPanel("#openHideClientPanelMenuItem"),
    MenuItemCloseApplication("#closeMenuItem"),
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

    public void verifyIsVisible() {
        FxAssert.verifyThat(getIdSelector(), Node::isVisible);
    }

    public void verifyIsInvisible() {
        FxAssert.verifyThat(getIdSelector(), node -> !node.isVisible());
    }

    public <T extends Node> T getElement() {
        NodeFinder nodeFinder = FxService.serviceContext().getNodeFinder();
        NodeQuery nodeQuery = nodeFinder.lookup(getIdSelector());

        Optional<Node> optional = nodeQuery.tryQuery();
        if (optional.isPresent()) {
            Node node = optional.get();
            return (T)node;
        } else {
            String message = String.format("UI-Element mit Id '%s' konnte nicht gefunden werden.",
                                           getIdSelector());
            throw new ElementNotFoundException(message);
        }
    }
}