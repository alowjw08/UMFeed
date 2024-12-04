package com.example.umfeed.models.recipe;

import com.google.firebase.firestore.PropertyName;

import java.util.List;
import java.util.Objects;

public class Recipe {
    private String id;
    private String name;
    private String imageUrl;
    private String description;
    private int calories;
    private int likes;
    private List<String> ingredients;
    private List<String> allergens;
    private List<String> steps;
    private List<String> categories;
    @PropertyName("nutritionFacts") // Add Firestore annotation
    private NutritionFacts nutritionFacts;

    public Recipe() {
    }

    public void incrementLikes(){
        likes++;
    }

    public void decrementLikes(){
        likes--;
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

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
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
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
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
