package com.github.odinasen.durak.business.network.server;

import com.github.odinasen.durak.business.network.server.session.Session;
import com.github.odinasen.durak.business.network.simon.Callable;
import com.github.odinasen.durak.dto.ClientDto;
import org.junit.Before;
import org.mockito.Mockito;

public class SessionTest {
    private Session session;

    private static String USER_NAME = "";
    private static String PASSWORD = "krombeeresalat";

    private ClientDto clientDto;

    @Before
    public void setUp() throws Exception {
        this.clientDto = new ClientDto("", "Horst");
        //this.session = Session.createSession(USER_NAME, PASSWORD);
    }

    private Callable createMockedCallable() {
        return Mockito.mock(Callable.class);
    }
}
