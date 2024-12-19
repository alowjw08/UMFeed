package com.example.umfeed.models.foodbank;

import androidx.annotation.Keep;
import com.google.firebase.Timestamp;

@Keep
public class FoodBank {
    private String id;
    private String name;
    private String imageUrl;
    private Long dailyPin;  // Changed from String to Long
    private Timestamp pinTimestamp;

    public FoodBank() {
    }

    public FoodBank(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public FoodBank(String id, String name, String imageUrl, Long dailyPin, Timestamp pinTimestamp) { // Updated constructor
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.dailyPin = dailyPin;
        this.pinTimestamp = pinTimestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getDailyPin() {
        return dailyPin;
    }

    public void setDailyPin(Long dailyPin) {
        this.dailyPin = dailyPin;
    }

    // Helper method to get formatted pin as String
    public String getFormattedPin() {
        return dailyPin != null ? String.format("%04d", dailyPin) : null;
    }

    public Timestamp getPinTimestamp() {
        return pinTimestamp;
    }

    public void setPinTimestamp(Timestamp pinTimestamp) {
        this.pinTimestamp = pinTimestamp;
    }

    @Override
    public String toString() {
        return "FoodBank{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", dailyPin=" + dailyPin +
                ", pinTimestamp=" + pinTimestamp +
                '}';
    }
}