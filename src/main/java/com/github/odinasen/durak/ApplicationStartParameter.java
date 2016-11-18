package com.github.odinasen.durak;

import com.github.odinasen.durak.business.exception.ApplicationParameterCode;
import com.github.odinasen.durak.business.exception.SystemException;
import com.github.odinasen.durak.util.LoggingUtility;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by tih on 16.09.2016.
 * <p>
 * Klasse mit Standardwerten, die bei Start von unterschiedlichen Komponenten verwendet werden koennen.
 */
public class ApplicationStartParameter {
    /**
     * Start-Parameter fuer den Port beim initalen Start des Servers.
     */
    public static final String PARAM_SERVER_PORT = "sPort";
    /**
     * Start-Parameter fuer das Server-Passwort beim initialen Start des Servers.
     */
    public static final String PARAM_SERVER_PW = "sPwd";
    /**
     * Start-Parameter fuer die Adresse des Servers, mit der sich ein Client verbindet.
     */
    public static final String PARAM_CLIENT_ADDRESS = "cAddress";
    /**
     * Start-Parameter fuer den Port beim initalen Start des Clients.
     */
    public static final String PARAM_CLIENT_PORT = "cPort";
    /**
     * Start-Parameter fuer das Server-Passwort beim initialen Start des Clients.
     */
    public static final String PARAM_CLIENT_PW = "cPwd";
    /**
     * Start-Parameter fuer den Client-Namen beim initalen Start des Clients.
     */
    public static final String PARAM_CLIENT_NAME = "cName";

    private static final Logger LOGGER = LoggingUtility.getLogger(ApplicationStartParameter.class);
    private static ApplicationStartParameter instance = null;

    private Integer serverPort;
    private String serverPwd;

    private String clientConnectionAddress;
    private Integer clientPort;
    private String clientPwd;
    private String clientName;

    private ApplicationStartParameter() {
        this(null);
    }

    private ApplicationStartParameter(Map<String, String> paramMap) {
        if (paramMap != null) {
            try {
                this.serverPort = parseIntegerParameter(paramMap, PARAM_SERVER_PORT);

                if (!this.isPortOkay(this.serverPort)) {
                    LOGGER.warning(String.format("Server port is smaller or equal to zero or not parsable. Port: %s",
                                                 this.serverPort));
                }
            } catch (SystemException ex) {
                if (ex.getErrorCode() == ApplicationParameterCode.PARAMETER_NOT_SET) {
                    LOGGER.fine("Parameter '" + ex.get(ApplicationParameterCode.PN_PARAM_NAME) +
                                "' not set in start parameter.");
                }
            }

            this.serverPwd = paramMap.get(PARAM_SERVER_PW);

            this.clientConnectionAddress = paramMap.get(PARAM_CLIENT_ADDRESS);
            this.clientName = paramMap.get(PARAM_CLIENT_NAME);
            try {
                this.clientPort = parseIntegerParameter(paramMap, PARAM_CLIENT_PORT);

                if (!this.isPortOkay(this.clientPort)) {
                    LOGGER.warning(String.format("Client port is smaller or equal to zero or not parsable. Port: %s",
                                                 this.clientPort));
                }
            } catch (SystemException ex) {
                if (ex.getErrorCode() == ApplicationParameterCode.PARAMETER_NOT_SET) {
                    LOGGER.fine("Parameter '" + ex.get(ApplicationParameterCode.PN_PARAM_NAME) +
                                "' not set in start parameter.");
                }
            }

            this.clientPwd = paramMap.get(PARAM_CLIENT_PW);
        }
    }

    /**
     * Fuehrt {@link #getInstance(Map, boolean)} mit false fuer {@code reinitialise} aus.
     *
     * @param paramMap
     * @return
     */
    public static synchronized ApplicationStartParameter getInstance(Map<String, String> paramMap) {
        return getInstance(paramMap, false);
    }

    /**
     * Gibt das Singleton-Objekt dieser Klasse zurueck. Ist das Objekt null, wird dieses initialisert. Das Objekt kann
     * mit einem Parameter neu initialisiert werden.
     *
     * @param paramMap
     * @param reinitialise True, Singleton-Objekt wird mit der Parameter-Map neu initialisiert.
     *                     False, das Singleton-Objekt wird nur initialisiert, wenn es noch null ist.
     */
    public static synchronized ApplicationStartParameter getInstance(Map<String, String> paramMap,
                                                                     boolean reinitialise) {
        if (instance == null || reinitialise) {
            instance = new ApplicationStartParameter(paramMap);
        }

        return instance;
    }

    /**
     * Liefert die Singleton-Instanz zurueck. Wenn noch nicht erstellt, wird es <b>ohne</b> Parameter erstellt.
     * Eine Warnung wird dann geloggt.
     *
     * @return
     */
    public static synchronized ApplicationStartParameter getInstance() {
        if (instance == null) {
            instance = new ApplicationStartParameter();
            LOGGER.warning("ApplicationStartParameter singleton intialized without parameter map.");
        }

        return instance;
    }

    /**
     * Liest einen Wert aus der Map aus und parst diesen zu einem Integer.
     * Logwarnung wird ausgegeben, wenn der Wert nicht geparst werden konnte.
     *
     * @param paramMap  Parameter Map aus der ein Wert gelesen wrid
     * @param paramName Key des Parameterwerts
     * @throws SystemException - setzt {@link com.github.odinasen.durak.business.exception.ApplicationParameterCode}
     *                         als Errorcode zu Informationszwecken, wenn der Parameter gar nicht gesetzt wurde.
     */
    private Integer parseIntegerParameter(Map<String, String> paramMap, String paramName) throws SystemException {
        String value = paramMap.get(paramName);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ex) {
                LOGGER.warning(String.format("Could not parse '%s' as Integer", value));
            }
        } else {
            throw new SystemException(ApplicationParameterCode.PARAMETER_NOT_SET)
                    .set(ApplicationParameterCode.PN_PARAM_NAME, paramName);
        }

        return null;
    }

    /**
     * Gibt true zurueck, wenn der uebergebene Port groesser ist als 0 und nich null ist.
     */
    private boolean isPortOkay(Integer port) {
        return port != null && port > 0;
    }

    /**
     * @return Liefert den vorkonfigurierten Wert des Serverports zurueck. Wurde dieser nicht, oder falsch konfiguriert,
     * wird 0 zurueckgeliefert.
     */
    public int getServerPort() {
        if (this.isPortOkay(serverPort)) {
            return serverPort;
        } else {
            return 0;
        }
    }

    public String getServerPwd() {
        return serverPwd;
    }

    public String getClientConnectionAddress() {
        return clientConnectionAddress;
    }

    /**
     * @return Liefert den vorkonfigurierten Wert des Clientports zurueck. Wurde dieser nicht, oder falsch konfiguriert,
     * wird 0 zurueckgeliefert.
     */
    public int getClientPort() {
        if (this.isPortOkay(clientPort)) {
            return clientPort;
        } else {
            return 0;
        }
    }

    public String getClientPwd() {
        return clientPwd;
    }

    public String getClientName() {
        return clientName;
    }

    /**
     * Gibt true zurueck, wenn Parameter uebergeben wurden, mit denen der Server gestartet werden sollte.
     * Ansonsten false.
     */
    public boolean canInitialStartServer() {
        return isPortOkay(this.serverPort);
    }

    /**
     * Gibt true zurueck, wenn Parameter uebergeben wurden, mit denen der Client einen Verbindungsversuch zum Server
     * starten kann. D.h. Name gesetzt, Port gesetzt, etc...
     * Ansonsten false.
     */
    public boolean canInitialConnectToServer() {
        return isPortOkay(this.clientPort) && this.clientName != null && this.clientConnectionAddress != null;
    }
}
