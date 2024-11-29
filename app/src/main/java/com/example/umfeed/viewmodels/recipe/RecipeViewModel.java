package com.example.umfeed.viewmodels.recipe;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.umfeed.models.recipe.Recipe;
import com.example.umfeed.repositories.RecipeRepository;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RecipeViewModel extends ViewModel {
    private final RecipeRepository repository;
    private final MutableLiveData<List<Recipe>> recipes = new MutableLiveData<>();
    private final MutableLiveData<Recipe> recipeDetails = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public RecipeViewModel() {
        repository = new RecipeRepository();
        loadRecipes();
    }

    public MutableLiveData<List<Recipe>> getRecipes() {
        return recipes;
    }

    public MutableLiveData<Recipe> getRecipeDetails() {
        return recipeDetails;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<String> getError() {
        return error;
    }

    public void loadRecipes() {
        isLoading.setValue(true);
        repository.getAllRecipes()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Recipe> recipeList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Recipe recipe = document.toObject(Recipe.class);
                        recipeList.add(recipe);
                    }
                    recipes.setValue(recipeList);
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue(e.getMessage());
                    isLoading.setValue(false);
                });
    }

    public void loadRecipeDetails(String recipeId) {
        if (recipeId == null) {
            error.setValue("Invalid recipe ID");
            return;
        }

        isLoading.setValue(true);
        repository.getRecipeById(recipeId)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Recipe recipe = documentSnapshot.toObject(Recipe.class);
                        recipeDetails.setValue(recipe);
                    } else {
                        error.setValue("Recipe not found");
                    }
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue(e.getMessage());
                    isLoading.setValue(false);
                });
    }

    public void filterRecipesByCategory(String category) {
        isLoading.setValue(true);
        repository.getRecipesByCategory(category)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Recipe> filteredList = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Recipe recipe = documentSnapshot.toObject(Recipe.class);
                        filteredList.add(recipe);
                    }
                    recipes.setValue(filteredList);
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue(e.getMessage());
                    isLoading.setValue(false);
                });
    }

    public void likeRecipe(String recipeId) {
        repository.likeRecipe(recipeId)
                .addOnFailureListener(e -> {
                    error.setValue(e.getMessage());
                });
    }

    public void refreshRecipes() {
        loadRecipes();
    }

    public void searchRecipes(String query) {
        if (query == null || query.trim().isEmpty()) {
            loadRecipes(); // Load all recipes if query is empty
            return;
        }

        isLoading.setValue(true);
        repository.searchRecipes(query.toLowerCase().trim())
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Recipe> searchResults = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Recipe recipe = document.toObject(Recipe.class);
                        searchResults.add(recipe);
                    }
                    recipes.setValue(searchResults);
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Search failed: " + e.getMessage());
                    isLoading.setValue(false);
                });
    }
}