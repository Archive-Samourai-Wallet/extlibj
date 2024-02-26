package com.samourai.wallet.httpClient;

public interface IHttpClientService {
  IHttpClient getHttpClient();

  void stop();
}
