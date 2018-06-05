package com.labuda.gdlunch.mvc.controllers.rest.entities;

public class RestaurantRequest {

    private String restaurantName;

    public RestaurantRequest(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public RestaurantRequest() {
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
}
