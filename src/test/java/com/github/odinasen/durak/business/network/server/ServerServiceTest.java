package com.github.odinasen.durak.business.network.server;

import com.github.odinasen.durak.business.network.server.exception.LoginFailedException;
import com.github.odinasen.durak.business.network.server.exception.SessionNotFoundException;
import com.github.odinasen.durak.business.network.server.session.SessionFactory;
import com.github.odinasen.durak.business.network.simon.Callable;
import com.github.odinasen.durak.business.network.simon.SessionInterface;
import com.github.odinasen.durak.dto.ClientDto;
import com.github.odinasen.test.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@Category(UnitTest.class)
public class ServerServiceTest {

    private String password = "";
    private ServerService serverService;
    private String clientName = "Horst";
    private ClientDto clientDto;

    @Before
    public void setUp() throws Exception {
        clientDto = new ClientDto("", "Horst");
        serverService = ServerService.createService(password);
    }

    @Test
    public void successfulLogin() throws Exception {
        SessionInterface session = loginClient();
        assertNotNull(session);
    }

    private Callable createMockedCallable() {
        return Mockito.mock(Callable.class);
    }

    @Test
    public void successfulLoginWithSameUserWorksMuiltipleTimesInARow() throws Exception {
        Callable callable = createMockedCallable();
        SessionInterface session = doSuccessfulLoginWithClient(callable);
        for (int i = 0; i < 5; i++) {
            SessionInterface recycledSession = doSuccessfulLoginWithClient(callable);
            assertNotNull(recycledSession);
            assertEquals(session, recycledSession);
        }
    }

    private SessionInterface doSuccessfulLoginWithClient(Callable callable) throws Exception {
        return serverService.login(clientName, password, callable);
    }

    @Test
    public void removeClientById() throws LoginFailedException, SessionNotFoundException {
        int sessionCountBeforeLogin = serverService.getSessionCount();

        SessionInterface session = loginClient();

        assertEquals(1, serverService.getSessionCount());

        String notExistingSessionId = "blibalblub";
        serverService.removeClientBySessionId(notExistingSessionId);
        assertEquals(1, serverService.getSessionCount());

        serverService.removeClientBySessionId(session.getSessionId());
        assertEquals(0, serverService.getSessionCount());
    }

    @Test
    public void successfulLoginMultipleClients() throws Exception {
        int numberOfLogins = 5;
        List<SessionInterface> sessions = new ArrayList<>(numberOfLogins);
        for (int i = 0; i < numberOfLogins; i++) {
            final Callable callable = createMockedCallable();
            final SessionInterface session = doSuccessfulLoginWithClient(callable);

            assertNotNull(session);
            sessions.add(session);
        }

        for (SessionInterface session : sessions) {
            assertEquals(1, Collections.frequency(sessions, session));
        }
    }

    @Test(expected = LoginFailedException.class)
    public void unsuccessfulLoginWithWrongPassword() throws Exception {
        serverService.login(clientName, "wrongPass", createMockedCallable());
    }

    @Test(expected = IllegalArgumentException.class)
    public void unsuccessfulLoginWithoutCallable() throws Exception {
        serverService.login(clientName, password, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void unsuccessfulLoginWithoutAuthenticationClient() throws Exception {
        serverService.login(null, null, createMockedCallable());
    }

    @Test(expected = IllegalArgumentException.class)
    public void unsuccessfulLoginWithNullParameters() throws Exception {
        serverService.login(null, null, null);
    }

    @Test
    public void logoffDoesNotRaiseAnException() throws Exception {
        serverService.logoff(null);

        int sessionCountBeforeLogin = serverService.getSessionCount();

        SessionInterface session = loginClient();

        assertTrue(sessionCountBeforeLogin < serverService.getSessionCount());
        serverService.logoff(session);
        assertEquals(sessionCountBeforeLogin, serverService.getSessionCount());
    }

    private SessionInterface loginClient() throws LoginFailedException, SessionNotFoundException {
        Callable callable = createMockedCallable();
        return serverService.login(clientName, password, callable);
    }

    private SessionFactory createMockedSessionFactory() {
        SessionInterface session = mock(SessionInterface.class);

        return new SessionFactory() {
            public SessionInterface createSession(ServerService server, ClientDto client,
                                                  Callable factoryCallable) {
                return session;
            }
        };
    }

    @Test
    public void removeSession() throws Exception {
        serverService.setSessionFactory(createMockedSessionFactory());

        SessionInterface session = loginClient();

        serverService.removeSession(session);
        assertEquals("Server must have zero sessions", 0, serverService.getSessionCount());
    }
}