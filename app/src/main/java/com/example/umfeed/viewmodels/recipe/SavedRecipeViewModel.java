package com.example.umfeed.viewmodels.recipe;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.umfeed.models.recipe.Recipe;
import com.example.umfeed.repositories.RecipeRepository;

import java.util.List;

public class SavedRecipeViewModel extends ViewModel {
    private final RecipeRepository repository;
    private final MutableLiveData<List<Recipe>> savedRecipes = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public RecipeRepository getRepository() {
        return repository;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<String> getError() {
        return error;
    }

    public SavedRecipeViewModel () {
        this.repository = new RecipeRepository();
        loadSavedRecipes();
    }

    public MutableLiveData<List<Recipe>> getSavedRecipes() {
        return savedRecipes;
    }
    public void loadSavedRecipes() {
        isLoading.setValue(true);
        repository.getSavedRecipes()
                .addOnSuccessListener(recipes -> {
                    savedRecipes.setValue(recipes);
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue(e.getMessage());
                    isLoading.setValue(false);
                });
    }

    public void saveRecipe(String recipeId) {
        repository.savedRecipe(recipeId).addOnFailureListener(e -> error.setValue(e.getMessage()));
    }

    private void removeSavedRecipe(String recipeId) {
        repository.removeSavedRecipe(recipeId).addOnFailureListener(e -> error.setValue(e.getMessage()));
    }
}
