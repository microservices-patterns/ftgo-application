package net.chrisrichardson.ftgo.endtoendtests.dto;

import java.util.ArrayList;
import java.util.List;

public class RestaurantMenu {
    private List<MenuItem> menuItems = new ArrayList<>();

    public RestaurantMenu() {
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public RestaurantMenu addMenuItemsItem(MenuItem menuItem) {
        this.menuItems.add(menuItem);
        return this;
    }
}
