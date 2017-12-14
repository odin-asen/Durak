package com.github.odinasen.durak.gui.server.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ServerConfigurationModel {
    /** Ist der Standardport fuer den Server. */
    private static final int DEFAULT_SERVER_PORT = 10000;

    /**
     * Ist der Port auf dem der Server laueft.
     */
    private IntegerProperty port;

    public ServerConfigurationModel() {
        port = new SimpleIntegerProperty(DEFAULT_SERVER_PORT);
    }

    public IntegerProperty getPort() {
        return port;
    }
}
