package com.github.odinasen.durak.gui.testfx;

import com.github.odinasen.durak.business.network.client.GameClient;
import com.github.odinasen.durak.business.network.server.GameServer;
import com.github.odinasen.durak.gui.testfx.uielement.UIElement;
import com.github.odinasen.durak.gui.testfx.uielement.UIElementGuiTest;
import com.github.odinasen.test.UITest;
import javafx.scene.Node;
import javafx.scene.control.Button;
import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.*;

@Category(UITest.class)
public class UIConnectClientToServerTest
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
        doubleClick(serverPortField).type("1000");

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

        doubleClick(UIElement.ConnectionNameField).type("Horst");
        doubleClick(UIElement.ConnectionAddressField).type("localhost");
        doubleClick(UIElement.ConnectionPortField).type("1000");
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
