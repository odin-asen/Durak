package com.github.odinasen.durak;

import javafx.scene.Node;
import org.loadui.testfx.GuiTest;

public abstract class UIElementGuiTest
        extends GuiTest {

    public UIElementGuiTest click(UIElement element, javafx.scene.input.MouseButton... buttons) {
        return (UIElementGuiTest) click(element.getId(), buttons);
    }

    public static <T extends Node> T find(UIElement element) {
        return find(element.getId());
    }
}
