package com.github.odinasen.durak.dto.network.server;

import com.github.odinasen.durak.dto.ClientDto;

public class SessionClientDto {
    ClientDto clientDto;

    public SessionClientDto(ClientDto clientDto) {
        this.clientDto = ClientDto.copy(clientDto);
    }
}
