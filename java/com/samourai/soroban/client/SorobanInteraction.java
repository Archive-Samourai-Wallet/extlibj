package com.samourai.soroban.client;

import com.samourai.soroban.cahoots.CahootsContext;
import com.samourai.soroban.cahoots.TypeInteraction;

public abstract class SorobanInteraction implements SorobanReply {
  private TypeInteraction typeInteraction;
  private SorobanMessage replyAccept;
  private CahootsContext cahootsContext;

  public SorobanInteraction(TypeInteraction typeInteraction, SorobanMessage replyAccept, CahootsContext cahootsContext) {
    this.typeInteraction = typeInteraction;
    this.replyAccept = replyAccept;
    this.cahootsContext = cahootsContext;
  }

  public SorobanInteraction(SorobanInteraction interaction) {
    this.typeInteraction = interaction.getTypeInteraction();
    this.replyAccept = interaction.getReplyAccept();
    this.cahootsContext = interaction.getCahootsContext();
  }

  public TypeInteraction getTypeInteraction() {
    return typeInteraction;
  }

  public SorobanMessage getReplyAccept() {
    return replyAccept;
  }

  public CahootsContext getCahootsContext() {
    return cahootsContext;
  }

  @Override
  public String toString() {
    return typeInteraction.name();
  }
}
