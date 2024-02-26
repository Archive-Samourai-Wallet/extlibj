package com.samourai.wallet.httpClient;

public interface IHttpClientService {
  IHttpClient getHttpClient(HttpUsage httpUsage);

  void stop();
}
