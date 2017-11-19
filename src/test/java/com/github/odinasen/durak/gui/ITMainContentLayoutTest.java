package com.github.odinasen.durak.gui;

import com.github.odinasen.durak.gui.uielement.UIElement;
import com.github.odinasen.durak.gui.uielement.UIElementGuiTest;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.junit.Test;
import org.loadui.testfx.exceptions.NoNodesVisibleException;

import static com.github.odinasen.durak.gui.uielement.UIElementAssertions.verifyThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ITMainContentLayoutTest
        extends UIElementGuiTest {

    @Test
    public void splitPanelIsVisibleAtStart() {
        verifyThat(UIElement.MainSplitPane, Node::isVisible);
    }

    @Test(expected = NoNodesVisibleException.class)
    public void serverPanelInvisibleAtStart() {
        findServerPanel();
    }

    private void findServerPanel() {
        UIElement.ServerPanel.findElement();
    }

    @Test
    public void serverPanelIsVisibleOnMenuclick() {
        openServerPanel();
        verifyThat(UIElement.ServerPanel, Node::isVisible);
    }

    private void openServerPanel() {
        click(UIElement.MenuConnection).click(UIElement.MenuItemOpenCloseServerPanel);
    }

    @Test(expected = NoNodesVisibleException.class)
    public void serverPanelIsInvisibleAfterSecondMenuclick() {
        openServerPanel();
        verifyThat(UIElement.ServerPanel, Node::isVisible);

        openServerPanel();
        findServerPanel();
    }

    @Test
    public void serverPaneWidthBiggerThan30PixelIfVisible() {
        openServerPanel();
        verifyThat(UIElement.ServerPanel, node -> ((Pane)node).widthProperty().get() > 30.0);
    }

    @Test(expected = NoNodesVisibleException.class)
    public void clientPanelInvisibleAtStart() {
        findClientPanel();
    }

    private void findClientPanel() {
        UIElement.ClientPanel.findElement();
    }

    @Test
    public void clientPanelIsVisibleOnMenuclick() {
        openClientPanel();
        verifyThat(UIElement.ClientPanel, Node::isVisible);
    }

    private void openClientPanel() {
        click(UIElement.MenuConnection).click(UIElement.MenuItemOpenCloseClientPanel);
    }

    @Test(expected = NoNodesVisibleException.class)
    public void clientPanelIsInvisibleAfterSecondMenuclick() {
        openClientPanel();
        verifyThat(UIElement.ClientPanel, Node::isVisible);

        openClientPanel();
        findClientPanel();
    }

    @Test
    public void clientPaneWidthBiggerThan30PixelIfVisible() {
        openClientPanel();
        verifyThat(UIElement.ClientPanel, node -> ((Pane)node).widthProperty().get() > 30.0);
    }

    @Test(expected = NoNodesVisibleException.class)
    public void closeClientPanelMakesPanelInvisible() {
        openClientPanel();
        closeClientPanel();
        findClientPanel();
    }

    private void closeClientPanel() {
        click(UIElement.MenuConnection).click(UIElement.MenuItemOpenCloseClientPanel);
    }

    @Test
    public void closeClientPanelMustNotCloseVisibleServerPanel() {
        openServerPanelBeforeClientPanelAndVerifyVisiblity();

        Pane serverPanel = UIElement.ServerPanel.findElement();
        int serverPanelWidth = (int)serverPanel.getWidth();

        closeClientPanel();

        verifyThat(UIElement.ServerPanel, Node::isVisible);
        verifyServerPanelHasWidth(serverPanelWidth);
    }

    private void openServerPanelBeforeClientPanelAndVerifyVisiblity() {
        openServerPanel();
        openClientPanel();
        verifyThat(UIElement.ClientPanel, Node::isVisible);
        verifyThat(UIElement.ServerPanel, Node::isVisible);
    }

    private void openClientPanelBeforeServerPanelAndVerifyVisiblity() {
        openClientPanel();
        openServerPanel();
        verifyThat(UIElement.ClientPanel, Node::isVisible);
        verifyThat(UIElement.ServerPanel, Node::isVisible);
    }

    private void verifyServerPanelHasWidth(int serverPanelWidth) {
        verifyThat(
                UIElement.ServerPanel,
                node -> ((Pane)node).widthProperty().isEqualTo(serverPanelWidth).get());
    }

    @Test(expected = NoNodesVisibleException.class)
    public void closeServerPanelMakesPanelInvisible() {
        openServerPanel();
        closeServerPanel();
        findServerPanel();
    }

    private void closeServerPanel() {
        click(UIElement.MenuConnection).click(UIElement.MenuItemOpenCloseServerPanel);
    }

    @Test
    public void closeServerPanelMustNotCloseVisibleClientPanel() {
        openServerPanelBeforeClientPanelAndVerifyVisiblity();

        Pane clientPanel = UIElement.ClientPanel.findElement();
        int clientPanelWidth = (int)clientPanel.getWidth();

        closeServerPanel();

        verifyThat(UIElement.ClientPanel, Node::isVisible);
        verifyClientPanelHasWidth(clientPanelWidth);
    }

    private void verifyClientPanelHasWidth(int panelWidth) {
        verifyThat(
                UIElement.ClientPanel,
                node -> ((Pane)node).widthProperty().isEqualTo(panelWidth).get());
    }

    @Test
    public void clientPanelIsUpIfServerPanelIsInvisible() {
        openClientPanel();

        Pane serverPanel = UIElement.ServerPanel.getElement(stage.getScene());
        int serverPanelHeight = (int)serverPanel.heightProperty().get();
        assertTrue(serverPanelHeight + " != 0", serverPanelHeight == 0);
    }

    @Test
    public void serverPanelIsUpIfClientPanelIsInvisible() {
        openServerPanel();

        Pane serverPanel = UIElement.ServerPanel.getElement(stage.getScene());
        assertTrue(serverPanel.heightProperty().get() > 0);
    }

    @Test
    public void clientPanelIsBottomIfServerPanelIsOpenedFirst() {
        openServerPanelBeforeClientPanelAndVerifyVisiblity();

        verifyThatServerPanelIsAboveClientPanel();
    }

    private void verifyThatServerPanelIsAboveClientPanel() {
        Pane clientPanel = UIElement.ClientPanel.getElement(stage.getScene());
        assertTrue(clientPanel.heightProperty().get() > 0);

        Pane serverPanel = UIElement.ServerPanel.getElement(stage.getScene());
        assertTrue(serverPanel.heightProperty().get() > 0);

        int serverPanelX = (int)serverPanel.getLayoutX();
        int serverPanelY = (int)serverPanel.getLayoutY();
        int clientPanelX = (int)clientPanel.getLayoutX();
        int clientPanelY = (int)clientPanel.getLayoutY();
        assertEquals(serverPanelX, clientPanelX);
        assertTrue("ServerPanel ist nicht oberhalb des ClientPanel", serverPanelY < clientPanelY);
    }

    @Test
    public void clientPanelIsBottomIfServerPanelIsOpenedLast() {
        openClientPanelBeforeServerPanelAndVerifyVisiblity();

        verifyThatServerPanelIsAboveClientPanel();
    }
}
