package com.github.odinasen.durak.business.network.client;

import com.github.odinasen.durak.business.exception.SystemException;

import java.util.logging.Logger;

public class GameClientExceptionHandler {
    private Logger output;
    private Exception exception;
    private String messageOverride;

    public GameClientExceptionHandler(Exception exception, Logger output) {
        this(exception, output, null);
    }

    public GameClientExceptionHandler(Exception exception, Logger output, String messageOverride) {
        this.exception = exception;
        this.output = output;
        this.messageOverride = messageOverride;
    }

    public void handleException() throws SystemException {
        final String loggingFormat = "%s: %s";
        GameClientExceptionOutput.handleException(output, exception, messageOverride);
    }
}
