package de.root1.simon.client;

import de.root1.simon.SimonUnreferenced;
import de.root1.simon.annotation.SimonRemote;

@SimonRemote(value = {ClientCallbackInterface.class})
public class ClientCallback
        implements ClientCallbackInterface,
                   SimonUnreferenced {

    private boolean unreferencedCalled;

    public ClientCallback() {
        unreferencedCalled = false;
    }

    public boolean hasUnreferencedCalled() {
        return unreferencedCalled;
    }

    @Override
    public void doClientStuff() {
        System.out.println("Endliche kann ich SimonClient Sachen machen");
    }

    @Override
    public void unreferenced() {
        unreferencedCalled = true;
        System.out.println("Client has been disconnected by server.");
    }
}
