package net.chrisrichardson.ftgo.endtoendtests.dto;

public class CreateRestaurantRequest {
    private String name;
    private Address address;
    private RestaurantMenu menu;

    public CreateRestaurantRequest() {
    }

    public String getName() {
        return name;
    }

    public CreateRestaurantRequest name(String name) {
        this.name = name;
        return this;
    }

    public Address getAddress() {
        return address;
    }

    public CreateRestaurantRequest address(Address address) {
        this.address = address;
        return this;
    }

    public RestaurantMenu getMenu() {
        return menu;
    }

    public CreateRestaurantRequest menu(RestaurantMenu menu) {
        this.menu = menu;
        return this;
    }
}
