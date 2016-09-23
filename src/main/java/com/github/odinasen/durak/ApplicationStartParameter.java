package com.github.odinasen.durak;

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
    private static final Logger LOGGER = LoggingUtility.getLogger(ApplicationStartParameter.class);
    private static ApplicationStartParameter instance = null;

    private Integer serverPort;
    private String serverPwd;

    private ApplicationStartParameter() {
        this(null);
    }

    private ApplicationStartParameter(Map<String, String> paramMap) {
        if (paramMap != null) {
            this.serverPort = parseIntegerParameter(paramMap, PARAM_SERVER_PORT);
            if (!this.isServerPortOkay()) {
                LOGGER.warning(String.format("Port is smaller or equal to zero or not parsable. Port: %s", this.serverPort));
            }
            this.serverPwd = paramMap.get(PARAM_SERVER_PW);
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
    public static synchronized ApplicationStartParameter getInstance(Map<String, String> paramMap, boolean reinitialise) {
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
     */
    private Integer parseIntegerParameter(Map<String, String> paramMap, String paramName) {
        String value = paramMap.get(paramName);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ex) {
                LOGGER.warning(String.format("Could not parse '%s' as Integer", value));
            }
        }

        return null;
    }

    /**
     * Gibt true zurueck, wenn der Serverport groesser ist als 0 oder geparst wurde. Andernfalls false.
     */
    private boolean isServerPortOkay() {
        return this.serverPort != null && this.serverPort > 0;
    }

    /**
     * @return Liefert den vorkonfigurierten Wert des Serverports zurueck. Wurde dieser nicht, oder falsch konfiguriert,
     * wird 0 zurueckgeliefert.
     */
    public int getServerPort() {
        if (this.isServerPortOkay()) {
            return serverPort;
        } else {
            return 0;
        }
    }

    public String getServerPwd() {
        return serverPwd;
    }

    /** Gibt true zurueck, wenn Parameter uebergeben wurden, mit denen der Server gestartet werden sollte.
     * Ansonsten false.
     */
    public boolean canInitialStartServer() {
        return isServerPortOkay();
    }
}
