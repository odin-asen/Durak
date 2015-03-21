package com.github.odinasen.durak.resources;

import com.github.odinasen.durak.LoggingUtility;
import com.github.odinasen.durak.i18n.I18nSupport;
import javafx.scene.image.Image;

import java.util.logging.Logger;

import static com.github.odinasen.durak.i18n.BundleStrings.RESOURCES_IMAGES;

/**
 * User: Timm Herrmann
 * Date: 03.10.12
 * Time: 19:59
 */
public class ResourceGetter {
  private static final String RESOURCES_ROOT = "com/github/odinasen/resources/";
  private static final String PICTURES_ROOT = RESOURCES_ROOT + "icons/";
  private static final String TOOLBAR_ROOT = PICTURES_ROOT + "toolbar/";

  private static final String PNG = "png";

  private static final Logger LOGGER = LoggingUtility.getLogger(ResourceGetter.class.getName());

  /***********/
  /* Methods */

  public static Image getToolbarIcon(String toolbarBundleKey, Object... params) {
    return getImage(
        TOOLBAR_ROOT+ I18nSupport.getValue(RESOURCES_IMAGES, toolbarBundleKey, params), PNG);
  }

  /*   End   */
  /***********/

  /*******************/
  /* Private Methods */

  /* Loads an image from the specified path and adds the */
  /* surpassed extension if it is not null */
  private static Image getImage(String imageName, String extension) {
    Image image = null;

    final String imageURL = imageName + getDotExtension(extension);

    try {
      image = new Image(imageURL);
    } catch (Exception e) {
      LOGGER.warning(e.getMessage()
          + "\nCould not find an URL for the path " + imageURL);
    }

    return image;
  }

  private static String getDotExtension(String extension) {
    if(extension == null) extension = "";
    else if(!extension.isEmpty()) extension = "."+extension;
    return extension;
  }

  /*       End       */
  /*******************/
}