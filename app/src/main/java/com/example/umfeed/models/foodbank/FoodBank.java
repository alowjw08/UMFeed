package com.example.umfeed.models.foodbank;

public class FoodBank {

    private final String name;
    private final int imageResId; // Drawable resource ID

    public FoodBank(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }
    public int getImageResId() {
        return imageResId;
    }
}
