package net.chrisrichardson.ftgo.restaurantservice.web;

import java.util.List;

public class GetRestaurantsResponse {
    private List<GetRestaurantResponse> restaurants;

    public GetRestaurantsResponse(List<GetRestaurantResponse> restaurants) {
        this.restaurants = restaurants;
    }

    public List<GetRestaurantResponse> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(List<GetRestaurantResponse> restaurants) {
        this.restaurants = restaurants;
    }
}
