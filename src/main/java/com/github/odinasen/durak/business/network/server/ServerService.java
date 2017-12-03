package com.github.odinasen.durak.business.network.server;

import com.github.odinasen.durak.business.ObserverNotificator;
import com.github.odinasen.durak.business.network.server.event.DurakServiceEvent;
import com.github.odinasen.durak.business.network.server.exception.LoginFailedException;
import com.github.odinasen.durak.business.network.server.exception.SessionNotFoundException;
import com.github.odinasen.durak.business.network.server.session.SessionFactory;
import com.github.odinasen.durak.business.network.simon.Callable;
import com.github.odinasen.durak.business.network.simon.ServerInterface;
import com.github.odinasen.durak.business.network.simon.SessionInterface;
import com.github.odinasen.durak.dto.ClientDto;
import de.root1.simon.annotation.SimonRemote;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.UUID;

import static com.github.odinasen.durak.business.network.server.event.DurakServiceEvent
        .DurakServiceEventType;

/**
 * Dieser Service ist ueberwachbar (java.util.Observable) und sendet bei folgenden Ereignissen
 * ein Event:
 * - Client hat sich angemeldet
 * - Client hat sich abgemeldet
 */
@SimonRemote(value = {ServerInterface.class, SessionInterface.class})
public class ServerService
        implements ServerInterface {

    private List<SessionInterface> loggedSessionList;

    private String serverPassword;
    private SessionFactory sessionFactory;
    private ObserverNotificator notificator;

    private ServerService(String serverPassword) {
        loggedSessionList = new ArrayList<>(6);
        setServerPassword(serverPassword);
        sessionFactory = new SessionFactory();
        notificator = new ObserverNotificator();
    }

    static ServerService createService(String password) {
        return new ServerService(password);
    }

    @Override
    public SessionInterface login(String name, String password, Callable remoteObject)
            throws LoginFailedException, SessionNotFoundException {
        if (name == null || remoteObject == null) {
            throw new IllegalArgumentException("Parameters must not be null!");
        }

        LoginAuthenticator authenticator =
                new LoginAuthenticator(name, serverPassword, remoteObject);

        if (authenticator.passwordIsCorrect(password)) {
            try {
                return retrieveSessionByReference(remoteObject);
            } catch (SessionNotFoundException ex) {
                return createNewClient(name, remoteObject);
            }
        } else {
            throw new LoginFailedException();
        }
    }

    private SessionInterface retrieveSessionByReference(Callable reference)
            throws SessionNotFoundException {
        boolean clientExists = false;
        for (SessionInterface session : loggedSessionList) {
            SessionComparator comparator = new SessionComparator(session);
            if (comparator.sessionHasReference(reference)) {
                return session;
            }
        }

        throw new SessionNotFoundException();
    }

    private SessionInterface createNewClient(String name, Callable clientCallable) {
        ClientDto newClient = new ClientDto(UUID.randomUUID().toString(), name);

        SessionInterface session = sessionFactory.createSession(this, newClient, clientCallable);
        loggedSessionList.add(session);

        DurakServiceEvent<ClientDto> loginEvent =
                new DurakServiceEvent<>(DurakServiceEventType.CLIENT_LOGIN, newClient);
        notificator.setChangedAndNotifyObservers(loginEvent);

        return session;
    }

    public synchronized void logoff(SessionInterface clientSession) {
        if (clientSession != null) {
            boolean sessionRemoved = removeSession(clientSession);

            if (sessionRemoved) {
                ClientDto clientDto = clientSession.getClientDto();

                List<ClientDto> clients = new ArrayList<>(1);
                clients.add(clientDto);
                notificator.setChangedAndNotifyObservers(
                        new DurakServiceEvent<>(DurakServiceEventType.CLIENT_LOGOUT, clients));
            }
        }
    }

    public boolean removeSession(SessionInterface session) {
        return loggedSessionList.remove(session);
    }

    public void removeClientBySessionId(String sessionId) {
        SessionInterface toBeRemovedSession = null;
        for (SessionInterface session : loggedSessionList) {
            if (sessionId.equals(session.getSessionId()) && toBeRemovedSession == null) {
                toBeRemovedSession = session;
            }
        }

        removeSession(toBeRemovedSession);
    }

    public void setServerPassword(String serverPassword) {
        if (serverPassword != null) {
            this.serverPassword = serverPassword;
        } else {
            this.serverPassword = "";
        }
    }

    public void addObserver(Observer observer) {
        notificator.addObserver(observer);
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public int getSessionCount() {
        return loggedSessionList.size();
    }
}