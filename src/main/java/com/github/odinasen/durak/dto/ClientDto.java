package com.github.odinasen.durak.dto;

import java.io.Serializable;

/**
 * DTO fuer einen Client.
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 06.01.14
 */
public class ClientDto
        implements Serializable {
    private String uuid;
    private String name;
    private boolean isSpectator;

    public ClientDto(String uuid, String name) {
        this.uuid = this.uuid;
        this.name = name;
        this.isSpectator = false;
    }

    /**
     * @return das {@link #uuid}-Objekt
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Setzt das {@link #uuid}-Objekt.
     *
     * @param uuid das {@link #uuid}-Objekt
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return das {@link #name}-Objekt
     */
    public String getName() {
        return name;
    }

    /**
     * Setzt das {@link #name}-Objekt.
     *
     * @param name das {@link #name}-Objekt
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return der {@link #isSpectator} Wert
     */
    public boolean isSpectator() {
        return isSpectator;
    }

    /**
     * Setzt den {@link #isSpectator} Wert
     * @return isSpectator der neue {@link #isSpectator} Wert
     */
    public void setIsSpectator(boolean spectator) {
        this.isSpectator = spectator;
    }
}
