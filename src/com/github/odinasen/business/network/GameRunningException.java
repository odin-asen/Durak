package com.github.odinasen.business.network;

/**
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 21.06.14
 */
public class GameRunningException extends Exception {
  public static final String REMOVE_PLAYERS_WHILE_RUNNING =
      "Players must not be removed while a game is running.";

  /****************/
  /* Constructors */

  /**
   * Standardkonstruktor, der als Nachricht {@link #REMOVE_PLAYERS_WHILE_RUNNING} ausgibt.
   */
  public GameRunningException() {
    super(REMOVE_PLAYERS_WHILE_RUNNING);
  }

  /*     End      */
  /****************/

  /***********/
  /* Methods */
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
