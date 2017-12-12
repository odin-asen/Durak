package com.github.odinasen.durak.business.network.client;

import com.github.odinasen.durak.business.exception.GameClientCode;
import com.github.odinasen.durak.business.exception.SystemException;
import com.github.odinasen.durak.util.LoggingUtility;
import de.root1.simon.exceptions.EstablishConnectionFailed;
import de.root1.simon.exceptions.LookupFailedException;
import org.junit.Test;

import java.net.UnknownHostException;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

public class GameClientExceptionOutputTest {
    Logger logger = LoggingUtility.getLogger(GameClientExceptionOutputTest.class);

    @Test
    public void getOutputObject() throws Exception {
        assertEqualsOutputObjectAndErrorCode(
                new EstablishConnectionFailed(""), GameClientCode.SERVER_NOT_FOUND);

        assertEqualsOutputObjectAndErrorCode(
                new LookupFailedException(""), GameClientCode.SERVICE_NOT_FOUND);

        assertEqualsOutputObjectAndErrorCode(
                new UnknownHostException(""), GameClientCode.SERVER_NOT_FOUND);

        assertEqualsOutputObjectAndErrorCode(
                new RuntimeException(""), GameClientCode.UNKNOWN_CLIENT_EXCEPTION);

        assertEqualsOutputObjectAndErrorCode(null, GameClientCode.UNKNOWN_CLIENT_EXCEPTION);

        assertEqualsOutputObjectAndErrorCode(
                new Exception(), GameClientCode.UNKNOWN_CLIENT_EXCEPTION);
    }

    private void assertEqualsOutputObjectAndErrorCode(Exception exception,
                                                      GameClientCode expectedErrorCode) {
        try {
            GameClientExceptionOutput.handleException(logger, exception, "");
        } catch (SystemException ex) {
            assertEquals(expectedErrorCode, ex.getErrorCode());
        }
    }
}