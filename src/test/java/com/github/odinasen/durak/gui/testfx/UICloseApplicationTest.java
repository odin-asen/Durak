package com.github.odinasen.durak.gui.testfx;

import com.github.odinasen.durak.gui.testfx.uielement.UIElement;
import com.github.odinasen.durak.gui.testfx.uielement.UIElementGuiTest;
import com.github.odinasen.test.UITest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UITest.class)
public class UICloseApplicationTest
        extends UIElementGuiTest {

    @Test
    public void closeApplication() {
        click(UIElement.MenuConnection).click(UIElement.MenuItemCloseApplication);
    }
}
