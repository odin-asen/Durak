package com.github.odinasen.durak.business.network.simon;

import com.github.odinasen.durak.dto.ClientDto;
import de.root1.simon.annotation.SimonRemote;

/**
 * Enthaelt Methoden, die von Clients auch ohne Authentifizierung aufgerufen werden koennen.
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 22.06.14
 */
public interface ServerInterface {
  /**
   * Meldet einen Client beim Server an. Wurde ein Client angemeldet, kann dieser auch Methoden
   * aus {@link SessionInterface} aufrufen.
   * @param callable
   *    Ist das Remote-Objekt, das den Client referenziert.
   * @param client
   *    Ist das Informationsobjekt des Clients.
   * @param password
   *    Ist das Passwort, dass fuer die Anmeldung verwendet wird.
   * @return
   *    True, wenn der Client erfoglreich angemeldet wurde.
   *    False, wenn der Client nicht angemeldet werden konnte
   *    (falsches Passwort oder schon angemeldet).
   */
  boolean login(Callable callable, ClientDto client, String password);

  /**
   * Meldet einen Client vom Server ab. Wurde ein Client abgemeldet, kann dieser keine Methoden
   * aus {@link SessionInterface} aufrufen.
   * @param callable
   *    Ist das Remote-Objekt, das den Client referenziert.
   */
  void logoff(Callable callable);
}
