package net.chrisrichardson.ftgo.orderservice.sagaparticipants;


import net.chrisrichardson.ftgo.common.Money;

public class BeginReviseOrderReply {
  private Money revisedOrderTotal;

  public BeginReviseOrderReply(Money revisedOrderTotal) {
    this.revisedOrderTotal = revisedOrderTotal;
  }

  public BeginReviseOrderReply() {
  }

  public Money getRevisedOrderTotal() {
    return revisedOrderTotal;
  }

  public void setRevisedOrderTotal(Money revisedOrderTotal) {
    this.revisedOrderTotal = revisedOrderTotal;
  }
}
