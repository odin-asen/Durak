package com.github.odinasen.durak.business.network.client;

import com.github.odinasen.durak.business.exception.GameClientCode;
import com.github.odinasen.durak.business.exception.SystemException;
import de.root1.simon.exceptions.EstablishConnectionFailed;
import de.root1.simon.exceptions.LookupFailedException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.UnknownHostException;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class GameClientExceptionHandlerTest {

    Logger loggerMock;
    String testMessage;

    @Before
    public void setup() {
        loggerMock = Mockito.mock(Logger.class);
        testMessage = "some message";
    }

    @Test
    public void handleEstablishConnectionFailed() throws Exception {
        EstablishConnectionFailed testException = new EstablishConnectionFailed(testMessage);
        assertEqualsErrorCodeAndPort(testException, GameClientCode.SERVER_NOT_FOUND);

        String expectedMessage =
                String.format("EstablishConnectionFailed occurred while connecting: %s",
                              testException.getMessage());
        verify(loggerMock, times(1)).warning(expectedMessage);
    }

    @Test
    public void handleUnknownHostException() throws Exception {
        UnknownHostException testException = new UnknownHostException(testMessage);
        assertEqualsErrorCodeAndPort(testException, GameClientCode.SERVER_NOT_FOUND);

        String expectedMessage =
                String.format("Failed connection try to: %s", testException.getMessage());
        verify(loggerMock, times(1)).warning(expectedMessage);
    }

    @Test
    public void handleLookupFailedException() throws Exception {
        LookupFailedException testException = new LookupFailedException("");
        assertEqualsErrorCodeAndPort(testException, GameClientCode.SERVICE_NOT_FOUND);

        String expectedMessage =
                String.format("LookupFailedException occurred while connection: %s",
                              testException.getMessage());
        verify(loggerMock, times(1)).warning(expectedMessage);
    }

    @Test
    public void handleUnexpectedException() {
        RuntimeException unexpectedException = new RuntimeException("Unbekannter Fehler");
        assertEqualsErrorCodeAndPort(unexpectedException, GameClientCode.UNKNOWN_CLIENT_EXCEPTION);

        String expectedMessage =
                String.format("Unknown exception: %s", unexpectedException.getMessage());
        verify(loggerMock, times(1)).warning(expectedMessage);
    }

    private void assertEqualsErrorCodeAndPort(Exception testException, GameClientCode errorCode) {
        try {
            new GameClientExceptionHandler(testException, loggerMock).handleException();
            fail("Must not reach this line");
        } catch (SystemException ex) {
            assertEquals(errorCode, ex.getErrorCode());
            assertEquals(0, ex.getProperties().size());
        }
    }

    @Test
    public void exceptionMessageCanBeReplaced() {
        String exceptionMessage = "Unbekannter Fehler";
        String customMessage = exceptionMessage + " noch ein Text";

        RuntimeException unexpectedException = new RuntimeException(exceptionMessage);
        try {
            new GameClientExceptionHandler(
                    unexpectedException, loggerMock, customMessage).handleException();
            fail("Must not reach this line");
        } catch (SystemException ex) {
            String unexpectedMessage =
                    String.format("Unknown exception: %s", unexpectedException.getMessage());
            verify(loggerMock, times(0)).warning(unexpectedMessage);

            String expectedMessage = String.format("Unknown exception: %s", customMessage);
            verify(loggerMock, times(1)).warning(expectedMessage);
        }
    }
}