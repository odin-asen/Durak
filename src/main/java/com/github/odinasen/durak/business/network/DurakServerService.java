package com.github.odinasen.durak.business.network;

import com.github.odinasen.durak.business.game.GameAction;
import com.github.odinasen.durak.business.network.simon.Callable;
import com.github.odinasen.durak.business.network.simon.ServerInterface;
import com.github.odinasen.durak.business.network.simon.SessionInterface;
import com.github.odinasen.durak.dto.ClientDto;
import com.github.odinasen.durak.util.LoggingUtility;
import com.github.odinasen.durak.util.StringUtils;
import de.root1.simon.annotation.SimonRemote;
import de.root1.simon.exceptions.SimonRemoteException;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Eine Klasse, die verschiedene Dienste des Durak-Servers bereitstellt.
 * Diese Klasse haelt Klassenvariablen, um die Kommunikation zwischen verschiedenen Clients gewaehrleisten zu koennen.
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 23.06.14
 */
@SimonRemote(value = {ServerInterface.class, SessionInterface.class})
public class DurakServerService
        implements ServerInterface, SessionInterface {

    private static final Logger LOGGER = LoggingUtility.getLogger(DurakServerService.class);

    /**
     * Liste der Clients fuer den gesamten Server. Mappt die ID eines Clients zu seinem zugehoerigen Callable-Objekt.
     */
    private static final Map<UUID, Callable> clientMap = new HashMap<>();

    /**
     * Server des Passworts
     */
    private String password;

    public DurakServerService(String password) {
        setPassword(password);
    }

    @Override
    public boolean login(Callable callable, ClientDto client, String password) {
        UUID clientUUID;
        try {
            clientUUID = UUID.fromString(client.getUuid());
        } catch (Exception ex) {
            // Hier wird absichtlich nichts gemacht oder geloggt, weil es nicht notwendig ist zu wissen, ob eine
            // UUID nicht geparst werden konnte. Eine nicht parsbare UUID wird als neuer Client gewertet.
            clientUUID = null;
        }

        // Gibt es eine bestehende Client-Verbindung fuer das Objekt?
        if (clientUUID != null && clientMap.containsKey(clientUUID)) {
            // Ja
            Callable listCallable = clientMap.get(clientUUID);

            if (callable != null) {

                // Ist der registrierte Client ein anderer wie der uerbergebene?
                if (!callable.equals(listCallable)) {
                    // Nein, also mit Passwortpruefung registrieren und UUID aendern
                    return registerNewClient(callable, client, password);
                }
            }
        } else {
            // Nein, also mit Passwortpruefung registrieren
            return registerNewClient(callable, client, password);
        }

        // Die Methode muss schon vorher mal true zurueckgeben, ansonsten wurde der Benutzer nie registriert.
        return false;
    }

    /**
     * Registriert bei uebereinstimmenden Passwoertern einen Benutzer. Setzt in das Client-Objekt die UUID, wenn der
     * Benutzer registriert wurde.
     *
     * @param callable Das Callable-Objekt des Benutzers.
     * @param client   Das Client-Objekt.
     * @param password Die Benutzereingabe des Passworts.
     * @return true, wenn der Client registriert ist und die UUID gesetzt wurde, andernfalls false.
     */
    private boolean registerNewClient(Callable callable, ClientDto client, String password) {
        boolean loggedIn = false;

        if (StringUtils.stringsAreSame(password, this.password)) {
            // ID generieren und Client neu registrieren
            UUID clientUUID = UUID.randomUUID();
            clientMap.put(clientUUID, callable);

            client.setUuid(clientUUID.toString());
            loggedIn = true;
        }

        return loggedIn;
    }

    @Override
    public void logoff(Callable callable) {
        if (callable != null) {
            List<UUID> idsToRemove = new ArrayList<>();
            for (Map.Entry<UUID, Callable> entry : clientMap.entrySet()) {
                try {
                    if (callable.equals(entry.getValue())) {
                        idsToRemove.add(entry.getKey());
                    }
                } catch (SimonRemoteException ex) {
                    final String message = "Remote Object could not properly be processed and will be disconnected " +
                                           "from server.";
                    LOGGER.info(message + " " + ex.getMessage());
                    LOGGER.log(Level.FINE, "", ex);
                }
            }

            clientMap.remove(idsToRemove);
            // Hier koennte man noch alle Clients benachrichtigen
        }
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

    /**
     * Setzt das Serverpasswort.
     */
    public void setPassword(String password) {
        if (password != null) {
            this.password = password;
        } else {
            this.password = "";
        }
    }
}
