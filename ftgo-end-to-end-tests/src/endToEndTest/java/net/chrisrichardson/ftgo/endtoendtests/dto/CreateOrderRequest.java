package net.chrisrichardson.ftgo.endtoendtests.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CreateOrderRequest {
    private int consumerId;
    private int restaurantId;
    private Address deliveryAddress;
    private LocalDateTime deliveryTime;
    private List<LineItem> lineItems;

    public CreateOrderRequest() {
    }

    public CreateOrderRequest(int consumerId, int restaurantId, Address deliveryAddress,
                              LocalDateTime deliveryTime, List<LineItem> lineItems) {
        this.consumerId = consumerId;
        this.restaurantId = restaurantId;
        this.deliveryAddress = deliveryAddress;
        this.deliveryTime = deliveryTime;
        this.lineItems = lineItems;
    }

    public int getConsumerId() {
        return consumerId;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public LocalDateTime getDeliveryTime() {
        return deliveryTime;
    }

    public List<LineItem> getLineItems() {
        return lineItems;
    }

    public static class LineItem {
        private String menuItemId;
        private int quantity;

        public LineItem() {
        }

        public LineItem(String menuItemId, int quantity) {
            this.menuItemId = menuItemId;
            this.quantity = quantity;
        }

        public String getMenuItemId() {
            return menuItemId;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}
