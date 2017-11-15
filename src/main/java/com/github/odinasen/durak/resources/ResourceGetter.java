package com.github.odinasen.durak.resources;

import com.github.odinasen.durak.i18n.I18nSupport;
import com.github.odinasen.durak.util.LoggingUtility;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import static com.github.odinasen.durak.i18n.BundleStrings.RESOURCES_IMAGES;

/**
 * User: Timm Herrmann
 * Date: 03.10.12
 * Time: 19:59
 */
public class ResourceGetter {
    private static final String RESOURCES_ROOT = "";
    private static final String PICTURES_ROOT = RESOURCES_ROOT + "icons/";
    private static final String TOOLBAR_ROOT = PICTURES_ROOT + "toolbar/";
    private static final String USER_ROOT = PICTURES_ROOT + "user/";

    private static final String PNG = "png";

    private static final Logger LOGGER = LoggingUtility.getLogger(ResourceGetter.class.getName());

    public static Image getToolbarIcon(String toolbarBundleKey, Object... params) {
        return getImage(TOOLBAR_ROOT + I18nSupport.getValue(RESOURCES_IMAGES, toolbarBundleKey, params), PNG);
    }

    public static Image getUserIcon(String generalBundleKey, Object... params) {
        return getImage(USER_ROOT + I18nSupport.getValue(RESOURCES_IMAGES, generalBundleKey, params), PNG);
    }
    /* Loads an image from the specified path and adds the */
  /* surpassed extension if it is not null */
    private static Image getImage(String imageName, String extension) {
        Image image = null;

        final String imageURL = imageName + getDotExtension(extension);

        try {
            image = new Image(imageURL);
        } catch (Exception e) {
            LOGGER.warning(e.getMessage() + "\nCould not find an URL for the path " + imageURL);
        }

        return image;
    }

    private static String getDotExtension(String extension) {
        if (extension == null) {
            extension = "";
        } else if (!extension.isEmpty()) {
            extension = "." + extension;
        }
        return extension;
    }

    /**
     * Ist der Dateiname ohne Endung des zu ladenen Panels. Die zu ladene Datei muss aber fxml als
     * Endung haben.
     *
     * @param fileName ist der Name der zu ladenen Datei ohne Endung.
     * @param bundle   ist das ResourceBundle, das in der FXML-Datei verwendet wird.
     * @return das Panel, das aus der Datei gelesen wird.
     */
    public static Parent loadFXMLPanel(String fileName, ResourceBundle bundle) throws IOException {
        final String resourcePath = "gui/" + fileName + ".fxml";
        final ClassLoader classLoader = ResourceGetter.class.getClassLoader();
        if (classLoader != null) {
            URL resourceURL = classLoader.getResource(resourcePath);

            if (resourceURL != null) {
                if (bundle != null) {
                    return FXMLLoader.load(resourceURL, bundle);
                } else {
                    return FXMLLoader.load(resourceURL);
                }
            } else {
                throw new Error("Could not find resource in path '" + resourcePath + "'." +
                                "Make sure the file exists in classpath.");
            }
        } else {
            throw new Error("ClassLoader is null. Could not load classpath '" + resourcePath + "'.");
        }
    }

    public static Parent loadFXMLPanel(String fileName) throws IOException {
        return loadFXMLPanel(fileName, null);
    }
}