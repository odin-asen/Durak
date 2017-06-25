package com.github.odinasen.durak.model;

import com.github.odinasen.durak.business.game.Player;
import com.github.odinasen.durak.business.game.Spectator;

import java.util.ArrayList;
import java.util.List;

/**
 * Model von benutzerbezogenen Daten im Server, z.B. eine Liste aller Beobachter und aller
 * Spieler.
 * <p/>
 * Author: Timm Herrmann
 * Date: 25.05.2017
 */
public class ServerUserModel {
    /** Liste aller Spieler im Server */
    private List<Player> players;

    /** Liste aller Beobachter im Server */
    private List<Spectator> spectators;

    public ServerUserModel() {
        this.players = new ArrayList<>(6);
        this.spectators = new ArrayList<>(0);
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public List<Spectator> getSpectators() {
        return this.spectators;
    }
}
