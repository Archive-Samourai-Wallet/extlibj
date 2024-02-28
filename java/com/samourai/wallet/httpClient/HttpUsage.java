package com.samourai.wallet.httpClient;

public enum HttpUsage {
  BACKEND(),
  SOROBAN(),
  COORDINATOR_REST(),
  COORDINATOR_REGISTER_OUTPUT();

  HttpUsage() {
  }
}
