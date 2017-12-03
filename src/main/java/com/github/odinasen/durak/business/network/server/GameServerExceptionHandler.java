package com.github.odinasen.durak.business.network.server;

import com.github.odinasen.durak.business.exception.GameServerCode;
import com.github.odinasen.durak.business.exception.SystemException;
import de.root1.simon.exceptions.NameBindingException;

import java.io.IOException;
import java.net.UnknownHostException;

public class GameServerExceptionHandler {
    private Exception exception;
    private int port;

    public GameServerExceptionHandler(Exception exception, int port) {
        this.exception = exception;
        this.port = port;
    }

    public void handleException() throws SystemException {
        final SystemException systemException;
        if (exception instanceof NameBindingException) {
            systemException =
                    SystemException.wrap(exception, GameServerCode.SERVICE_ALREADY_RUNNING)
                                   .set("port", port);
        } else if (exception instanceof UnknownHostException) {
            systemException =
                    SystemException.wrap(exception, GameServerCode.NETWORK_ERROR).set("port", port);
        } else if (exception instanceof IOException) {
            systemException =
                    SystemException.wrap(exception, GameServerCode.PORT_USED).set("port", port);
        } else {
            systemException = new SystemException(GameServerCode.UNKNOWN_SERVER_ERROR);
        }

        throw systemException;
    }
}
