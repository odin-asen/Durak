package com.github.odinasen.durak.business.network.server;

import com.github.odinasen.durak.business.exception.GameServerCode;
import com.github.odinasen.durak.business.exception.SystemException;
import de.root1.simon.exceptions.NameBindingException;
import org.junit.Test;

import java.io.IOException;
import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class GameServerExceptionHandlerTest {
    @Test
    public void handleNameBindingException() throws Exception {
        NameBindingException testException =
                new NameBindingException("Name is already bound to something.");
        assertEqualsErrorCodeAndPort(testException, GameServerCode.SERVICE_ALREADY_RUNNING);
    }

    @Test
    public void handleUnknownHostException() throws Exception {
        UnknownHostException testException = new UnknownHostException("Host is unknown");
        assertEqualsErrorCodeAndPort(testException, GameServerCode.NETWORK_ERROR);
    }

    @Test
    public void handleIOException() throws Exception {
        IOException testException = new IOException("IOException thrown");
        assertEqualsErrorCodeAndPort(testException, GameServerCode.PORT_USED);
    }

    private void assertEqualsErrorCodeAndPort(Exception testException, GameServerCode errorCode) {
        int testPort = 5;
        try {
            new GameServerExceptionHandler(testException, testPort).handleException();
            fail("Must not reach this line");
        } catch (SystemException ex) {
            assertEquals(errorCode, ex.getErrorCode());
            assertEquals(ex.get("port"), testPort);
        }
    }

    @Test
    public void handleUnexpectedException() {
        RuntimeException unexpectedException = new RuntimeException("Unbekannter Fehler");
        try {
            new GameServerExceptionHandler(unexpectedException, 5).handleException();
            fail("Must not reach this line");
        } catch (SystemException ex) {
            assertEquals(GameServerCode.UNKNOWN_SERVER_ERROR, ex.getErrorCode());
            assertEquals(0, ex.getProperties().size());
        }
    }
}