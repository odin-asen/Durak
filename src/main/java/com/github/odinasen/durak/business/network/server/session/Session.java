package com.github.odinasen.durak.business.network.server.session;

import com.github.odinasen.durak.business.game.GameAction;
import com.github.odinasen.durak.business.network.server.ServerService;
import com.github.odinasen.durak.business.network.simon.Callable;
import com.github.odinasen.durak.business.network.simon.SessionInterface;
import com.github.odinasen.durak.dto.ClientDto;
import de.root1.simon.SimonUnreferenced;
import de.root1.simon.annotation.SimonRemote;

@SimonRemote(SessionInterface.class)
public class Session
        implements SessionInterface, SimonUnreferenced {

    private Callable sessionReference;
    private ClientDto client;
    private ServerService serverService;

    Session(ServerService serverService, ClientDto client, Callable sessionReference) {
        this.serverService = serverService;
        this.client = client;
        this.sessionReference = sessionReference;
    }

    @Override
    public void unreferenced() {
        //serverService.removeClient()
    }

    @Override
    public void sendChatMessage(Callable callable, String message) {

    }

    @Override
    public boolean doAction(Callable callable, GameAction action) {
        return false;
    }

    @Override
    public void updateClient(Callable callable, ClientDto client) {

    }

    @Override
    public Callable getCallable() {
        return sessionReference;
    }
}
