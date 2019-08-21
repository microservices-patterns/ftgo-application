package net.chrisrichardson.ftgo.deliveryservice.api.web;

import java.util.List;

public class DeliveryStatus {
  private DeliveryInfo deliveryInfo;
  private Long assignedCourier;
  private List<ActionInfo> courierActions;

  public DeliveryStatus() {
  }

  public DeliveryInfo getDeliveryInfo() {
    return deliveryInfo;
  }

  public void setDeliveryInfo(DeliveryInfo deliveryInfo) {
    this.deliveryInfo = deliveryInfo;
  }

  public Long getAssignedCourier() {
    return assignedCourier;
  }

  public void setAssignedCourier(Long assignedCourier) {
    this.assignedCourier = assignedCourier;
  }

  public List<ActionInfo> getCourierActions() {
    return courierActions;
  }

  public void setCourierActions(List<ActionInfo> courierActions) {
    this.courierActions = courierActions;
  }

  public DeliveryStatus(DeliveryInfo deliveryInfo, Long assignedCourier, List<ActionInfo> courierActions) {
    this.deliveryInfo = deliveryInfo;
    this.assignedCourier = assignedCourier;
    this.courierActions = courierActions;
  }
}
