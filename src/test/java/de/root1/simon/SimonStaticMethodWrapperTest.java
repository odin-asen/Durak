package de.root1.simon;

import com.github.odinasen.test.UnitTest;
import de.root1.simon.client.ClientCallback;
import de.root1.simon.client.ClientCallbackInterface;
import de.root1.simon.client.SimonClient;
import de.root1.simon.exceptions.EstablishConnectionFailed;
import de.root1.simon.exceptions.LookupFailedException;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.net.UnknownHostException;

import static org.junit.Assert.*;

@Category(UnitTest.class)
public class SimonStaticMethodWrapperTest
        extends SimonConnectionSetup {

    private static final int MAX_INTERVAL_COUNTS = 5;

    @Test
    public void closeNetworkConnectionFromRemoteObject() throws Exception {
        SimonClient proxyClient = openClientConnection();

        ClientCallback callback = getClientCallbackFromProxyClient(proxyClient);
        assertFalse("Unreferenced has already been called", callback.hasUnreferencedCalled());

        SimonStaticMethodWrapper wrapper = new SimonStaticMethodWrapper();
        ClientCallbackInterface remoteObject =
                serverService.getClientRemoteObject(proxyClient.getName());
        wrapper.closeNetworkConnectionFromRemoteObject(remoteObject);

        waitFiveSecondsOrUnreferencedHasBeenCalled(callback);
        assertTrue("Unreferenced not called", callback.hasUnreferencedCalled());
    }

    private SimonClient openClientConnection()
            throws EstablishConnectionFailed, LookupFailedException, UnknownHostException {
        SimonClient proxyClient = new SimonClient(getTestRegistryName(), testPort, "client");
        proxyClient.connectToServer();
        proxyClient.login();

        return proxyClient;
    }

    private ClientCallback getClientCallbackFromProxyClient(SimonClient proxyClient) {
        ClientCallbackInterface localObject = proxyClient.getClientLocalObject();

        if (!(localObject instanceof ClientCallback)) {
            fail("remoteObject is not of type ClientCallback");
        }

        return (ClientCallback) localObject;
    }

    private void waitFiveSecondsOrUnreferencedHasBeenCalled(ClientCallback callback) {
        int count = 0;
        while (count < MAX_INTERVAL_COUNTS && !callback.hasUnreferencedCalled()) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void closeNetworkConnectionFromRemoteObjectThrowsException() throws Exception {
        SimonStaticMethodWrapper wrapper = new SimonStaticMethodWrapper();
        wrapper.closeNetworkConnectionFromRemoteObject(null);
    }
}