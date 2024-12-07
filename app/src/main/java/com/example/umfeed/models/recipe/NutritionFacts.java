package com.example.umfeed.models.recipe;

import com.google.firebase.firestore.PropertyName;

public class NutritionFacts {
    @PropertyName("carbohydrates")
    private float carbohydrates;

    @PropertyName("protein")
    private float protein;

    @PropertyName("fats")
    private float fats;

    // Default constructor required for Firestore
    public NutritionFacts() {}

    // Getters and setters with PropertyName annotations
    @PropertyName("carbohydrates")
    public float getCarbohydrates() {
        return carbohydrates;
    }

    @PropertyName("carbohydrates")
    public void setCarbohydrates(float carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    @PropertyName("protein")
    public float getProtein() {
        return protein;
    }

    @PropertyName("protein")
    public void setProtein(float protein) {
        this.protein = protein;
    }

    @PropertyName("fats")
    public float getFats() {
        return fats;
    }

    @PropertyName("fats")
    public void setFats(float fats) {
        this.fats = fats;
    }
}