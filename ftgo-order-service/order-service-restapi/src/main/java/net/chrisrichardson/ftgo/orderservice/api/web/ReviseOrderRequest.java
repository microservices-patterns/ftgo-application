package net.chrisrichardson.ftgo.orderservice.api.web;

import net.chrisrichardson.ftgo.common.RevisedOrderLineItem;

import java.util.List;

public class ReviseOrderRequest {
  private List<RevisedOrderLineItem> revisedOrderLineItems;

  private ReviseOrderRequest() {
  }

  public ReviseOrderRequest(List<RevisedOrderLineItem> revisedOrderLineItems) {
    this.revisedOrderLineItems = revisedOrderLineItems;
  }

  public List<RevisedOrderLineItem> getRevisedOrderLineItems() {
    return revisedOrderLineItems;
  }

  public void setRevisedOrderLineItems(List<RevisedOrderLineItem> revisedOrderLineItems) {
    this.revisedOrderLineItems = revisedOrderLineItems;
  }
}
