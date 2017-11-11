package de.root1.simon.client;

import de.root1.simon.Lookup;
import de.root1.simon.Simon;
import de.root1.simon.exceptions.EstablishConnectionFailed;
import de.root1.simon.exceptions.LookupFailedException;
import de.root1.simon.serverService.ServerInterface;

import java.net.UnknownHostException;

public class SimonClient {

    private int serverPort;
    private String serverRegistryName;
    private ServerInterface server;
    private ClientCallbackInterface clientRemoteObject;
    private Lookup nameLookup;
    private String name;

    public SimonClient(String serverRegistryName, int port, String clientName) {
        this.serverPort = port;
        this.serverRegistryName = serverRegistryName;
        name = clientName;
    }

    public void connectToServer() throws EstablishConnectionFailed, LookupFailedException, UnknownHostException {
        nameLookup = Simon.createNameLookup("localhost", serverPort);
        server = (ServerInterface) nameLookup.lookup(serverRegistryName);
    }

    public void login() {
        clientRemoteObject = new ClientCallback();
        server.login(name, clientRemoteObject);
    }

    public boolean tryRelease() {
        return nameLookup.release(server);
    }

    public ClientCallbackInterface getClientLocalObject() {
        return clientRemoteObject;
    }

    public String getName() {
        return name;
    }
}
