package net.chrisrichardson.ftgo.orderservice.api.web;

import net.chrisrichardson.ftgo.common.RevisedOrderLineItem;

public class ReviseOrderRequest {
  private RevisedOrderLineItem[] revisedOrderLineItems;

  private ReviseOrderRequest() {
  }

  public ReviseOrderRequest(RevisedOrderLineItem[] revisedOrderLineItems) {
    this.revisedOrderLineItems = revisedOrderLineItems;
  }

  public RevisedOrderLineItem[] getRevisedOrderLineItems() {
    return revisedOrderLineItems;
  }

  public void setRevisedOrderLineItems(RevisedOrderLineItem[] revisedOrderLineItems) {
    this.revisedOrderLineItems = revisedOrderLineItems;
  }
}
