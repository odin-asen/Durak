package com.github.odinasen.durak.business.network.server.session;

import com.github.odinasen.durak.business.network.server.ServerService;
import com.github.odinasen.durak.business.network.simon.Callable;
import com.github.odinasen.durak.business.network.simon.SessionInterface;
import com.github.odinasen.durak.dto.ClientDto;
import de.root1.simon.SimonUnreferenced;
import de.root1.simon.annotation.SimonRemote;

import java.util.UUID;

@SimonRemote(SessionInterface.class)
public class Session
        implements SessionInterface, SimonUnreferenced {

    private Callable sessionReference;
    private ClientDto client;
    private ServerService serverService;
    private UUID sessionId;

    Session(ServerService serverService, ClientDto client, Callable sessionReference) {
        this.serverService = serverService;
        this.client = client;
        this.sessionReference = sessionReference;
        this.sessionId = UUID.randomUUID();
    }

    @Override
    public void unreferenced() {
        serverService.removeSession(this);
    }

    @Override
    public Callable getCallable() {
        return sessionReference;
    }

    @Override
    public String getSessionId() {
        return sessionId.toString();
    }

    public ClientDto getClientDto() {
        return client;
    }
}
