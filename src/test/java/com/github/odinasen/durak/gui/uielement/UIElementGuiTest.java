package com.github.odinasen.durak.gui.uielement;

import com.github.odinasen.durak.gui.testfx.AbstractMainContentGuiTest;

/**
 * Abstrakte Klasse, die es ermoeglicht TestFX Methoden mit UIElement Objekten auszufuehren.
 */
public abstract class UIElementGuiTest
        extends AbstractMainContentGuiTest {

    public UIElementGuiTest click(UIElement element, javafx.scene.input.MouseButton... buttons) {
        return (UIElementGuiTest)click(element.getIdSelector(), buttons);
    }
}
