package com.github.odinasen.durak.business.exception;

/**
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 18.10.14
 */
public enum GameClientCode
        implements ErrorCode {
    /**
     * Server wurde nicht gefunden.
     */
    SERVER_NOT_FOUND(401),

    /**
     * Service am Server wurde nicht gefunden.
     */
    SERVICE_NOT_FOUND(402),
    ALREADY_CONNECTED(421),
    UNKNOWN_CLIENT_EXCEPTION(499);

    private int errorNumber;

    GameClientCode(int errorNumber) {
        this.errorNumber = errorNumber;
    }

    @Override
    public int getNumber() {
        return errorNumber;
    }
}
