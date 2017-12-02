package com.github.odinasen.durak.business.network.simon;

import com.github.odinasen.durak.business.network.server.exception.LoginFailedException;
import com.github.odinasen.durak.business.network.server.exception.SessionNotFoundException;

/**
 * Enthaelt Methoden, die von Clients auch ohne Authentifizierung aufgerufen werden koennen.
 * <p>
 * Author: Timm Herrmann
 * Date: 22.06.14
 */
public interface ServerInterface {
    /**
     * Meldet einen Client beim Server an. Wurde ein Client angemeldet, kann dieser auch Methoden
     * aus {@link SessionInterface} aufrufen.
     *
     * @return True, wenn der Client erfoglreich angemeldet wurde.
     * False, wenn der Client nicht angemeldet werden konnte
     * (falsches Passwort oder schon angemeldet).
     */
    SessionInterface login(String name, String password, Callable remoteObject)
            throws LoginFailedException, SessionNotFoundException;

    /**
     * Meldet einen Client vom Server ab. Wurde ein Client abgemeldet, kann dieser keine Methoden
     * aus {@link SessionInterface} aufrufen.
     */
    void logoff(SessionInterface clientSession);
}
