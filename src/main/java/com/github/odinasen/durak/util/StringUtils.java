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
     * Liefet true, wenn beide Strings nicht null sind und die gleiche Zeichenfolge besitzen. (Siehe
     * String{@link #equals(Object)})
     */
    public static boolean stringsAreSame(String theOne, String theOther) {
        return (theOne != null) && (theOther != null) && theOne.equals(theOther);
    }
}
