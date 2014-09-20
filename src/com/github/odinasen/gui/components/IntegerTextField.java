package com.github.odinasen.gui.components;

import javafx.scene.control.TextField;

/**
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 09.01.14
 */
public class IntegerTextField extends TextField {
  private static final String REGEX_INTEGER = "[0-9]";

  /****************/
  /* Constructors */
  /*     End      */
  /****************/

  /***********/
  /* Methods */


  @Override
  public void replaceText(int start, int end, String text) {
    /* Text nur ersetzen, wenn die Eingabe gueltig ist. */
    if (text.matches(REGEX_INTEGER) || text.isEmpty()) {
      super.replaceText(start, end, text);
    }
  }

  @Override
  public void replaceSelection(String text) {
    if (text.matches(REGEX_INTEGER) || text.isEmpty()) {
      super.replaceSelection(text);
    }
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
