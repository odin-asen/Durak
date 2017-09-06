package com.github.odinasen.durak.business.network.server;

import com.github.odinasen.durak.business.ExtendedObservable;
import com.github.odinasen.durak.business.game.GameAction;
import com.github.odinasen.durak.business.network.server.event.DurakServiceEvent;
import com.github.odinasen.durak.business.network.simon.AuthenticationClient;
import com.github.odinasen.durak.business.network.simon.Callable;
import com.github.odinasen.durak.business.network.simon.ServerInterface;
import com.github.odinasen.durak.business.network.simon.SessionInterface;
import com.github.odinasen.durak.dto.ClientDto;
import com.github.odinasen.durak.util.LoggingUtility;
import com.github.odinasen.durak.util.StringUtils;
import de.root1.simon.annotation.SimonRemote;
import de.root1.simon.exceptions.SimonRemoteException;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.github.odinasen.durak.business.network.server.event.DurakServiceEvent.DurakServiceEventType;

/**
 * Dieser Service ist ueberwachbar (java.util.Observable) und sendet bei folgenden Ereignissen
 * ein Event:
 * <p>
 * - Client hat sich angemeldet
 * - Client hat sich abgemeldet
 * <p>
 * Author: Timm Herrmann
 * Date: 23.06.14
 */
@SimonRemote(value = {ServerInterface.class, SessionInterface.class})
public class ServerService
        extends ExtendedObservable
        implements ServerInterface, SessionInterface {

    private static final Logger LOGGER = LoggingUtility.getLogger(ServerService.class);
    private static final Map<UUID, Callable> loggedInClients = new HashMap<>();

    private String serverPassword;

    private ServerService(String serverPassword) {
        setServerPassword(serverPassword);
    }

    static ServerService createService(String password) {
        return new ServerService(password);
    }

    @Override
    public boolean login(AuthenticationClient client) {
        LoginAuthenticator authenticator = new LoginAuthenticator(client, this.serverPassword);
        UUID clientUUID = createUUIDFromString(client.getClientDto().getUuid());

        if (authenticator.isAuthenticated()) {
            if (!clientConnectionExists(clientUUID) || !isClientUUIDLoggedIn(authenticator, clientUUID)) {
                registerNewClient(client);
                return true;
            }
        }

        return false;
    }

    private UUID createUUIDFromString(String uuidString) {
        UUID clientUUID;
        try {
            clientUUID = UUID.fromString(uuidString);
        } catch (Exception ex) {
            clientUUID = null;
        }
        return clientUUID;
    }

    private boolean clientConnectionExists(UUID clientUUID) {
        return clientUUID != null && loggedInClients.containsKey(clientUUID);
    }

    private boolean isClientUUIDLoggedIn(LoginAuthenticator authenticator, UUID clientUUID) {
        Callable loggedInClient = loggedInClients.get(clientUUID);

        return authenticator.clientHasCallable(loggedInClient);
    }

    private void registerNewClient(AuthenticationClient client) {
        ClientDto loginClient = client.getClientDto();
        UUID newClientsUUID = insertClientToLoggedInClients(client);
        loginClient.setUuid(newClientsUUID.toString());
        this.setChangedAndNotifyObservers(new DurakServiceEvent<ClientDto>(DurakServiceEventType.CLIENT_LOGIN, loginClient));
    }

    private synchronized UUID insertClientToLoggedInClients(AuthenticationClient client) {
        UUID clientUUID = UUID.randomUUID();
        loggedInClients.put(clientUUID, client.getCallable());

        return clientUUID;
    }

    @Override
    public synchronized void logoff(Callable callable) {
        if (callable != null) {
            List<UUID> idsToRemove = getUUIDsToRemove(callable);
            removeIDsFromLoggedInClients(idsToRemove);
            this.setChangedAndNotifyObservers(new DurakServiceEvent<List<UUID>>(DurakServiceEventType.CLIENT_LOGOUT,
                                                                                idsToRemove));
            //TODO andere Benutzer benachrichtigen
        }
    }

    @NotNull
    private List<UUID> getUUIDsToRemove(Callable callable) {
        List<UUID> idsToRemove = new ArrayList<>();
        for (Map.Entry<UUID, Callable> entry : loggedInClients.entrySet()) {
            addUUIDIfCallableFoundInto(idsToRemove, callable, entry);
        }
        return idsToRemove;
    }

    private void addUUIDIfCallableFoundInto(List<UUID> idsToRemove,
                                            Callable callable,
                                            Map.Entry<UUID, Callable> entry) {
        try {
            UUID uuid = entry.getKey();
            if (callable.equals(entry.getValue())) {
                idsToRemove.add(uuid);
            }
        } catch (SimonRemoteException ex) {
            final String message =
                    "Remote Object could not properly be processed and " + "will be disconnected from server.";
            LOGGER.info(message + " " + ex.getMessage());
            LOGGER.log(Level.FINE, "", ex);
        }
    }

    private void removeIDsFromLoggedInClients(List<UUID> idsToRemove) {
        for (UUID uuid : idsToRemove) {
            loggedInClients.remove(uuid);
        }
    }

    @Override
    public void sendChatMessage(Callable callable, String message) {

    }

    @Override
    public boolean doAction(Callable callable, GameAction action) {
        return false;
    }

    @Override
    public void updateClient(Callable callable, ClientDto client) {

    }

    public void setServerPassword(String serverPassword) {
        if (serverPassword != null) {
            this.serverPassword = serverPassword;
        } else {
            this.serverPassword = "";
        }
    }
}