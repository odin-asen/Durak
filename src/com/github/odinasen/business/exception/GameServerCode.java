package com.github.odinasen.business.exception;

/**
 * <p/>
 * Author: Timm Herrmann<br/>
 * Date: 14.03.15
 */
public enum GameServerCode implements ErrorCode {
  SERVICE_ALREADY_RUNNING(501),
  NETWORK_ERROR(502),
  PORT_USED(503),
  STOP_SERVER_ERROR(515);

  private int errorNumber;

  private GameServerCode(int errorNumber) {
    this.errorNumber = errorNumber;
  }

  @Override
  public int getNumber() {
    return errorNumber;
  }
}
