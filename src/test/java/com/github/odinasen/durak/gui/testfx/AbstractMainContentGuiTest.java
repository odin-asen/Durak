package com.github.odinasen.durak.gui.testfx;

import com.github.odinasen.durak.i18n.BundleStrings;
import com.github.odinasen.durak.resources.ResourceGetter;
import javafx.scene.Parent;
import org.loadui.testfx.GuiTest;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Abstrakte TestFX-Klasse, die das FXML main_content laedt.
 */
public class AbstractMainContentGuiTest
        extends GuiTest {
    @Override
    protected Parent getRootNode() {
        try {
            Locale locale = Locale.GERMAN;

            ResourceBundle bundle =
                    ResourceBundle.getBundle(BundleStrings.JAVAFX_BUNDLE_NAME, locale);
            return ResourceGetter.loadFXMLPanel("main_content", bundle);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
