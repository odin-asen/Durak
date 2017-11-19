package com.github.odinasen.durak.gui.uielement;

import javafx.scene.Node;
import org.loadui.testfx.Assertions;

import java.util.function.Predicate;

public class UIElementAssertions
        extends Assertions {
    public static <T extends Node> void verifyThat(UIElement element, Predicate<T> predicate) {
        verifyThat(element.getId(), predicate::test);
    }
}
