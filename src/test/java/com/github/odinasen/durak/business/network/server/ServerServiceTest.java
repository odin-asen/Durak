package com.github.odinasen.durak.business.network.server;

import com.github.odinasen.durak.business.network.server.exception.LoginFailedException;
import com.github.odinasen.durak.business.network.server.session.SessionFactory;
import com.github.odinasen.durak.business.network.simon.AuthenticationClient;
import com.github.odinasen.durak.business.network.simon.Callable;
import com.github.odinasen.durak.business.network.simon.SessionInterface;
import com.github.odinasen.durak.dto.ClientDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;

/**
 * Testklasse fuer die ServerService-Schnittstelle fuer Clients.
 * <p/>
 * Author: Timm Herrmann
 * Date: 19.12.2016
 */
public class ServerServiceTest {

    private String password = "";
    private ServerService serverService;
    private ClientDto clientDto;

    @Before
    public void setUp() throws Exception {
        this.clientDto = new ClientDto("", "Horst");
        this.serverService = ServerService.createService(password);
    }

    @Test
    public void successfulLogin() throws Exception {
        SessionInterface session = serverService.login(createAuthenticationClient(password), createMockedCallable());
        Assert.assertNotNull(session);
    }

    @Test
    public void successfulLoginWithSameUserWorksMuiltipleTimesInARow() throws Exception {
        Callable callable = createMockedCallable();
        SessionInterface session = doSuccessfulLoginWithClient(callable);
        for (int i = 0; i < 5; i++) {
            SessionInterface recycledSession = doSuccessfulLoginWithClient(callable);
            Assert.assertNotNull(recycledSession);
            Assert.assertEquals(session, recycledSession);
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

    private Callable createMockedCallable() {
        return Mockito.mock(Callable.class);
    }

    @Test
    public void logoffDoesNotRaiseAnException() throws Exception {
        Callable callable = createMockedCallable();
        serverService.login(createAuthenticationClient(password), callable);

        serverService.logoff(callable);
    }

    @Test
    public void logoffNotifiesClients() throws Exception {
        serverService.setSessionFactory(createMockedSessionFactory());

        Callable callable = createMockedCallable();
        SessionInterface session = serverService.login(createAuthenticationClient(password), callable);
        serverService.logoff(callable);

        // TODO benachrichtigung testen
        //verify(session, times(1))
    }

    private SessionFactory createMockedSessionFactory() {
        SessionInterface session = mock(SessionInterface.class);
        SessionFactory sessionFactory = new SessionFactory() {
            public SessionInterface createSession(ServerService server, ClientDto clientDto, Callable factoryCallable) {
                return session;
            }
        };

        return sessionFactory;
    }
}