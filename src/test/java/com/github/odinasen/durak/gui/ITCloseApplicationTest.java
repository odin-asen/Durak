package com.github.odinasen.durak.gui;

import com.github.odinasen.durak.gui.uielement.UIElement;
import com.github.odinasen.durak.gui.uielement.UIElementGuiTest;
import org.junit.Test;

public class ITCloseApplicationTest
        extends UIElementGuiTest {

    @Test
    public void closeApplication() {
        click(UIElement.MenuConnection).click(UIElement.MenuItemCloseApplication);
    }
}
