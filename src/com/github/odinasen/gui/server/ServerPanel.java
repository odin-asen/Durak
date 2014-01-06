package com.github.odinasen.gui.server;

import com.github.odinasen.dto.DTOClient;

import java.util.List;

/**
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 06.01.14
 */
public class ServerPanel {

  private int mPort;
  private InitialCard mNumberCards;

  /****************/
  /* Constructors */
  /*     End      */
  /****************/

  /***********/
  /* Methods */

  public boolean startGame() {
    return false;
  }

  public boolean startServer() {
    return false;
  }

  public void stopGame() {

  }

  public void stopServer() {

  }

  /**
   * Loescht die uebergebenen Clients und gibt die Anzahl
   * der geloeschten Clients zurueck.
   */
  public int removeClients(List<DTOClient> toRemove) {
    return 0;
  }

  /*   End   */
  /***********/

  /*******************/
  /* Private Methods */
  /*       End       */
  /*******************/

  /*********************/
  /* Getter and Setter */

  public int getPort() {
    return mPort;
  }

  public void setPort(int port) {
    mPort = port;
  }

  public void setInitialCard(InitialCard numberCards) {
    mNumberCards = numberCards;
  }

  /*        End        */
  /*********************/

  /*****************/
  /* Inner classes */
  /*      End      */
  /*****************/
}
