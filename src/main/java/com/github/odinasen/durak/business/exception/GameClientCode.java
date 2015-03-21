package com.github.odinasen.durak.business.exception;

/**
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 18.10.14
 */
public enum GameClientCode implements ErrorCode {
  SERVER_NOT_FOUND(401),
  SERVICE_NOT_FOUND(402);

  private int errorNumber;

  private GameClientCode(int errorNumber) {
    this.errorNumber = errorNumber;
  }

  @Override
  public int getNumber() {
    return errorNumber;
  }
}
