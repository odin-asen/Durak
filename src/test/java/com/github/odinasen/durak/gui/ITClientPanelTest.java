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
public class ITClientPanelTest
        extends UIElementGuiTest {

    @Test(expected = NoNodesVisibleException.class)
    public void clientPanelInvisibleAtStart() {
        findClientPanel();
    }

    @Test
    public void clientPanelIsVisibleOnMenuclick() {
        openClientPanel();
        verifyThat(UIElement.ClientPanel, Node::isVisible);
    }

    @Test(expected = NoNodesVisibleException.class)
    public void clientPanelIsInvisibleAfterSecondMenuclick() {
        openClientPanel();
        verifyThat(UIElement.ClientPanel, Node::isVisible);

        openClientPanel();
        findClientPanel();
    }

    private void findClientPanel() {
        find(UIElement.ClientPanel);
    }

    private void openClientPanel() {
        click(UIElement.MenuConnection).click(UIElement.MenuItemOpenCloseClientPanel);
    }
}