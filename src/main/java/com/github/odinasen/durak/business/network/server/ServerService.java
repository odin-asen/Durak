package com.github.odinasen.durak.business.network.server;

import com.github.odinasen.durak.business.ExtendedObservable;
import com.github.odinasen.durak.business.game.GameAction;
import com.github.odinasen.durak.business.network.server.event.DurakServiceEvent;
import com.github.odinasen.durak.business.network.server.exception.LoginFailedException;
import com.github.odinasen.durak.business.network.server.exception.SessionNotFoundException;
import com.github.odinasen.durak.business.network.server.session.SessionFactory;
import com.github.odinasen.durak.business.network.simon.AuthenticationClient;
import com.github.odinasen.durak.business.network.simon.Callable;
import com.github.odinasen.durak.business.network.simon.ServerInterface;
import com.github.odinasen.durak.business.network.simon.SessionInterface;
import com.github.odinasen.durak.dto.ClientDto;
import com.github.odinasen.durak.util.LoggingUtility;
import de.root1.simon.SimonStaticMethodWrapper;
import de.root1.simon.annotation.SimonRemote;
import de.root1.simon.exceptions.SimonRemoteException;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.github.odinasen.durak.business.network.server.event.DurakServiceEvent.DurakServiceEventType;

/**
 * Dieser Service ist ueberwachbar (java.util.Observable) und sendet bei folgenden Ereignissen
 * ein Event:
 * - Client hat sich angemeldet
 * - Client hat sich abgemeldet
 */
@SimonRemote(value = {ServerInterface.class, SessionInterface.class})
public class ServerService
        extends ExtendedObservable
        implements ServerInterface,
                   SessionInterface {

    private static final Logger LOGGER = LoggingUtility.getLogger(ServerService.class);
    private static final Map<UUID, Callable> loggedInClients = new HashMap<>();

    private List<SessionInterface> loggedSessionList;

    private String serverPassword;
    private SessionFactory sessionFactory;

    private ServerService(String serverPassword) {
        loggedSessionList = new ArrayList<>(6);
        setServerPassword(serverPassword);
        sessionFactory = new SessionFactory();
    }

    static ServerService createService(String password) {
        return new ServerService(password);
    }

    public SessionInterface login(AuthenticationClient client, Callable clientCallable)
            throws LoginFailedException, SessionNotFoundException {
        if (client == null || clientCallable == null) {
            throw new IllegalArgumentException("Parameters must not be null!");
        }

        LoginAuthenticator authenticator = new LoginAuthenticator(client, this.serverPassword);
        UUID clientUUID = createUUIDFromString(client.getClientDto().getUuid());

        if (authenticator.isAuthenticated()) {
            if (!clientSessionExists(clientCallable)) {
                registerNewClient(client);
                SessionInterface session =
                        sessionFactory.createSession(this, client.getClientDto(), clientCallable);
                loggedSessionList.add(session);

                return session;
            } else {
                return retrieveSessionByReference(clientCallable);
            }
        } else {
            throw new LoginFailedException();
        }
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

    //TODO wirft exception, wenn sich ein Client zweimal hintereinander einloggt
    private boolean clientSessionExists(Callable callable) {
        boolean clientExists = false;
        for (SessionInterface session : loggedSessionList) {
            clientExists = clientExists || callable.equals(session.getCallable());
        }
        return clientExists;
    }

    private SessionInterface retrieveSessionByReference(Callable reference)
            throws SessionNotFoundException {
        for (SessionInterface session : loggedSessionList) {
            if (reference.equals(session.getCallable())) {
                return session;
            }
        }

        throw new SessionNotFoundException();
    }

    private void registerNewClient(AuthenticationClient client) {
        ClientDto loginClient = client.getClientDto();
        UUID newClientsUUID = insertClientToLoggedInClients(client);
        loginClient.setUuid(newClientsUUID.toString());
        this.setChangedAndNotifyObservers(
                new DurakServiceEvent<>(DurakServiceEventType.CLIENT_LOGIN, loginClient));
    }

    private synchronized UUID insertClientToLoggedInClients(AuthenticationClient client) {
        UUID clientUUID = UUID.randomUUID();
        loggedInClients.put(clientUUID, client.getCallable());

        return clientUUID;
    }

    public synchronized void logoff(Callable callable) {
        if (callable != null) {
            List<UUID> idsToRemove = getUUIDsToRemove(callable);
            removeIDsFromLoggedInClients(idsToRemove);
            this.setChangedAndNotifyObservers(
                    new DurakServiceEvent<>(DurakServiceEventType.CLIENT_LOGOUT, idsToRemove));
            //TODO andere Benutzer benachrichtigen
        }
    }

    private List<UUID> getUUIDsToRemove(Callable callable) {
        List<UUID> idsToRemove = new ArrayList<>();
        for (Map.Entry<UUID, Callable> entry : loggedInClients.entrySet()) {
            UUID uuid = getUUIDIfFound(callable, entry);
            if (uuid != null) {
                idsToRemove.add(uuid);
            }
        }
        return idsToRemove;
    }

    private UUID getUUIDIfFound(Callable callable, Map.Entry<UUID, Callable> entry) {
        try {
            UUID uuid = entry.getKey();
            if (callable.equals(entry.getValue())) {
                return uuid;
            }
        } catch (SimonRemoteException ex) {
            final String message =
                    "Remote Object could not properly be processed and will be disconnected from "
                    + "" + "server.";
            LOGGER.info(message + " " + ex.getMessage());
            LOGGER.log(Level.FINE, "", ex);
        }

        return null;
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

    @Override
    public Callable getCallable() {
        return null;
    }

    public void removeClientByIdSendNotification(String clientId) {
        UUID clientUUID = UUID.fromString(clientId);
        if (loggedInClients.containsKey(clientUUID)) {
            Callable clientCallable = loggedInClients.get(clientUUID);

            SimonStaticMethodWrapper simonWrapper = new SimonStaticMethodWrapper();
            simonWrapper.closeNetworkConnectionFromRemoteObject(clientCallable);

            loggedInClients.remove(clientUUID);
        }
    }

    public void setServerPassword(String serverPassword) {
        if (serverPassword != null) {
            this.serverPassword = serverPassword;
        } else {
            this.serverPassword = "";
        }
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}