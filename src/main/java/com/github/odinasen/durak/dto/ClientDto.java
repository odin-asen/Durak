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
        this.uuid = uuid;
        this.name = name;
        this.isSpectator = false;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSpectator() {
        return isSpectator;
    }

    public void setIsSpectator(boolean spectator) {
        this.isSpectator = spectator;
    }

    /**
     * Never returns null.
     */
    public static ClientDto copy(ClientDto clientDto) {
        ClientDto copy = new ClientDto("", "");

        if (clientDto != null) {
            copy.setUuid(clientDto.getUuid());
            copy.setName(clientDto.getName());
            copy.setIsSpectator(clientDto.isSpectator());
        }

        return copy;
    }
}
