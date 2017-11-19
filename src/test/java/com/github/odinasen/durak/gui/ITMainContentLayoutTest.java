package com.github.odinasen.durak.gui;

import com.github.odinasen.durak.gui.uielement.UIElement;
import com.github.odinasen.durak.gui.uielement.UIElementGuiTest;
import javafx.scene.Node;
import org.junit.Test;

import static com.github.odinasen.durak.gui.uielement.UIElementAssertions.verifyThat;

public class ITMainContentLayoutTest
        extends UIElementGuiTest {

    @Test
    public void splitPanelIsVisibleAtStart() {
        verifyThat(UIElement.MainSplitPane, Node::isVisible);
    }
}
