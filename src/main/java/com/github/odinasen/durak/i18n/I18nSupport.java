package com.github.odinasen.durak.i18n;

import com.github.odinasen.durak.business.exception.ErrorCode;
import com.github.odinasen.durak.util.LoggingUtility;

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
  private static final String I18N_POINT = "i18n.";
  private static final String EXCEPTIONS_DIR = "exceptions.";

  /**
   * Gibt eine Exception-Nachricht fuer einen Fehlercode zurueck.
   * @param errorCode
   *          ist der Fehlercode.
   * @return
   *    Einen Text zum Fehlercode. Wenn es dazu keinen Text gibt, werden die Klasse und die
   *    uebergebenen Parameter zurueckgegeben.
   */
  public static String getException(ErrorCode errorCode, Object... params) {
    final String className = errorCode.getClass().getSimpleName();
    final String key = "" + errorCode.getNumber();
    final String bundleName = EXCEPTIONS_DIR + className;
    try {
      return getBundle(bundleName).getString(key);
    } catch (Exception ex) {
      final StringBuilder builder = new StringBuilder(256);
      builder.append("Exception: ").append(errorCode.getClass().getName()).append('\n');
      for (int index = 0; index < params.length; index++) {
        builder.append("Parameter:\t").append(index + 1).append(" - ").append(params[index]);
      }
      LOGGER.warning(buildKeyNotFoundMessage(key, bundleName));
      return builder.toString();
    }
  }

  /** Log-Meldung fuer Keys, die nicht gefunden wurden. */
  private static String buildKeyNotFoundMessage(String key, String bundleName)
  {
    return "Could not find key '" + key + "' in bundle '" + bundleName + "'";
  }

  /**
   * Holt einen Property-Wert aus einer Properties-Datei und gibt diesen zurueck.
   * @param bundleName
   *          ist der absolute Pfad der Properties-Datei ab '{@value #I18N_POINT}'.<br/>
   *          siehe {@link BundleStrings}
   * @param key
   *          ist der Key des Property-Werts.
   * @param params
   *          sind Parameter, um Platzhalter innerhalb des Property-Werts auszufuellen.
   * @return
   *          den Property-Wert
   */
  public static String getValue(String bundleName, String key, Object... params) {
    try {
      final String value = getBundle(bundleName).getString(key);
      if(params.length > 0) return MessageFormat.format(value,  params);
      return value;
    } catch (Exception ex) {
      LOGGER.warning(buildKeyNotFoundMessage(key, bundleName));
      return "!" +bundleName+"/"+key+"!";
    }
  }

  /** Liefert das ResourceBundle-Objekt fuer den absoluten Pfad ab '{@value #I18N_POINT}' */
  private static ResourceBundle getBundle(String bundleName) {
    if(!BUNDLE_NAME.equals(I18N_POINT +bundleName)) {
      BUNDLE_NAME = I18N_POINT+bundleName;
      BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
    }
    return BUNDLE;
  }
}
