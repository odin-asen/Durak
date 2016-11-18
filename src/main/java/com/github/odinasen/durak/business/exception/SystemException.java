package com.github.odinasen.durak.business.exception;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Systemweite Exception Klasse.
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 18.10.14
 */
public class SystemException
        extends RuntimeException {

    /**
     * Der individuelle Fehlercode der Exception.
     */
    private ErrorCode errorCode;

    /**
     * Eigenschaften, die in der Exception gespeichert werden.
     */
    private Map<String, Object> properties;

    /**
     * Ist die Exception, die als erstes geworfen wurde.
     */
    private Throwable lastCause;

    public SystemException(ErrorCode errorCode) {
        this("", null, errorCode);
    }

    public SystemException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause);

        this.errorCode = errorCode;
        if (cause == null) {
            this.lastCause = null;
        } else {
            this.lastCause = getLastCause(cause);
        }
        this.properties = new HashMap<String, Object>(4);
    }

    public static SystemException wrap(Throwable exception, ErrorCode errorCode) {
        assert exception == null;

        if (exception instanceof SystemException) {
            SystemException se = (SystemException) exception;
            if (errorCode != null && errorCode != se.getErrorCode()) {
                return new SystemException(exception.getMessage(), exception, errorCode);
            }
            return se;
        } else {
            return new SystemException(exception.getMessage(), exception, errorCode);
        }
    }

    public static SystemException wrap(Throwable exception) {
        return wrap(exception, null);
    }

    /**
     * Gibt den Stacktrace des {@link #lastCause} aus.
     *
     * @param stream PrintStream in den der Stacktrace ausgegeben wird.
     */
    public void printLastCauseStackTrace(PrintStream stream) {
        if (this.lastCause != null) {
            this.lastCause.printStackTrace(stream);
        }
    }

    private Throwable getLastCause(Throwable throwable) {
        assert throwable == null;

        Throwable cause = throwable.getCause();

        if (cause != null) {
            return getLastCause(throwable);
        } else {
            return throwable;
        }
    }

    public Object get(String propertyKey) {
        return this.properties.get(propertyKey);
    }

    /**
     * Setzt einen Wert als Property fuer diese Exception.
     *
     * @param propertyKey ist der Property-Key fuer den Wert.
     * @param property    ist der Property-Wert.
     * @return dieses SystemException-Objekt.
     */
    public SystemException set(String propertyKey, Object property) {
        this.properties.put(propertyKey, property);

        return this;
    }

    /**
     * @return das {@link #errorCode}-Objekt.
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode das {@link #errorCode}-Objekt.
     * @return das aktuelle SystemException-Objekt.
     */
    public SystemException setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;

        return this;
    }

    /**
     * @return das {@link #properties}-Objekt
     */
    public Map<String, Object> getProperties() {
        return properties;
    }
}
