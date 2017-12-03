package com.github.odinasen.durak.business.network.server;

import com.github.odinasen.durak.business.network.ClientMessageType;
import com.github.odinasen.durak.business.network.simon.Callable;
import com.github.odinasen.durak.business.network.simon.SessionInterface;
import com.github.odinasen.durak.dto.ClientDto;
import de.root1.simon.exceptions.SimonRemoteException;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SessionComparatorTest {

    @Test
    public void sessionHasReferenceReturnsTrue() {
        Callable mockedCallable = Mockito.mock(Callable.class);
        SessionInterface session = createSession(mockedCallable);

        SessionComparator comparator = new SessionComparator(session);

        assertTrue(comparator.sessionHasReference(mockedCallable));
    }

    private SessionInterface createSession(Callable callable) {
        return new SessionInterface() {
            @Override
            public Callable getCallable() {
                return callable;
            }

            @Override
            public String getSessionId() {
                return null;
            }

            @Override
            public ClientDto getClientDto() {
                return null;
            }
        };
    }

    @Test
    public void sessionHasReferenceReturnsFalse() {
        Callable mockedCallable = Mockito.mock(Callable.class);
        SessionInterface session = createSession(mockedCallable);

        SessionComparator comparator = new SessionComparator(session);

        Callable otherCallable = Mockito.mock(Callable.class);
        assertFalse(comparator.sessionHasReference(otherCallable));
    }

    @Test
    public void sessionHasReferenceReturnsFalseForSimonException() {
        Callable mockedCallable =
                getCallableWithEqualsThrowsException(new SimonRemoteException(""));
        SessionInterface session = createSession(mockedCallable);

        SessionComparator comparator = new SessionComparator(session);

        assertFalse(comparator.sessionHasReference(mockedCallable));
    }

    private Callable getCallableWithEqualsThrowsException(RuntimeException e) {
        return new Callable() {
            @Override
            public void sendClientMessage(ClientMessageType parameter) {

            }

            @Override
            public boolean equals(Object o) {
                throw e;
            }
        };
    }
}