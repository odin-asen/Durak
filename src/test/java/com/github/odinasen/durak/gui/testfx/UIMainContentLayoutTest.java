package com.github.odinasen.durak.gui.testfx;

import com.github.odinasen.durak.gui.testfx.uielement.UIElement;
import com.github.odinasen.durak.gui.testfx.uielement.UIElementGuiTest;
import com.github.odinasen.test.UITest;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static com.github.odinasen.durak.gui.testfx.uielement.UIElementAssertions.verifyThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(UITest.class)
public class UIMainContentLayoutTest
        extends UIElementGuiTest {

    @Test
    public void splitPanelIsVisibleAtStart() {
        verifyThat(UIElement.MainSplitPane, Node::isVisible);
    }

    @Test
    public void serverPanelInvisibleAtStart() {
        UIElement.ServerPanel.verifyIsInvisible();
    }

    @Test
    public void serverPanelIsVisibleOnMenuclick() {
        openServerPanel();
        UIElement.ServerPanel.verifyIsVisible();
    }

    private void openServerPanel() {
        click(UIElement.MenuConnection).click(UIElement.MenuItemOpenCloseServerPanel);
    }

    @Test
    public void serverPanelIsInvisibleAfterSecondMenuclick() {
        openServerPanel();
        UIElement.ServerPanel.verifyIsVisible();

        openServerPanel();
        UIElement.ServerPanel.verifyIsInvisible();
    }

    @Test
    public void serverPaneWidthBiggerThan30PixelIfVisible() {
        openServerPanel();
        verifyThat(UIElement.ServerPanel, node -> ((Pane)node).widthProperty().get() > 30.0);
    }

    @Test
    public void clientPanelInvisibleAtStart() {
        UIElement.ClientPanel.verifyIsInvisible();
    }

    @Test
    public void clientPanelIsVisibleOnMenuclick() {
        openClientPanel();
        UIElement.ClientPanel.verifyIsVisible();
    }

    private void openClientPanel() {
        click(UIElement.MenuConnection).click(UIElement.MenuItemOpenCloseClientPanel);
    }

    @Test
    public void clientPanelIsInvisibleAfterSecondMenuclick() {
        openClientPanel();
        UIElement.ClientPanel.verifyIsVisible();

        openClientPanel();
        UIElement.ClientPanel.verifyIsInvisible();
    }

    @Test
    public void clientPaneWidthBiggerThan30PixelIfVisible() {
        openClientPanel();
        verifyThat(UIElement.ClientPanel, node -> ((Pane)node).widthProperty().get() > 30.0);
    }

    @Test
    public void closeClientPanelMakesPanelInvisible() {
        openClientPanel();
        closeClientPanel();
        UIElement.ServerPanel.verifyIsInvisible();
    }

    private void closeClientPanel() {
        click(UIElement.MenuConnection).click(UIElement.MenuItemOpenCloseClientPanel);
    }

    @Test
    public void closeClientPanelMustNotCloseVisibleServerPanel() {
        openServerPanelBeforeClientPanelAndVerifyVisiblity();

        Pane serverPanel = (Pane)UIElement.ServerPanel.getElement();
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

    @Test
    public void closeServerPanelMakesPanelInvisible() {
        openServerPanel();
        closeServerPanel();
        UIElement.ServerPanel.verifyIsInvisible();
    }

    private void closeServerPanel() {
        click(UIElement.MenuConnection).click(UIElement.MenuItemOpenCloseServerPanel);
    }

    @Test
    public void closeServerPanelMustNotCloseVisibleClientPanel() {
        openServerPanelBeforeClientPanelAndVerifyVisiblity();

        Pane clientPanel = (Pane)UIElement.ClientPanel.getElement();
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

        Pane serverPanel = (Pane)UIElement.ServerPanel.getElement();
        int serverPanelHeight = (int)serverPanel.heightProperty().get();
        assertTrue(serverPanelHeight + " != 0", serverPanelHeight == 0);
    }

    @Test
    public void serverPanelIsUpIfClientPanelIsInvisible() {
        openServerPanel();

        Pane serverPanel = (Pane)UIElement.ServerPanel.getElement();
        assertTrue(serverPanel.heightProperty().get() > 0);
    }

    @Test
    public void clientPanelIsBottomIfServerPanelIsOpenedFirst() {
        openServerPanelBeforeClientPanelAndVerifyVisiblity();

        verifyThatServerPanelIsAboveClientPanel();
    }

    private void verifyThatServerPanelIsAboveClientPanel() {
        Pane clientPanel = (Pane)UIElement.ClientPanel.getElement();
        assertTrue(clientPanel.heightProperty().get() > 0);

        Pane serverPanel = (Pane)UIElement.ServerPanel.getElement();
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
