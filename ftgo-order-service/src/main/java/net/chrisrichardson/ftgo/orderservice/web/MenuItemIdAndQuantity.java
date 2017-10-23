package net.chrisrichardson.ftgo.orderservice.web;

public class MenuItemIdAndQuantity {

  private String menuItemId;
  private int quantity;

  public String getMenuItemId() {
    return menuItemId;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setMenuItemId(String menuItemId) {
    this.menuItemId = menuItemId;
  }

  public MenuItemIdAndQuantity(String menuItemId, int quantity) {

    this.menuItemId = menuItemId;
    this.quantity = quantity;

  }
}
