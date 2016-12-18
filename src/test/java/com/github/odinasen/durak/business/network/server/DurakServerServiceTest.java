package com.github.odinasen.durak.business.network.server;

import com.github.odinasen.durak.business.network.simon.Callable;
import com.github.odinasen.durak.dto.ClientDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Testklasse fuer die ServerService-Schnittstelle fuer Clients.
 * <p/>
 * Author: Timm Herrmann
 * Date: 19.12.2016
 */
public class DurakServerServiceTest {

    private DurakServerService serverService;

    private static String PWD = "";

    @Before
    public void setUp() throws Exception {
        this.serverService = new DurakServerService(PWD);
    }

    @Test
    public void login() throws Exception {
        Callable callable = Mockito.mock(Callable.class);
        ClientDto client = new ClientDto("", "Horst");
        boolean success = this.serverService.login(callable, client, PWD);
        Assert.assertTrue(success);

        // Passwort falsch
        ClientDto client2 = new ClientDto("", "Horst2");
        success = this.serverService.login(callable, client2, "wrongPass");
        Assert.assertFalse(success);

        // Callable null
        success = this.serverService.login(null, client2, PWD);
        Assert.assertFalse(success);
    }

    @Test
    public void logoff() throws Exception {

    }

    @Test
    public void sendChatMessage() throws Exception {

    }

    @Test
    public void doAction() throws Exception {

    }

    @Test
    public void updateClient() throws Exception {

    }

}