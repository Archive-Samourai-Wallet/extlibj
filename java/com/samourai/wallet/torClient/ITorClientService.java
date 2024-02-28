package com.samourai.wallet.torClient;

import com.samourai.wallet.httpClient.HttpProxy;

import java.util.Optional;

public interface ITorClientService {
  Optional<HttpProxy> getTorProxy();
  void changeIdentity();
}
