package com.github.odinasen.durak.business.network.server.session;

import com.github.odinasen.durak.business.network.server.ServerService;
import com.github.odinasen.durak.business.network.simon.Callable;
import com.github.odinasen.durak.business.network.simon.SessionInterface;
import com.github.odinasen.durak.dto.ClientDto;

public class SessionFactory {

    public SessionInterface createSession(ServerService serverService, ClientDto client, Callable sessionReference) {
        return new Session(serverService, client, sessionReference);
    }
}
