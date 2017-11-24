package com.github.odinasen.durak.business.exception;

public enum GameServerCode implements ErrorCode {
  SERVICE_ALREADY_RUNNING(501),
  NETWORK_ERROR(502),
  PORT_USED(503),
  STOP_SERVER_ERROR(515),
  NOT_ENOUGH_PLAYERS_FOR_GAME(516);

  private int errorNumber;

  GameServerCode(int errorNumber) {
    this.errorNumber = errorNumber;
  }

  @Override
  public int getNumber() {
    return errorNumber;
  }
}
