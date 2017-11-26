package com.github.odinasen.durak.gui;

import com.github.odinasen.durak.business.network.client.GameClient;
import com.github.odinasen.durak.business.network.server.GameServer;
import com.github.odinasen.durak.gui.uielement.UIElement;
import com.github.odinasen.durak.gui.uielement.UIElementGuiTest;
import javafx.scene.Node;
import javafx.scene.control.Button;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

public class ITConnectClientToServerTest
        extends UIElementGuiTest {

    @After
    public void tearDown() {
        GameClient.getInstance().disconnect();
        if (GameServer.getInstance().isRunning()) {
            GameServer.getInstance().stopServer();
        }
    }

    @Test
    public void startServerChangesServerButton() {
        openServerPanelAndStartServer();
    }

    private void openServerPanelAndStartServer() {
        click(UIElement.MenuConnection).click(UIElement.MenuItemOpenCloseServerPanel);
        UIElement serverButton = UIElement.StartServerButton;
        serverButton.verifyIsVisible();
        assertTrue(serverButton.hasStyleClass("startServerButton"));

        UIElement serverPortField = UIElement.ServerPortField;
        click(serverPortField).type("1000");

        click(serverButton);
        assertTrue(serverButton.hasStyleClass("stopServerButton"));

        assertTrue(GameServer.getInstance().isRunning());
    }

    @Test
    public void connectClientToServer() {
        openServerPanelAndStartServer();
        openClientPanelAndConnectToServer();
    }

    private void openClientPanelAndConnectToServer() {
        assertTrue("Server has not been started", GameServer.getInstance().isRunning());

        click(UIElement.MenuConnection).click(UIElement.MenuItemOpenCloseClientPanel);
        UIElement clientButton = UIElement.ConnectToServerButton;
        clientButton.verifyIsVisible();

        Node connectGraphic = ((Button)clientButton.getElement()).getGraphic();

        click(UIElement.ConnectionAddressField).type("localhost");
        click(UIElement.ConnectionPortField).type("1000");
        click(clientButton);

        Node disconnectGraphic = ((Button)clientButton.getElement()).getGraphic();

        assertNotSame(connectGraphic, disconnectGraphic);
    }

    @Test
    public void disconnectClientFromServer() {
        openServerPanelAndStartServer();
        openClientPanelAndConnectToServer();

        clickDisconnectButton();
        assertFalse(GameClient.getInstance().isConnected());
    }

    private void clickDisconnectButton() {
        assertTrue(
                "Client should be connected, before the disconnect button can be clicked",
                GameClient.getInstance().isConnected());

        UIElement disconnectButton = UIElement.DisconnectFromServerButton;
        disconnectButton.verifyIsVisible();

        Node disconnectGraphic = ((Button)disconnectButton.getElement()).getGraphic();

        click(disconnectButton);

        Node connectGraphic = ((Button)disconnectButton.getElement()).getGraphic();

        assertNotSame(connectGraphic, disconnectGraphic);
    }
}
