package com.github.odinasen.durak.gui.server.model;

import com.github.odinasen.durak.dto.ClientDto;
import com.github.odinasen.durak.util.Assert;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.util.HashSet;

/**
 * Model fuer den Server, z.B. angemeldete Benutzer.
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 27.03.15
 */
public class GameServerModel {

    /** Initialer Wert fuer die Anzahl an Clients. */
    private static final int INITIAL_CLIENTS = 8;

    /** Ist der Standardport fuer den Server. */
    private static final int DEFAULT_SERVER_PORT = 10000;

    /**
     * Set aller Clients
     */
    private final ObservableSet<SimpleObjectProperty<ClientDto>> clients;

    /**
     * Ist der Port auf dem der Server laueft.
     */
    private IntegerProperty port;

    /**
     * Ist das Passwort des Servers.
     */
    private StringProperty password;

    /**
     * Gibt an, ob das Spiel laeuft oder nicht.
     */
    private boolean gameRunning;

    /**
     * Initialisiert {@link #clients} fuer {@value #INITIAL_CLIENTS} Clients. Das Passwort ist leer
     * und der Port ist {@value #DEFAULT_SERVER_PORT}.
     */
    public GameServerModel() {
        this.clients = FXCollections.observableSet(new HashSet<SimpleObjectProperty<ClientDto>>(
                INITIAL_CLIENTS));
        this.password = new SimpleStringProperty("");
        this.port = new SimpleIntegerProperty(DEFAULT_SERVER_PORT);
    }

    //================================================================================================
    // Getter and Setter

    /**
     * @return ein Iterator von {@link #clients}.
     */
    public ObservableSet<SimpleObjectProperty<ClientDto>> getClients() {
        return this.clients;
    }

    /**
     * Fuegt einen Client zur Liste {@link #clients} hinzu.
     *
     * @param client
     *         ist ein {@link ClientDto}-Objekt. Darf nicht null sein.
     */
    public void addClient(ClientDto client) {
        Assert.assertNotNull(client);
        //----------------------------------------------------------------------------------------------
        this.clients.add(new SimpleObjectProperty<ClientDto>(client));
    }

    /**
     * @return das {@link #port}-Objekt
     */
    public IntegerProperty getPort() {
        return this.port;
    }

    /**
     * @return das {@link #password}-Objekt
     */
    public StringProperty getPassword() {
        return this.password;
    }

    /**
     * Setzt das {@link #gameRunning}-Objekt.
     *
     * @param gameRunning
     *         das {@link #gameRunning}-Objekt
     */
    public void setGameRunning(boolean gameRunning) {
        this.gameRunning = gameRunning;
    }

    /**
     * @return das {@link #gameRunning}-Objekt
     */
    public boolean isGameRunning() {
        return gameRunning;
    }

    public void removeAllClients() {
        this.clients.clear();
    }
}
