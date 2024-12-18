package com.example.umfeed.models.foodbank;

import com.google.firebase.Timestamp;

import java.util.Map;

public class FoodBank {

    private String id;
    private final String name;
    private final String imageUrl;
    private String dailyPin;
    private Timestamp pinTimestamp;

    public FoodBank(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }
    public FoodBank(String id, String name, String imageUrl, String dailyPin, Timestamp pinTimestamp) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.dailyPin = dailyPin;
        this.pinTimestamp = pinTimestamp;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public String getDailyPin() {
        return dailyPin;
    }
    public Object getPinTimestamp() {
        return pinTimestamp;
    }
}
