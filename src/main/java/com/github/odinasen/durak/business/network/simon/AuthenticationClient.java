package com.github.odinasen.durak.business.network.simon;

import com.github.odinasen.durak.dto.ClientDto;

import java.io.Serializable;

public class AuthenticationClient implements Serializable {
    private final Callable callable;
    private final ClientDto clientDto;
    private final String password;

    public AuthenticationClient(Callable callable, ClientDto clientDto, String password) {
        this.callable = callable;
        this.clientDto = clientDto;
        this.password = password;
    }

    public Callable getCallable() {
        return callable;
    }

    public ClientDto getClientDto() {
        return clientDto;
    }

    public String getPassword() {
        return password;
    }
}
