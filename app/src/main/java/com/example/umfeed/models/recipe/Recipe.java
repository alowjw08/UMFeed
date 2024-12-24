package com.example.umfeed.models.recipe;

import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Recipe {
    @PropertyName("id")
    private String id;
    private String name;
    private String imageUrl;
    private String description;
    private int calories;
    private List<String> ingredients;
    private List<String> allergens;
    private List<String> steps;
    private List<String> categories;
    @PropertyName("nutritionFacts") // Add Firestore annotation
    private NutritionFacts nutritionFacts;
    private int searchScore;

    public int getSearchScore() {
        return searchScore;
    }

    public void setSearchScore(int searchScore) {
        this.searchScore = searchScore;
    }

    public Recipe() {
    }

    @PropertyName("id")
    public String getId() {
        return id;
    }

    @PropertyName("id")
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getAllergens() {
        return allergens;
    }

    public void setAllergens(List<String> allergens) {
        this.allergens = allergens;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }

    public List<String> getCategories() {
        return categories != null ? categories : new ArrayList<>();
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public boolean hasCategory(String category) {
        return categories != null && categories.contains(category);
    }

    @PropertyName("nutritionFacts")
    public NutritionFacts getNutritionFacts() {
        return nutritionFacts;
    }

    @PropertyName("nutritionFacts")
    public void setNutritionFacts(NutritionFacts nutritionFacts) {
        this.nutritionFacts = nutritionFacts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipe recipe = (Recipe) o;
        return Objects.equals(getId(), recipe.getId()) &&
                Objects.equals(getName(), recipe.getName()) &&
                Objects.equals(getImageUrl(), recipe.getImageUrl()) &&
                Objects.equals(getCalories(), recipe.getCalories());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getImageUrl(), getCalories());
    }
}
