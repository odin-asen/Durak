package com.github.odinasen.business.network;

/**
 * Das ist der Spieleserver. Er verwaltet ein laufendes Spiel und wertet Aktionen aus, um Sie dann
 * allen Spielern zu kommunizieren.<br/>
 * Methoden werden über ein Singleton-Objekt aufgerufen.
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 21.06.14
 */
public class GameServer {

  /**
   * Ist das Singleton-Objekt der Klasse.
   */
  private static GameServer instance;

  /****************/
  /* Constructors */

  private GameServer() {

  }

  public static GameServer getInstance() {
    if (instance == null) {
      instance = new GameServer();
    }

    return instance;
  }
  /*     End      */
  /****************/

  /***********/
  /* Methods */

  /**
   * Stoppt den laufenden Server. Laueft ein Spiel, wird dieses auch geschlossen.
   */
  public void stopServer() {

  }

  /**
   * Stoppt ein laufendes Spiel.
   */
  public void stopGame() {

  }

  /**
   * Trennt alle beobachtenden Clients vom Server und entfernt Sie aus der entsprechenden Liste.
   * @return
   *    Die Anzahl der Clients, die entfernt wurden.
   */
  public int removeAllSpectators() {
    return 0;
  }

  /**
   * Trennt alle spielenden Clients vom Server und entfernt Sie aus der entsprechenden Liste.
   * @return
   *    Die Anzahl der Clients, die entfernt wurden.
   */
  public int removeAllPlayers() throws GameRunningException {
    return 0;
  }

  /**
   * Trennt alle Clients vom Server und entfernt Sie aus der Liste.
   * @return
   *    Die Anzahl der Clients, die entfernt wurden.
   */
  public int removeAllClients() throws GameRunningException {
    int removedClients = removeAllSpectators();
    removedClients = removedClients + removeAllPlayers();
    return removedClients;
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
