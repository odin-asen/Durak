package com.github.odinasen.resources;

import com.github.odinasen.LoggingUtility;
import com.github.odinasen.i18n.I18nSupport;

import javax.swing.*;
import java.net.URL;
import java.util.logging.Logger;

import static com.github.odinasen.i18n.BundleStrings.GUI;

/**
 * User: Timm Herrmann
 * Date: 03.10.12
 * Time: 19:59
 */
public class ResourceGetter {
  private static final String PICTURES_ROOT = "icons/";
  private static final String TOOLBAR_ROOT = PICTURES_ROOT + "toolbar/";

  private static final String PNG = "png";

  private static final Logger LOGGER = LoggingUtility.getLogger(ResourceGetter.class.getName());

  /***********/
  /* Methods */

  public static ImageIcon getToolbarIcon(String toolbarBundleKey, Object... params) {
    return getImage(
        TOOLBAR_ROOT+ I18nSupport.getValue(GUI, toolbarBundleKey, params), PNG);
  }

  /*   End   */
  /***********/

  /*******************/
  /* Private Methods */

  /* Loads an image from the specified path and adds the */
  /* surpassed extension if it is not null */
  private static ImageIcon getImage(String imageName, String extension) {
    ImageIcon image = null;

    if(extension == null) extension = "";
    else if(!extension.isEmpty()) extension = "."+extension;

    try {
      image = loadImage(imageName+extension);
    } catch (ResourceGetterException e) {
      LOGGER.warning(e.getMessage());
    }

    return image;
  }

  private static ImageIcon loadImage(String imageURL)
      throws ResourceGetterException {
    final ImageIcon image;

    final URL url = ResourceGetter.class.getResource(imageURL);
    if(url != null)
      image = new ImageIcon(url);
    else
      throw new ResourceGetterException("Could not find an URL for the path "+imageURL);

    return image;
  }

  /*       End       */
  /*******************/
}