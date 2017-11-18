package com.github.odinasen.durak.business.exception;

/**
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 18.10.14
 */
public enum ApplicationParameterCode
        implements ErrorCode {
    /**
     * Ein Parameter wurde nicht gesetzt und konnte nicht geparst werden. Sollte nicht als Fehler, sondern als Info
     * gehandhabt werden.
     */
    PARAMETER_NOT_SET(1),

    /**
     * Falscher Typ eines Parameters beim Parsen.
     */
    WRONG_TYPE(2);

    /**
     * Property-Name fuer einen Parameter-Namen.
     */
    public static final String PN_PARAM_NAME = "paramName";
    private int errorNumber;

    ApplicationParameterCode(int errorNumber) {
        this.errorNumber = errorNumber;
    }

    @Override
    public int getNumber() {
        return errorNumber;
    }
}
