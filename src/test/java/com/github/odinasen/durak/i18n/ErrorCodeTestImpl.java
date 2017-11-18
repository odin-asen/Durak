package com.github.odinasen.durak.i18n;

import com.github.odinasen.durak.business.exception.ErrorCode;

public enum ErrorCodeTestImpl
        implements ErrorCode {

    ARMAGEDDON(1),
    RED_ALERT(2),
    NOT_IN_MESSAGE_BUNDLE(3);

    private int errorNumber;

    ErrorCodeTestImpl(int errorNumber) {
        this.errorNumber = errorNumber;
    }

    @Override
    public int getNumber() {
        return errorNumber;
    }
}
