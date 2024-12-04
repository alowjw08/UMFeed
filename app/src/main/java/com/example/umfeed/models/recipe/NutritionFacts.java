package com.example.umfeed.models.recipe;

import com.google.firebase.firestore.PropertyName;

public class NutritionFacts {
    @PropertyName("carbohydrates")
    private double carbohydrates;

    @PropertyName("protein")
    private double protein;

    @PropertyName("fats")
    private double fats;

    // Default constructor required for Firestore
    public NutritionFacts() {}

    // Getters and setters with PropertyName annotations
    @PropertyName("carbohydrates")
    public double getCarbohydrates() {
        return carbohydrates;
    }

    @PropertyName("carbohydrates")
    public void setCarbohydrates(double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    @PropertyName("protein")
    public double getProtein() {
        return protein;
    }

    @PropertyName("protein")
    public void setProtein(double protein) {
        this.protein = protein;
    }

    @PropertyName("fats")
    public double getFats() {
        return fats;
    }

    @PropertyName("fats")
    public void setFats(double fats) {
        this.fats = fats;
    }
}