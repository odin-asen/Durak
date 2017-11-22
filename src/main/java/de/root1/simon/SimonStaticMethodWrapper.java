package de.root1.simon;

/**
 * !!!!!!!!!!! Package Bezeichnung: Die Utils Klasse kapselt static protected Methoden aus der Simon Klasse.
 */
public class SimonStaticMethodWrapper {

    public void closeNetworkConnectionFromRemoteObject(Object remoteObject) {
        if (remoteObject == null) {
            throw new IllegalArgumentException("remoteObject darf nicht null sein");
        }

        SimonProxy simonProxy = Simon.getSimonProxy(remoteObject);
        simonProxy.release();
        simonProxy.getIoSession().closeNow();

    }
}
