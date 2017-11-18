package com.github.odinasen.durak.i18n;

import com.github.odinasen.durak.business.exception.ErrorCode;
import com.github.odinasen.durak.util.LoggingUtility;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class I18nSupport {
    private static final Logger LOGGER = LoggingUtility.getLogger(I18nSupport.class.getName());
    private ResourceBundle bundle;
    private String i18nBundleName;
    private static final String I18N_POINT = "i18n.";
    private static final String EXCEPTIONS_DIR = "exceptions.";

    private static I18nSupport translatorInstance;

    public static I18nSupport getInstance() {
        if (translatorInstance == null) {
            translatorInstance = new I18nSupport();
        }

        return translatorInstance;
    }

    private I18nSupport() {
        i18nBundleName = "";
    }

    public String getExceptionMessage(ErrorCode errorCode, Object... params) {
        final String className = errorCode.getClass().getSimpleName();
        final String key = "" + errorCode.getNumber();
        final String bundleName = EXCEPTIONS_DIR + className;
        try {
            return getBundle(bundleName).getString(key);
        } catch (Exception ex) {
            final StringBuilder builder = new StringBuilder(256);
            builder.append("Exception: ").append(errorCode.getClass().getName());

            if (params.length > 0) {
                builder.append('\n');
            }

            for (int index = 0; index < params.length; index++) {
                builder.append("Parameter:\t")
                       .append(index + 1)
                       .append(" - ")
                       .append(params[index]);
            }
            LOGGER.warning(buildKeyNotFoundMessage(key, bundleName));

            return builder.toString();
        }
    }

    /**
     * Gibt eine Exception-Nachricht fuer einen Fehlercode zurueck.
     *
     * @param errorCode
     *         ist der Fehlercode.
     *
     * @return Einen Text zum Fehlercode. Wenn es dazu keinen Text gibt, werden die Klasse und die
     * uebergebenen Parameter zurueckgegeben.
     */
    public static String getException(ErrorCode errorCode, Object... params) {
        I18nSupport translator = I18nSupport.getInstance();
        return translator.getExceptionMessage(errorCode, params);
    }

    /** Log-Meldung fuer Keys, die nicht gefunden wurden. */
    private static String buildKeyNotFoundMessage(String key, String bundleName) {
        return "Could not find key '" + key + "' in bundle '" + bundleName + "'";
    }

    public String getBundleValue(String bundleName, String key, Object... params) {
        try {
            final String value = getBundle(bundleName).getString(key);
            if (params.length > 0) {
                return MessageFormat.format(value, params);
            }
            return value;
        } catch (Exception ex) {
            LOGGER.warning(buildKeyNotFoundMessage(key, bundleName));
            return "!" + bundleName + "/" + key + "!";
        }
    }

    /**
     * Holt einen Property-Wert aus einer Properties-Datei und gibt diesen zurueck.
     *
     * @param bundleName
     *         ist der absolute Pfad der Properties-Datei ab 'i18n'.<br/>
     *         siehe {@link BundleStrings}
     * @param key
     *         ist der Key des Property-Werts.
     * @param params
     *         sind Parameter, um Platzhalter innerhalb des Property-Werts auszufuellen.
     *
     * @return den Property-Wert
     */
    public static String getValue(String bundleName, String key, Object... params) {
        I18nSupport translator = I18nSupport.getInstance();
        return translator.getBundleValue(bundleName, key, params);
    }

    private ResourceBundle getBundle(String bundleName) {
        String i18nBundleName = I18N_POINT + bundleName;
        bundle = ResourceBundle.getBundle(i18nBundleName);

        return bundle;
    }
}
