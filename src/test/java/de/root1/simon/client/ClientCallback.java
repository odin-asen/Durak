package de.root1.simon.client;

import de.root1.simon.annotation.SimonRemote;

@SimonRemote(value = {ClientCallbackInterface.class})
public class ClientCallback
        implements ClientCallbackInterface {

    @Override
    public void doClientStuff() {
        System.out.println("Endliche kann ich SimonClient Sachen machen");
    }
}
