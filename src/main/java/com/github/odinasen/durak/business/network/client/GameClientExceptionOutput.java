package com.github.odinasen.durak.business.network.client;

import com.github.odinasen.durak.business.exception.ErrorCode;
import com.github.odinasen.durak.business.exception.GameClientCode;
import com.github.odinasen.durak.business.exception.SystemException;
import de.root1.simon.exceptions.EstablishConnectionFailed;
import de.root1.simon.exceptions.LookupFailedException;

import java.net.UnknownHostException;
import java.util.logging.Logger;

public enum GameClientExceptionOutput {
    ESTABLISHED_CONNECTION(EstablishConnectionFailed.class,
                           "EstablishConnectionFailed occurred while connecting",
                           GameClientCode.SERVER_NOT_FOUND),
    UNKNOWN_HOST(UnknownHostException.class, "Failed connection try to",
                 GameClientCode.SERVER_NOT_FOUND),
    LOOKUP_FAILED(LookupFailedException.class, "LookupFailedException occurred while connection",
                  GameClientCode.SERVICE_NOT_FOUND),
    UNKNOWN(Exception.class, "Unknown exception", GameClientCode.UNKNOWN_CLIENT_EXCEPTION);

    private Class<? extends Exception> exceptionClass;
    private String exceptionIntro;
    //TODO variable entfernen
    private ErrorCode errorCode;

    GameClientExceptionOutput(Class<? extends Exception> exceptionClass,
                              String exceptionIntro,
                              ErrorCode errorCode) {
        this.exceptionClass = exceptionClass;
        this.exceptionIntro = exceptionIntro;
        this.errorCode = errorCode;
    }

    public static void handleException(Logger output, Exception exception, String messageOverride)
            throws SystemException {
        final String loggingFormat = "%s: %s";

        output.warning(String.format(loggingFormat, getOutputObject(exception).exceptionIntro,
                                     GameClientExceptionOutput.getOutputMessage(exception,
                                                                                messageOverride)));
        throw new SystemException(getOutputObject(exception).errorCode);
    }

    private static String getOutputMessage(Exception exception, String messageOverride) {
        final String exceptionMessage;

        if (messageOverride != null && !messageOverride.isEmpty()) {
            exceptionMessage = messageOverride;
        } else {
            if (exception != null) {
                exceptionMessage = exception.getMessage();
            } else {
                exceptionMessage = "";
            }
        }
        return exceptionMessage;
    }

    private static GameClientExceptionOutput getOutputObject(Exception exception) {
        for (GameClientExceptionOutput object : GameClientExceptionOutput.values()) {
            if (object.exceptionClass.isInstance(exception)) {
                return object;
            }
        }

        return UNKNOWN;
    }
}
