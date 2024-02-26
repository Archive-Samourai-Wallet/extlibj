package com.samourai.wallet.stompClient;

public interface IStompMessage {
  String getStompHeader(String headerName);

  Object getPayload();
}
