package com.samourai.wallet.soroban.client;

import com.samourai.wallet.soroban.cahoots.TypeInteraction;

import java.util.concurrent.Callable;

public class SorobanInteraction implements SorobanReply {

  private SorobanMessage request;
  public TypeInteraction typeInteraction;
  private Callable<SorobanMessage> onAccept;

  public SorobanInteraction(
          SorobanMessage request,
          TypeInteraction typeInteraction, Callable<SorobanMessage> onAccept) {
    this.request = request;
    this.typeInteraction = typeInteraction;
    this.onAccept = onAccept;
  }

  public SorobanMessage getRequest() {
    return request;
  }

  public TypeInteraction getTypeInteraction() {
    return typeInteraction;
  }

  public SorobanMessage accept() throws Exception {
    return onAccept.call();
  }

  @Override
  public String toString() {
    return typeInteraction.name();
  }
}
