package net.chrisrichardson.ftgo.consumerservice.domain.swagger;

public class ActionInfo {
  private DeliveryActionType type;

  public ActionInfo() {
  }

  public ActionInfo(DeliveryActionType type) {
    this.type = type;
  }

  public DeliveryActionType getType() {
    return type;
  }

  public void setType(DeliveryActionType type) {
    this.type = type;
  }
}
