package net.chrisrichardson.ftgo.endtoendtests.dto;

import java.util.List;

public class ReviseOrderRequest {
    private List<RevisedOrderLineItem> revisedOrderLineItems;

    public ReviseOrderRequest() {
    }

    public ReviseOrderRequest(List<RevisedOrderLineItem> revisedOrderLineItems) {
        this.revisedOrderLineItems = revisedOrderLineItems;
    }

    public List<RevisedOrderLineItem> getRevisedOrderLineItems() {
        return revisedOrderLineItems;
    }
}
