package com.github.odinasen.i18n;

import com.github.odinasen.LoggingUtility;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * User: Timm Herrmann
 * Date: 20.11.12
 * Time: 20:35
 */
public class I18nSupport {
  private static final Logger LOGGER = LoggingUtility.getLogger(I18nSupport.class.getName());
  private static String BUNDLE_NAME = "";
  private static ResourceBundle BUNDLE;
  private static final String I18N_POINT = "common.i18n."; //NON-NLS

  public static String getValue(String bundleName, String key, Object... params) {
    try {
      final String value = getBundle(bundleName).getString(key);
      if(params.length > 0) return MessageFormat.format(value,  params);
      return value;
    } catch (Exception ex) {
      LOGGER.warning("Could not find key '" + key + "' in bundle '" + bundleName + "'");
      return "!" +bundleName+"/"+key+"!";
    }
  }

  private static ResourceBundle getBundle(String bundleName) {
    if(!BUNDLE_NAME.equals(I18N_POINT +bundleName)) {
      BUNDLE_NAME = I18N_POINT+bundleName;
      BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
    }
    return BUNDLE;
  }
}
