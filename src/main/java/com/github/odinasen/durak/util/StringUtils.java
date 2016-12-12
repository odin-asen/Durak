package com.github.odinasen.durak.util;

/**
 * StringUtil wie in jeder anderen Bibliothek.
 * Wird irgendwann eine Bibliothek verwendet mit solch einer Klasse, sollte dieser hier nicht mehr verwendet und ersetzt
 * werden. Extra eine lib fuer StringUtils wollte ich nicht einbinden.
 * <p/>
 * Author: Timm Herrmann
 * Date: 17.11.2016
 */
public class StringUtils {
    /**
     * Liefert true, wenn beide Strings nicht null sind und die gleiche Zeichenfolge besitzen. (Siehe
     * {@link String#equals(Object)})<br/>
     * Liefert true, wenn beide Strings null bzw. leer sind.
     */
    public static boolean stringsAreSame(String theOne, String theOther) {
        // Beide gesetzt
        if (theOne != null && theOther != null) {
            if (theOne.equals(theOther)) {
                return true;
            }
        }

        // Beide null oder leer
        if (theOne == null || theOne.isEmpty()) {
            if (theOther == null || theOther.isEmpty()) {
                return true;
            }
        }

        return false;
    }
}
