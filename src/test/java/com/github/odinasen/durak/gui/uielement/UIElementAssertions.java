package com.github.odinasen.durak.gui.uielement;

import javafx.scene.Node;
import org.testfx.api.FxAssert;

import java.util.function.Predicate;

public class UIElementAssertions {
    public static <T extends Node> void verifyThat(UIElement element, Predicate<T> predicate) {
        FxAssert.verifyThat(element.getIdSelector(), predicate);
    }
}
