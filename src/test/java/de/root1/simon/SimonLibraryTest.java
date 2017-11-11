package de.root1.simon;

import de.root1.simon.client.SimonClient;
import de.root1.simon.exceptions.EstablishConnectionFailed;
import de.root1.simon.exceptions.LookupFailedException;
import de.root1.simon.exceptions.NameBindingException;
import de.root1.simon.serverService.TestServerService;
import de.root1.simon.utils.Utils;
import org.apache.mina.core.session.IoSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.UnknownHostException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * SIMON Bibliothek testen und Dinge ausprobieren
 */
public class SimonLibraryTest {

    private int testPort;
    private Registry serverRegistry;
    private TestServerService serverService;

    @Before
    public void setUp() throws Exception {
        testPort = 10000;
        serverRegistry = Simon.createRegistry(testPort);
        serverService = new TestServerService();
        serverRegistry.start();
        serverRegistry.bind(getTestRegistryName(), serverService);
    }

    @After
    public void tearDown() throws Exception {
        serverRegistry.unbind(getTestRegistryName());
        serverRegistry.stop();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testServerRemovesClient()
            throws IOException, NameBindingException, EstablishConnectionFailed, LookupFailedException {
        SimonClient client = new SimonClient(getTestRegistryName(), testPort, "client");
        client.connectToServer();
        client.login();

        Object clientRemoteObject = serverService.getClientRemoteObject(client.getName());
        SimonProxy clientProxy = Simon.getSimonProxy(clientRemoteObject);
        clientProxy.release();

        clientProxy.getIoSession().closeNow();

        IoSession ioSession = clientProxy.getIoSession();
        assertTrue("Clientverbindung schließt nicht", ioSession.isClosing());
        waitXTimesForSecondsIfSessionIsConnected(5, ioSession);
        assertFalse("Client ist immernoch verbunden", ioSession.isConnected());

        Simon.getLocalInetSocketAddress(clientProxy);
    }

    private void waitXTimesForSecondsIfSessionIsConnected(int waitXTimes, IoSession ioSession) {
        int count = 0;
        while (count < waitXTimes && ioSession.isConnected()) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
        }
    }

    @Test
    public void testRemoteInterfaceIsRemoteObject()
            throws EstablishConnectionFailed, LookupFailedException, UnknownHostException {
        SimonClient client = new SimonClient(getTestRegistryName(), testPort, "client");
        client.connectToServer();
        client.login();

        Object clientRemoteObject = serverService.getClientRemoteObject(client.getName());

        assertTrue("clientRemoteObject is not SimonProxy", Utils.isSimonProxy(clientRemoteObject));
    }

    private String getTestRegistryName() {
        return "testRegistry";
    }
}
