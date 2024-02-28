package com.samourai.wallet.httpClient;

public enum HttpUsage {
  BACKEND(true),
  SOROBAN(true),
  COORDINATOR_REST(true),
  COORDINATOR_WEBSOCKET(false),
  COORDINATOR_REGISTER_OUTPUT(true);

  private boolean rest;

  HttpUsage(boolean rest) {
    this.rest = rest;
  }

  public boolean isRest() {
    return rest;
  }
}
