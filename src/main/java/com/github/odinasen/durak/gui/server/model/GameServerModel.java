package com.github.odinasen.durak.gui.server.model;

import com.github.odinasen.durak.dto.ClientDto;
import com.github.odinasen.durak.util.Assert;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

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
    private final ObservableList<ClientDto> clients;

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
        this.clients = FXCollections.observableArrayList(new ArrayList<ClientDto>(INITIAL_CLIENTS));
        this.password = new SimpleStringProperty("");
        this.port = new SimpleIntegerProperty(DEFAULT_SERVER_PORT);
    }

    public ObservableList<ClientDto> getClients() {
        return this.clients;
    }

    public void addClient(ClientDto client) {
        Assert.assertNotNull(client);

        this.clients.add(client);
    }

    public IntegerProperty getPort() {
        return this.port;
    }

    public StringProperty getPassword() {
        return this.password;
    }

    public void setGameRunning(boolean gameRunning) {
        this.gameRunning = gameRunning;
    }

    public boolean isGameRunning() {
        return gameRunning;
    }

    public void removeAllClients() {
        this.clients.clear();
    }

    /**
     * Loescht mehrere Benutzer aus der Liste anhand der UUIDs.
     * @param clientIds Eine Liste mit UUID als Generic Type
     */
    public void removeClients(List<? extends ClientDto> clientIds) {
        if (clientIds != null) {
            List<ClientDto> clientsToRemove = new ArrayList<>(clientIds.size());

            this.clients.removeIf(clientIds::contains);
        }
    }
}
