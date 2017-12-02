package com.github.odinasen.durak.gui.testfx.uielement;

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
    MainSplitPane("#mainSplitPane"),
    StartServerButton("#buttonLaunchServer"),
    StopServerButton("#buttonLaunchServer"),
    ServerPortField("#fieldServerPort"),
    StatusLabel("#leftStatus"),
    ConnectToServerButton("#buttonConnectDisconnect"),
    DisconnectFromServerButton("#buttonConnectDisconnect"),
    ConnectionAddressField("#fieldServerAddress"),
    ConnectionPortField("#clientConnectionPortField"),
    ConnectionNameField("#fieldLoginName");


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

    public Node getElement() {
        NodeFinder nodeFinder = FxService.serviceContext().getNodeFinder();
        NodeQuery nodeQuery = nodeFinder.lookup(getIdSelector());

        Optional<Node> optional = nodeQuery.tryQuery();
        if (optional.isPresent()) {
            return optional.get();
        } else {
            String message = String.format("UI-Element mit Id '%s' konnte nicht gefunden werden.",
                                           getIdSelector());
            throw new ElementNotFoundException(message);
        }
    }

    public boolean hasStyleClass(String styleClass) {
        boolean hasStyle = false;

        for (String style : getElement().getStyleClass()) {
            hasStyle = hasStyle || style.equals(styleClass);
        }

        return hasStyle;
    }
}