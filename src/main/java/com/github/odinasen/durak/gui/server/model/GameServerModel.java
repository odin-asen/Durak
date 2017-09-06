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
import java.util.UUID;

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

    //================================================================================================
    // Getter and Setter

    /**
     * @return ein Iterator von {@link #clients}.
     */
    public ObservableList<ClientDto> getClients() {
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
        this.clients.add(client);
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

    /**
     * Loescht mehrere Benutzer aus der Liste anhand der UUIDs.
     * @param logoutIds Eine Liste mit UUID als Generic Type
     */
    public void removeClients(List<?> logoutIds) {
        if (logoutIds != null) {
            List<ClientDto> clientsToRemove = new ArrayList<>(logoutIds.size());

            for (ClientDto clientDto : this.clients) {
                if (logoutIds.contains(UUID.fromString(clientDto.getUuid()))) {
                    clientsToRemove.add(clientDto);
                }
            }

            this.clients.removeAll(clientsToRemove);
        }
    }
}
