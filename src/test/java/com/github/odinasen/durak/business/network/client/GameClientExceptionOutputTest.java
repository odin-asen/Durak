package com.github.odinasen.durak.business.network.client;

import de.root1.simon.exceptions.EstablishConnectionFailed;
import de.root1.simon.exceptions.LookupFailedException;
import org.junit.Test;

import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;

public class GameClientExceptionOutputTest {
    @Test
    public void getOutputObject() throws Exception {
        GameClientExceptionOutput output =
                GameClientExceptionOutput.getOutputObject(new EstablishConnectionFailed(""));
        assertEquals(output, GameClientExceptionOutput.ESTABLISHED_CONNECTION);

        output = GameClientExceptionOutput.getOutputObject(new LookupFailedException(""));
        assertEquals(output, GameClientExceptionOutput.LOOKUP_FAILED);

        output = GameClientExceptionOutput.getOutputObject(new UnknownHostException(""));
        assertEquals(output, GameClientExceptionOutput.UNKNOWN_HOST);

        output = GameClientExceptionOutput.getOutputObject(new RuntimeException(""));
        assertEquals(output, GameClientExceptionOutput.UNKNOWN);

        output = GameClientExceptionOutput.getOutputObject(null);
        assertEquals(output, GameClientExceptionOutput.UNKNOWN);

        output = GameClientExceptionOutput.getOutputObject(new Exception());
        assertEquals(output, GameClientExceptionOutput.UNKNOWN);
    }

}