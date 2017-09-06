package com.github.odinasen.durak.business.network.simon;

/**
 * Enthaelt Methoden, die von Clients auch ohne Authentifizierung aufgerufen werden koennen.
 *
 * Author: Timm Herrmann
 * Date: 22.06.14
 */
public interface ServerInterface {
  /**
   * Meldet einen Client beim Server an. Wurde ein Client angemeldet, kann dieser auch Methoden
   * aus {@link SessionInterface} aufrufen.
   *
   * @param authenticationClient@return
   *    True, wenn der Client erfoglreich angemeldet wurde.
   *    False, wenn der Client nicht angemeldet werden konnte
   *    (falsches Passwort oder schon angemeldet).
   */
  boolean login(AuthenticationClient authenticationClient);

  /**
   * Meldet einen Client vom Server ab. Wurde ein Client abgemeldet, kann dieser keine Methoden
   * aus {@link SessionInterface} aufrufen.
   * @param clientsCallable
   *    Ist das Remote-Objekt, das den Client referenziert.
   */
  void logoff(Callable clientsCallable);
}
