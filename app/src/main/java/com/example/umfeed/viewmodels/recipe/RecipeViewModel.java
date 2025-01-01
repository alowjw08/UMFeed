package com.example.umfeed.viewmodels.recipe;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.umfeed.models.recipe.NutritionFilter;
import com.example.umfeed.models.recipe.Recipe;
import com.example.umfeed.repositories.RecipeRepository;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecipeViewModel extends ViewModel {
    private final RecipeRepository repository;
    private final MutableLiveData<List<Recipe>> recipes = new MutableLiveData<>();
    private final MutableLiveData<Recipe> recipeDetails = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private static final String TAG = "RecipeViewModel";
    private boolean isFiltering = false;

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
        if (isFiltering) {
            Log.d(TAG, "Skipping loadRecipes during filter operation");
            return;
        }
        Log.d(TAG, "Loading all recipes");
        isLoading.setValue(true);
        repository.getAllRecipes()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Recipe> recipeList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Recipe recipe = document.toObject(Recipe.class);
                        recipe.setId(document.getId());
                        recipeList.add(recipe);
                    }
                    Log.d(TAG, "Setting all recipes - size: " + recipeList.size());
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
        Log.d("RecipeViewModel", "Filtering recipes by category: " + category);
        isLoading.setValue(true);
        error.setValue(null); // Clear any previous errors

        repository.getRecipesByCategory(category)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Recipe> filteredList = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Recipe recipe = documentSnapshot.toObject(Recipe.class);
                        recipe.setId(documentSnapshot.getId());
                        filteredList.add(recipe);
                    }
                    Log.d("RecipeViewModel", "Found " + filteredList.size() + " recipes for category: " + category);
                    recipes.setValue(filteredList);
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    Log.e("RecipeViewModel", "Error filtering recipes: " + e.getMessage());
                    error.setValue("Failed to filter recipes: " + e.getMessage());
                    isLoading.setValue(false);
                });
    }
    public void searchRecipes(String query) {
        if (query == null || query.trim().isEmpty()) {
            loadRecipes();
            return;
        }

        isLoading.setValue(true);
        error.setValue(null);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            repository.searchRecipes(query)
                    .addOnSuccessListener(searchResults -> {
                        recipes.postValue(searchResults);
                        isLoading.postValue(false);
                    })
                    .addOnFailureListener(e -> {
                        error.postValue("Search failed: " + e.getMessage());
                        isLoading.postValue(false);
                    });
        });
    }

    public void getRecipesByNutrition(NutritionFilter filter) {
        if (!isFiltering) {
            return;
        }

        isLoading.setValue(true);
        error.setValue(null);
        repository.getRecipesByNutrition(filter)
                .addOnSuccessListener(filteredRecipes -> {
                    Log.d(TAG, "Filter success - recipes size: " + filteredRecipes.size());

                    if (filteredRecipes.isEmpty()) {
                        Log.d(TAG, "Setting empty list to recipes LiveData");
                        recipes.setValue(new ArrayList<>());
                        error.setValue("No recipes match the selected nutrition criteria");
                    } else {
                        Log.d(TAG, "Setting filtered list to recipes LiveData");
                        recipes.setValue(filteredRecipes);
                    }
                    isLoading.setValue(false);
                    isFiltering = false;
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Filter failed: " + e.getMessage());
                    error.setValue(e.getMessage());
                    isLoading.setValue(false);
                    isFiltering = false;
                });
    }

    public void clearRecipes() {
        Log.d(TAG, "Clearing recipes before filter");
        isFiltering = true;
        recipes.postValue(null);
    }
}