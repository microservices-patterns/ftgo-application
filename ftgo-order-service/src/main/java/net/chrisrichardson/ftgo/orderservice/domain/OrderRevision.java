package net.chrisrichardson.ftgo.orderservice.domain;

import java.util.Map;
import java.util.Optional;

public class OrderRevision {

  private Optional<DeliveryInformation> deliveryInformation = Optional.empty();
  private Map<String, Integer> revisedLineItemQuantities;

  private OrderRevision() {
  }

  public OrderRevision(Optional<DeliveryInformation> deliveryInformation, Map<String, Integer> revisedLineItemQuantities) {
    this.deliveryInformation = deliveryInformation;
    this.revisedLineItemQuantities = revisedLineItemQuantities;
  }

  public void setDeliveryInformation(Optional<DeliveryInformation> deliveryInformation) {
    this.deliveryInformation = deliveryInformation;
  }

  public void setRevisedLineItemQuantities(Map<String, Integer> revisedLineItemQuantities) {
    this.revisedLineItemQuantities = revisedLineItemQuantities;
  }

  public Optional<DeliveryInformation> getDeliveryInformation() {
    return deliveryInformation;
  }


  public Map<String, Integer> getRevisedLineItemQuantities() {
    return revisedLineItemQuantities;
  }
}
