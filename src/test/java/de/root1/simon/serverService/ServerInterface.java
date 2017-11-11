package de.root1.simon.serverService;

import de.root1.simon.client.ClientCallbackInterface;

public interface ServerInterface {
    boolean login(String clientName, ClientCallbackInterface clientCallable);

    void logoff(String clientName);
}
