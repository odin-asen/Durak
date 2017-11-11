package de.root1.simon.serverService;

import com.github.odinasen.durak.business.network.simon.SessionInterface;
import de.root1.simon.annotation.SimonRemote;
import de.root1.simon.client.ClientCallbackInterface;

import java.util.HashMap;
import java.util.Map;

@SimonRemote(value = {ServerInterface.class, SessionInterface.class})
public class TestServerService
        implements ServerInterface {

    private static final Map<String, Object> loggedInClients = new HashMap<>();

    @Override
    public boolean login(String clientName, ClientCallbackInterface client) {
        loggedInClients.put(clientName, client);

        /*LoginAuthenticator authenticator = new LoginAuthenticator(client, this.serverPassword);
        UUID clientUUID = createUUIDFromString(client.getClientDto().getUuid());

        if (authenticator.isAuthenticated()) {
            if (!clientConnectionExists(clientUUID) || !isClientUUIDLoggedIn(authenticator, clientUUID)) {
                registerNewClient(client);
                return true;
            }
        }
*/
        return false;
    }

    @Override
    public synchronized void logoff(String clientName) {
        /*if (callable != null) {
            List<UUID> idsToRemove = getUUIDsToRemove(callable);
            removeIDsFromLoggedInClients(idsToRemove);
            this.setChangedAndNotifyObservers(new DurakServiceEvent<>(DurakServiceEventType.CLIENT_LOGOUT,
                                                                                idsToRemove));
            //TODO andere Benutzer benachrichtigen
        }
        */
    }

    public Object getClientRemoteObject(String name) {
        return loggedInClients.get(name);
    }
}