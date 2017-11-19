package com.github.odinasen.durak.gui.testfx;

import com.github.odinasen.durak.i18n.BundleStrings;
import com.github.odinasen.durak.resources.ResourceGetter;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Abstrakte TestFX-Klasse, die das FXML main_content laedt.
 */
public class AbstractMainContentGuiTest
        extends ApplicationTest {

    @Override
    public void start(Stage stage) {
        Parent sceneRoot = getRootNode();
        Scene scene = new Scene(sceneRoot, 100, 100);
        stage.setScene(scene);
        stage.show();
    }

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
