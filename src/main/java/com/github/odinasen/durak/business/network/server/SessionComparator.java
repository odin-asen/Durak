package com.github.odinasen.durak.business.network.server;

import com.github.odinasen.durak.business.network.simon.Callable;
import com.github.odinasen.durak.business.network.simon.SessionInterface;
import de.root1.simon.exceptions.SimonRemoteException;

public class SessionComparator {

    private SessionInterface session;

    public SessionComparator(SessionInterface session) {
        this.session = session;
    }

    public boolean sessionHasReference(Callable reference) {
        try {
            return reference.equals(session.getCallable());
        } catch (SimonRemoteException ex) {
            return false;
        }
    }
}
