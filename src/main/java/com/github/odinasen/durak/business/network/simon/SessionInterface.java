package com.github.odinasen.durak.business.network.simon;

/**
 * Dieses Interface enthaehlt Methoden, die ein Client ausfuehren kann, wenn dieser sich ueber
 * das {@link ServerInterface} authentifiziert hat.
 */
public interface SessionInterface {
    Callable getCallable();

    String getSessionId();
}
