package de.root1.simon.serverService;

import com.github.odinasen.durak.business.network.simon.SessionInterface;
import de.root1.simon.annotation.SimonRemote;
import de.root1.simon.client.ClientCallbackInterface;

import java.util.HashMap;
import java.util.Map;

@SimonRemote(value = {ServerInterface.class, SessionInterface.class})
public class TestServerService
        implements ServerInterface {

    private static final Map<String, ClientCallbackInterface> loggedInClients = new HashMap<>();

    @Override
    public boolean login(String clientName, ClientCallbackInterface client) {
        loggedInClients.put(clientName, client);

        return false;
    }

    @Override
    public synchronized void logoff(String clientName) {

    }

    public ClientCallbackInterface getClientRemoteObject(String clientName) {
        return loggedInClients.get(clientName);
    }
}