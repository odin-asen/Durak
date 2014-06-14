package com.github.odinasen;

/**
 * Eine Klasse fuer statische Assertation-Methoden.
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 15.06.14
 */
public class Assert {
  /****************/
  /* Constructors */
  /*     End      */
  /****************/

  /***********/
  /* Methods */

  /**
   * Prueft ein Objekt auf null und gibt im Fehlerfall eine entsprechende Meldung fuer ein
   * FX-Element aus, dass aus einer fxml-Datei geladen werden sollte.
   * @param fxElement
   *          ist das Objekt des FX-Elements.
   * @param name
   *          ist der Name der FX-Element-Variablen.
   * @param fxmlFile
   *          ist der Name oder Pfad der fxml-Datei. Die Dateiendung wird in der Nachricht
   *          angehaengt.
   */
  public static void assertFXElementNotNull(Object fxElement, String name, String fxmlFile) {
    final String errorMessage = "fx:id=\"" + name + "\""
                              + "was not injected: check your FXML file '"
                              + fxmlFile + ".fxml'.";
    assert fxElement != null : errorMessage;
  }

  /*   End   */
  /***********/

  /*******************/
  /* Private Methods */
  /*       End       */
  /*******************/

  /*********************/
  /* Getter and Setter */
  /*        End        */
  /*********************/

  /*****************/
  /* Inner classes */
  /*      End      */
  /*****************/
}