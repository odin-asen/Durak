package com.github.odinasen.durak;

import com.github.odinasen.durak.i18n.BundleStrings;
import com.github.odinasen.durak.resources.ResourceGetter;
import javafx.scene.Node;
import javafx.scene.Parent;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadui.testfx.categories.TestFX;
import org.loadui.testfx.exceptions.NoNodesVisibleException;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.github.odinasen.durak.UIElementAssertions.verifyThat;

@Category(TestFX.class)
public class ITDurakApplicationTest
        extends UIElementGuiTest {

    @Test(expected = NoNodesVisibleException.class)
    public void clientPanelInvisibleAtStart() {
        find(UIElement.ClientPanel);
    }

    @Test
    public void clientPanelIsVisibleOnMenuclick() {
        click(UIElement.MenuConnection).click(UIElement.MenuItemOpenCloseClientPanel);
        verifyThat(UIElement.ClientPanel, Node::isVisible);
    }

    @Test(expected = NoNodesVisibleException.class)
    public void clientPanelIsInvisibleAfterSecondMenuclick() {
        click(UIElement.MenuConnection).click(UIElement.MenuItemOpenCloseClientPanel);
        verifyThat(UIElement.ClientPanel, Node::isVisible);
        click(UIElement.MenuConnection).click(UIElement.MenuItemOpenCloseClientPanel);
        find(UIElement.ClientPanel);
    }

    @Test(expected = NoNodesVisibleException.class)
    public void serverPanelInvisibleAtStart() {
        find(UIElement.ServerPanel);
    }

    @Test
    public void serverPanelIsVisibleOnMenuclick() {
        click(UIElement.MenuConnection).click(UIElement.MenuItemOpenCloseServerPanel);
        verifyThat(UIElement.ServerPanel, Node::isVisible);
    }

    @Test(expected = NoNodesVisibleException.class)
    public void serverPanelIsInvisibleAfterSecondMenuclick() {
        click(UIElement.MenuConnection).click(UIElement.MenuItemOpenCloseServerPanel);
        verifyThat(UIElement.ServerPanel, Node::isVisible);
        click(UIElement.MenuConnection).click(UIElement.MenuItemOpenCloseServerPanel);
        find(UIElement.ServerPanel);
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