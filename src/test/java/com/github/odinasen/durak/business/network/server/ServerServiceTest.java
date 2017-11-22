package com.github.odinasen.durak.business.network.server;

import com.github.odinasen.durak.business.network.server.exception.LoginFailedException;
import com.github.odinasen.durak.business.network.server.session.SessionFactory;
import com.github.odinasen.durak.business.network.simon.AuthenticationClient;
import com.github.odinasen.durak.business.network.simon.Callable;
import com.github.odinasen.durak.business.network.simon.SessionInterface;
import com.github.odinasen.durak.dto.ClientDto;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class ServerServiceTest {

    private String password = "";
    private ServerService serverService;
    private ClientDto clientDto;

    @Before
    public void setUp() throws Exception {
        clientDto = new ClientDto("", "Horst");
        serverService = ServerService.createService(password);
    }

    @Test
    public void successfulLogin() throws Exception {
        SessionInterface session =
                serverService.login(createAuthenticationClient(password), createMockedCallable());
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
        return serverService.login(createAuthenticationClient(password), callable);
    }

    private AuthenticationClient createAuthenticationClient(String password) {
        return new AuthenticationClient(createMockedCallable(), clientDto, password);
    }

    @Test(expected = LoginFailedException.class)
    public void unsuccessfulLoginWithWrongPassword() throws Exception {
        serverService.login(createAuthenticationClient("wrongPass"), createMockedCallable());
    }

    @Test(expected = IllegalArgumentException.class)
    public void unsuccessfulLoginWithoutCallable() throws Exception {
        serverService.login(new AuthenticationClient(null, clientDto, password), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void unsuccessfulLoginWithoutAuthenticationClient() throws Exception {
        serverService.login(null, createMockedCallable());
    }

    @Test(expected = IllegalArgumentException.class)
    public void unsuccessfulLoginWithNullParameters() throws Exception {
        serverService.login(null, null);
    }

    @Test
    public void logoffDoesNotRaiseAnException() throws Exception {
        Callable callable = createMockedCallable();
        serverService.login(createAuthenticationClient(password), callable);

        serverService.logoff(callable);
    }

    private SessionFactory createMockedSessionFactory() {
        SessionInterface session = mock(SessionInterface.class);

        return new SessionFactory() {
            public SessionInterface createSession(ServerService server,
                                                  ClientDto client,
                                                  Callable factoryCallable) {
                return session;
            }
        };
    }

    @Test
    public void removeSession() throws Exception {
        serverService.setSessionFactory(createMockedSessionFactory());

        Callable callable = createMockedCallable();
        SessionInterface session =
                serverService.login(createAuthenticationClient(password), callable);

        serverService.removeSession(session);
        assertEquals("Server must have zero sessions", 0, serverService.getSessionCount());
    }
}