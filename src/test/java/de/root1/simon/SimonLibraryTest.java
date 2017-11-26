package de.root1.simon;

import com.github.odinasen.test.ThirdPartyTest;
import de.root1.simon.client.SimonClient;
import de.root1.simon.exceptions.EstablishConnectionFailed;
import de.root1.simon.exceptions.LookupFailedException;
import de.root1.simon.exceptions.NameBindingException;
import de.root1.simon.utils.Utils;
import org.apache.mina.core.session.IoSession;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.net.UnknownHostException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * SIMON Bibliothek testen und Dinge ausprobieren
 */
@Category(ThirdPartyTest.class)
public class SimonLibraryTest
        extends SimonConnectionSetup {

    private static final int maxIntervalCounts = 5;

    @Test(expected = IllegalArgumentException.class)
    public void testServerRemovesClient()
            throws IOException, NameBindingException, EstablishConnectionFailed, LookupFailedException {
        SimonClient client = new SimonClient(getTestRegistryName(), testPort, "client");
        client.connectToServer();
        client.login();

        Object clientRemoteObject = serverService.getClientRemoteObject(client.getName());
        SimonProxy clientProxy = Simon.getSimonProxy(clientRemoteObject);

        throwExceptionIfDisconnected(clientProxy);

        clientProxy.release();
        clientProxy.getIoSession().closeNow();

        IoSession ioSession = clientProxy.getIoSession();
        assertTrue("Clientverbindung schließt nicht", ioSession.isClosing());
        waitFiveTimesForSecondsIfSessionIsConnected(ioSession);
        assertFalse("Client ist immernoch verbunden", ioSession.isConnected());

        throwExceptionIfDisconnected(clientProxy);
    }

    private void throwExceptionIfDisconnected(SimonProxy proxyObject) {
        Simon.getLocalInetSocketAddress(proxyObject);
    }

    private void waitFiveTimesForSecondsIfSessionIsConnected(IoSession ioSession) {
        int count = 0;
        while (count < maxIntervalCounts && ioSession.isConnected()) {
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
}
