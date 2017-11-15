package com.github.odinasen.durak;

import com.github.odinasen.durak.i18n.BundleStrings;
import com.github.odinasen.durak.resources.ResourceGetter;
import javafx.scene.Node;
import javafx.scene.Parent;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadui.testfx.GuiTest;
import org.loadui.testfx.categories.TestFX;
import org.loadui.testfx.exceptions.NoNodesVisibleException;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.loadui.testfx.Assertions.verifyThat;

@Category(TestFX.class)
public class ITDurakApplicationTest
        extends GuiTest {

    @Test(expected = NoNodesVisibleException.class)
    public void serverPanelInvisibleAtStart() {
        String serverPanel = "#serverPanel";
        find(serverPanel);
    }

    @Test
    public void serverPanelIsVisibleOnMenuclick() {
        String serverPanel = "#serverPanel";
        click("#menuConnection").click("#openHideServerPanelMenuItem");
        verifyThat(serverPanel, Node::isVisible);
    }

    @Test(expected = NoNodesVisibleException.class)
    public void serverPanelIsInvisibleAfterSecondMenuclick() {
        String serverPanel = "#serverPanel";
        click("#menuConnection").click("#openHideServerPanelMenuItem");
        verifyThat(serverPanel, Node::isVisible);
        click("#menuConnection").click("#openHideServerPanelMenuItem");
        find(serverPanel);
    }

    @Override
    protected Parent getRootNode() {
        try {
            Locale.Builder localeBuilder = new Locale.Builder();
            localeBuilder.setLanguageTag("de-de");
            Locale locale = localeBuilder.build();

            ResourceBundle bundle =
                    ResourceBundle.getBundle(BundleStrings.JAVAFX_BUNDLE_NAME, locale);
            return ResourceGetter.loadFXMLPanel("main_content", bundle);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}