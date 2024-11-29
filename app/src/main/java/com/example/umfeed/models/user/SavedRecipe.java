package com.example.umfeed.models.user;


import com.google.firebase.Timestamp;

public class SavedRecipe {
    private String recipeId;
    private Timestamp savedAt;

    public SavedRecipe() {
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public Timestamp getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(Timestamp savedAt) {
        this.savedAt = savedAt;
    }
}
