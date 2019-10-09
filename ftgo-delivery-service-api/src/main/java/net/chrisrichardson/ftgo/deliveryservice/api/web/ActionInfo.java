package net.chrisrichardson.ftgo.deliveryservice.api.web;

public class ActionInfo {
  private String type;

  public ActionInfo() {
  }

  public ActionInfo(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
