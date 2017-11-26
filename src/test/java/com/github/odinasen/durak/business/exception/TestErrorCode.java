package com.github.odinasen.durak.business.exception;

public enum TestErrorCode
        implements ErrorCode {
    ERROR_ONE_OH_ONE(101);

    private int errorNumber;

    TestErrorCode(int errorNumber) {
        this.errorNumber = errorNumber;
    }

    @Override
    public int getNumber() {
        return errorNumber;
    }
}
