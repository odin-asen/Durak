package com.github.odinasen.durak.gui.testfx;

import com.github.odinasen.durak.i18n.BundleStrings;
import com.github.odinasen.durak.resources.ResourceGetter;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.testfx.framework.junit.ApplicationTest;

import java.awt.*;
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
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dim = toolkit.getScreenSize();

        Scene scene = new Scene(sceneRoot, getScaledWindowDimension(dim.getWidth()),
                                getScaledWindowDimension(dim.getHeight()));
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

    private double getScaledWindowDimension(double size) {
        return 0.75 * size;
    }
}
