package com.github.odinasen.durak.business.network.server;

import com.github.odinasen.durak.business.ExtendedObservable;
import com.github.odinasen.durak.business.game.GameAction;
import com.github.odinasen.durak.business.network.server.event.DurakServiceEvent;
import com.github.odinasen.durak.business.network.simon.AuthenticationClient;
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

import static com.github.odinasen.durak.business.network.server.event.DurakServiceEvent.DurakServiceEventType;

/**
 * Dieser Service ist ueberwachbar (java.util.Observable) und sendet bei folgenden Ereignissen
 * ein Event:
 *
 * - Client hat sich angemeldet
 * - Client hat sich abgemeldet
 *
 * Author: Timm Herrmann
 * Date: 23.06.14
 */
@SimonRemote(value = {ServerInterface.class, SessionInterface.class})
public class ServerService
        extends ExtendedObservable
        implements ServerInterface,
                   SessionInterface {

    private static final Logger LOGGER = LoggingUtility.getLogger(ServerService.class);

    /**
     * Liste der Clients fuer den gesamten Server. Mappt die ID eines Clients zu seinem
     * zugehoerigen Callable-Objekt.
     */
    private static final Map<UUID, Callable> clientMap = new HashMap<>();

    /** Server des Passworts */
    private String password;

    public static ServerService createService(String password) {
        return new ServerService(password);
    }

    private ServerService(String password) {
        setPassword(password);
    }

    @Override
    public boolean login(AuthenticationClient client) {
        UUID clientUUID;
        try {
            clientUUID = UUID.fromString(client.getClientDto().getUuid());
        } catch (Exception ex) {
            clientUUID = null;
        }

        // Gibt es eine bestehende Client-Verbindung fuer das Objekt?
        if (clientUUID != null && clientMap.containsKey(clientUUID)) {
            // Ja
            Callable listCallable = clientMap.get(clientUUID);

            if (client.getCallable() != null) {

                // Ist der registrierte Client ein anderer als der uerbergebene?
                if (!client.getCallable().equals(listCallable)) {
                    // Nein, also mit Passwortpruefung registrieren und UUID aendern
                    int a = 0;
                    return registerNewClient(client);
                }
            }
        } else {
            if (client.getCallable() != null) {
                // Nein, also mit Passwortpruefung registrieren
                return registerNewClient(client);
            }
        }

        // Die Methode muss schon vorher mal true zurueckgeben, ansonsten wurde der Benutzer nie
        // registriert.
        return false;
    }

    /**
     * Registriert bei uebereinstimmenden Passwoertern einen Benutzer. Setzt in das Client-Objekt
     * die UUID, wenn der
     * Benutzer registriert wurde.
     *
     * @return true, wenn der Client registriert ist und die UUID gesetzt wurde, andernfalls false.
     */
    private boolean registerNewClient(AuthenticationClient client) {
        boolean loggedIn = false;

        // Passwort muss stimmen und Callable-Objekt darf nicht null sein
        if (StringUtils.stringsAreSame(client.getPassword(), this.password) && (client.getCallable() != null)) {
            // ID generieren und Client neu registrieren
            UUID clientUUID = UUID.randomUUID();
            clientMap.put(clientUUID, client.getCallable());

            client.getClientDto().setUuid(clientUUID.toString());
            loggedIn = true;

            // Observer informieren
            this.setChangedAndUpdate(new DurakServiceEvent<ClientDto>(DurakServiceEventType.CLIENT_LOGIN, client.getClientDto()));
        }

        return loggedIn;
    }

    @Override
    public void logoff(Callable callable) {
        if (callable != null) {
            List<UUID> idsToRemove = new ArrayList<>();
            for (Map.Entry<UUID, Callable> entry : clientMap.entrySet()) {
                try {
                    UUID uuid = entry.getKey();
                    if (callable.equals(entry.getValue())) {
                        idsToRemove.add(uuid);
                    }
                } catch (SimonRemoteException ex) {
                    final String message = "Remote Object could not properly be processed and " +
                                           "will be disconnected from server.";
                    LOGGER.info(message + " " + ex.getMessage());
                    LOGGER.log(Level.FINE, "", ex);
                }
            }

            for (UUID uuid : idsToRemove) {
                clientMap.remove(uuid);
            }
            /* Observer informieren */
            this.setChangedAndUpdate(new DurakServiceEvent<List<UUID>>(DurakServiceEventType.CLIENT_LOGOUT, idsToRemove));
            /* Hier koennte man noch alle Clients benachrichtigen */
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

    public void setPassword(String password) {
        if (password != null) {
            this.password = password;
        } else {
            this.password = "";
        }
    }
}
