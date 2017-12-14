package com.github.odinasen.durak.gui.server.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ServerConfigurationModel {
    /** Ist der Standardport fuer den Server. */
    private static final int DEFAULT_SERVER_PORT = 10000;

    /**
     * Ist der Port auf dem der Server laueft.
     */
    private IntegerProperty port;

    /**
     * Ist das Passwort des Servers.
     */
    private StringProperty password;

    public ServerConfigurationModel() {
        port = new SimpleIntegerProperty(DEFAULT_SERVER_PORT);
        password = new SimpleStringProperty("");
    }

    public IntegerProperty getPort() {
        return port;
    }

    public StringProperty getPassword() {
        return password;
    }
}
