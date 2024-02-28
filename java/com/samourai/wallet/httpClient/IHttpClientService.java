package com.samourai.wallet.httpClient;

public interface IHttpClientService {
  IHttpClient getHttpClient(HttpUsage httpUsage);
  void changeIdentity(); // should call httpProxy.changeIdentity()

  void stop();
}
