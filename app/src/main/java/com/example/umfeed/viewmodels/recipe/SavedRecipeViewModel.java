package com.example.umfeed.viewmodels.recipe;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.umfeed.models.recipe.Recipe;
import com.example.umfeed.repositories.RecipeRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SavedRecipeViewModel extends ViewModel {
    private final RecipeRepository repository;
    private final MutableLiveData<List<Recipe>> savedRecipes = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Boolean>> savedStates = new MutableLiveData<>(new HashMap<>());
    private final MutableLiveData<Boolean> isSaved = new MutableLiveData<>();
    private static final String TAG = "SavedRecipeViewModel";

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<Boolean> getIsSaved() {
        return isSaved;
    }

    public MutableLiveData<String> getError() {
        return error;
    }
    public LiveData<Map<String, Boolean>> getSavedStates() { return savedStates; }

    public SavedRecipeViewModel () {
        this.repository = new RecipeRepository();
        loadSavedRecipes();
    }

    public void checkIsSaved(String recipeId) {
        Log.d(TAG, "Checking if recipe is saved: " + recipeId);
        repository.isRecipeSaved(recipeId)
                .addOnSuccessListener(saved -> {
                    Log.d(TAG, "Recipe saved status: " + saved);
                    isSaved.setValue(saved);

                    // Also update map for consistency
                    Map<String, Boolean> currentStates = savedStates.getValue();
                    if (currentStates != null) {
                        currentStates.put(recipeId, saved);
                        savedStates.setValue(currentStates);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error checking saved status", e);
                    error.setValue(e.getMessage());
                });
    }

    public void toggleSaveRecipe(String recipeId) {
        Log.d(TAG, "Toggling save state for recipe: " + recipeId);
        isLoading.setValue(true);
        error.setValue(null);

        repository.toggleSaveRecipe(recipeId)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Successfully toggled save state");
                    isLoading.setValue(false);

                    // Update both states
                    Boolean currentSingleState = isSaved.getValue();
                    isSaved.setValue(currentSingleState != null ? !currentSingleState : true);

                    Map<String, Boolean> currentStates = savedStates.getValue();
                    if (currentStates != null) {
                        boolean currentState = currentStates.getOrDefault(recipeId, false);
                        currentStates.put(recipeId, !currentState);
                        savedStates.setValue(currentStates);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error toggling save state", e);
                    isLoading.setValue(false);
                    error.setValue(e.getMessage());
                });
    }

    public Boolean getSavedState(String recipeId) {
        Map<String, Boolean> states = savedStates.getValue();
        return states != null ? states.get(recipeId) : null;
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
}
