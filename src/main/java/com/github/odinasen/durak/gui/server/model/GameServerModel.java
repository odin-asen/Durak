package com.github.odinasen.durak.gui.server.model;

import com.github.odinasen.durak.dto.ClientDto;
import com.github.odinasen.durak.util.Assert;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Model fuer den Server, z.B. angemeldete Benutzer.
 */
public class GameServerModel {

    /** Initialer Wert fuer die Anzahl an Clients. */
    private static final int INITIAL_CLIENTS = 8;

    /**
     * Set aller Clients
     */
    private final ObservableList<ClientDto> clients;

    /**
     * Ist das Passwort des Servers.
     */
    private StringProperty password;

    /**
     * Gibt an, ob das Spiel laeuft oder nicht.
     */
    private boolean gameRunning;

    /**
     * Initialisiert {@link #clients} fuer {@value #INITIAL_CLIENTS} Clients. Das Passwort ist leer.
     */
    public GameServerModel() {
        clients = FXCollections.observableArrayList(new ArrayList<ClientDto>(INITIAL_CLIENTS));
        password = new SimpleStringProperty("");
    }

    public ObservableList<ClientDto> getClients() {
        return clients;
    }

    public void addClient(ClientDto client) {
        Assert.assertNotNull(client);

        clients.add(client);
    }

    public StringProperty getPassword() {
        return password;
    }

    public void setGameRunning(boolean gameRunning) {
        this.gameRunning = gameRunning;
    }

    public boolean isGameRunning() {
        return gameRunning;
    }

    public void removeAllClients() {
        clients.clear();
    }

    /**
     * Loescht mehrere Benutzer aus der Liste anhand der UUIDs.
     * @param clientIds Eine Liste mit UUID als Generic Type
     */
    public void removeClients(List<? extends ClientDto> clientIds) {
        if (clientIds != null) {
            List<ClientDto> clientsToRemove = new ArrayList<>(clientIds.size());

            clients.removeIf(clientIds::contains);
        }
    }
}
