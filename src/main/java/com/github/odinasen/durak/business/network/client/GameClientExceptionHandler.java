package com.github.odinasen.durak.business.network.client;

import com.github.odinasen.durak.business.exception.ErrorCode;
import com.github.odinasen.durak.business.exception.GameClientCode;
import com.github.odinasen.durak.business.exception.SystemException;
import de.root1.simon.exceptions.EstablishConnectionFailed;
import de.root1.simon.exceptions.LookupFailedException;

import java.net.UnknownHostException;
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
        final String exceptionIntro;
        final ErrorCode errorCode;

        if (exception instanceof EstablishConnectionFailed) {
            exceptionIntro = "EstablishConnectionFailed occurred while connecting";
            errorCode = GameClientCode.SERVER_NOT_FOUND;
        } else if (exception instanceof UnknownHostException) {
            exceptionIntro = "Failed connection try to";
            errorCode = GameClientCode.SERVER_NOT_FOUND;
        } else if (exception instanceof LookupFailedException) {
            exceptionIntro = "LookupFailedException occurred while connection";
            errorCode = GameClientCode.SERVICE_NOT_FOUND;
        } else {
            exceptionIntro = "Unknown exception";
            errorCode = GameClientCode.UNKNOWN_CLIENT_EXCEPTION;
        }

        output.warning(String.format(loggingFormat, exceptionIntro, getOutputMessage()));
        throw new SystemException(errorCode);
    }

    private String getOutputMessage() {
        final String exceptionMessage;

        if (messageOverride != null && !messageOverride.isEmpty()) {
            exceptionMessage = messageOverride;
        } else {
            exceptionMessage = exception.getMessage();
        }
        return exceptionMessage;
    }
}
