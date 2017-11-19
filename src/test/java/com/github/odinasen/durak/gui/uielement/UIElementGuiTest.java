package com.github.odinasen.durak.gui.uielement;

import com.github.odinasen.durak.gui.testfx.AbstractMainContentGuiTest;
import javafx.scene.input.MouseButton;

/**
 * Abstrakte Klasse, die es ermoeglicht TestFX Methoden mit UIElement Objekten auszufuehren.
 */
public abstract class UIElementGuiTest
        extends AbstractMainContentGuiTest {

    public UIElementGuiTest click(UIElement element, MouseButton... buttons) {
        return (UIElementGuiTest)clickOn(element.getIdSelector(), buttons);
    }
}
