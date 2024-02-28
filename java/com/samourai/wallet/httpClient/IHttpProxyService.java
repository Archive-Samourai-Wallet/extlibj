package com.samourai.wallet.httpClient;

import java.util.Optional;

public interface IHttpProxyService {
  Optional<HttpProxy> getHttpProxy(HttpUsage httpUsage);
  void changeIdentity();
}
