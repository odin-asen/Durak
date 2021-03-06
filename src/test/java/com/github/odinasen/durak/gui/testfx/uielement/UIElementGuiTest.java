package com.github.odinasen.durak.gui.testfx.uielement;

import com.github.odinasen.durak.gui.testfx.AbstractMainContentGuiTest;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import org.testfx.robot.Motion;

/**
 * Abstrakte Klasse, die es ermoeglicht TestFX Methoden mit UIElement Objekten auszufuehren.
 */
public abstract class UIElementGuiTest
        extends AbstractMainContentGuiTest {

    public UIElementGuiTest click(UIElement element, MouseButton... buttons) {
        return (UIElementGuiTest)clickOn(element.getIdSelector(), buttons);
    }

    public UIElementGuiTest doubleClick(UIElement element, MouseButton... buttons) {
        return (UIElementGuiTest)doubleClickOn(element.getIdSelector(), Motion.DEFAULT, buttons);
    }

    public void type(String text) {
        String upperCaseText = text.toUpperCase();
        KeyCode[] numberKeyCodes = new KeyCode[upperCaseText.length()];
        for (int index = 0; index < upperCaseText.length(); index++) {
            String character = upperCaseText.substring(index, index + 1);
            if (character.equals(".")) {
                numberKeyCodes[index] = KeyCode.PERIOD;
            } else {
                numberKeyCodes[index] = KeyCode.getKeyCode(character);
            }

        }

        type(numberKeyCodes);
    }
}
