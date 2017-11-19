package com.github.odinasen.durak.gui;

import com.github.odinasen.durak.gui.uielement.UIElement;
import com.github.odinasen.durak.gui.uielement.UIElementGuiTest;
import javafx.scene.Node;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadui.testfx.categories.TestFX;
import org.loadui.testfx.exceptions.NoNodesVisibleException;

import static com.github.odinasen.durak.gui.uielement.UIElementAssertions.verifyThat;

@Category(TestFX.class)
public class ITServerPanelTest
        extends UIElementGuiTest {

    @Test(expected = NoNodesVisibleException.class)
    public void serverPanelInvisibleAtStart() {
        findServerPanel();
    }

    @Test
    public void serverPanelIsVisibleOnMenuclick() {
        openServerPanel();
        verifyThat(UIElement.ServerPanel, Node::isVisible);
    }

    @Test(expected = NoNodesVisibleException.class)
    public void serverPanelIsInvisibleAfterSecondMenuclick() {
        openServerPanel();
        verifyThat(UIElement.ServerPanel, Node::isVisible);

        openServerPanel();
        findServerPanel();
    }

    private void findServerPanel() {
        find(UIElement.ServerPanel);
    }

    private void openServerPanel() {
        click(UIElement.MenuConnection).click(UIElement.MenuItemOpenCloseServerPanel);
    }
}