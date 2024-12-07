package com.example.umfeed.repositories;

import android.util.Log;

import com.example.umfeed.models.recipe.NutritionFacts;
import com.example.umfeed.models.recipe.NutritionFilter;
import com.example.umfeed.models.recipe.Range;
import com.example.umfeed.models.recipe.Recipe;
import com.example.umfeed.models.user.SavedRecipe;
import com.example.umfeed.utils.FuzzySearchUtil;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecipeRepository {
    private final FirebaseFirestore db;
    private final String userId;
    private final CollectionReference recipesRef;
    private static final String TAG = "RecipeRepository";

    public RecipeRepository() {
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        recipesRef = db.collection("recipes");
    }

    public Task<QuerySnapshot> getAllRecipes() {
        return db.collection("recipes").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Recipe recipe = document.toObject(Recipe.class);
                recipe.setId(document.getId()); // Set ID immediately when converting to object
                // Update the document in the snapshot
                document.getReference().set(recipe);
            }
        });
    }
    public Task<QuerySnapshot> getRecipesByCategory(String category) {
        // Add logging for debugging
        Log.d("RecipeRepository", "Filtering by category: " + category);

        return db.collection("recipes")
                .whereArrayContains("categories", category)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Ensure IDs are set
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Recipe recipe = document.toObject(Recipe.class);
                        recipe.setId(document.getId());
                        // Update the document with ID
                        document.getReference().set(recipe);
                    }
                    Log.d(TAG, "Found " + queryDocumentSnapshots.size() + " recipes for category: " + category);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error filtering by category: " + e.getMessage());
                });
    }

    public Task<List<Recipe>> getSavedRecipes() {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        CollectionReference savedRecipesRef = db.collection("users")
                .document(userId)
                .collection("savedRecipes");

        return savedRecipesRef
                .orderBy("savedAt", Query.Direction.DESCENDING)
                .get()
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    List<Task<DocumentSnapshot>> recipeTasks = new ArrayList<>();

                    for (QueryDocumentSnapshot savedRecipeDoc : task.getResult()) {
                        String recipeId = savedRecipeDoc.getString("recipeId");
                        Task<DocumentSnapshot> recipeTask = db.collection("recipes")
                                .document(recipeId)
                                .get();
                        recipeTasks.add(recipeTask);
                    }

                    return Tasks.whenAllSuccess(recipeTasks);
                })
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }

                    List<Recipe> recipes = new ArrayList<>();
                    // Cast objects to DocumentSnapshot safely
                    List<?> results = task.getResult();
                    for (Object result : results) {
                        if (result instanceof DocumentSnapshot) {
                            DocumentSnapshot recipeDoc = (DocumentSnapshot) result;
                            if (recipeDoc.exists()) {
                                Recipe recipe = recipeDoc.toObject(Recipe.class);
                                recipe.setId(recipeDoc.getId());
                                recipes.add(recipe);
                            }
                        }
                    }

                    return recipes;
                });
    }

    public Task<Void> savedRecipe (String recipeId) {
        SavedRecipe savedRecipe = new SavedRecipe();
        savedRecipe.setRecipeId(recipeId);
        savedRecipe.setSavedAt(Timestamp.now());

        return db.collection("users").document(userId).collection("savedRecipes").document(recipeId).set(savedRecipe);
    }

    public Task<Void> removeSavedRecipe(String recipeId) {
        return db.collection("users").document(userId).collection("savedRecipes").document(recipeId).delete();
    }

    public Task<Void> likeRecipe(String recipeId) {
        DocumentReference recipeRef = db.collection("recipes").document(recipeId);

        return db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(recipeRef);
            long newLikes = snapshot.getLong("likes") + 1;
            transaction.update(recipeRef, "likes", newLikes);
            return null;
        });
    }

    public Task<DocumentSnapshot> getRecipeById(String recipeId) {
        return db.collection("recipes").document(recipeId).get();
    }

    public Task<List<Recipe>> searchRecipes(String query) {
        // First get all recipes to perform fuzzy search on
        return getAllRecipes()
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    List<Recipe> allRecipes = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Recipe recipe = document.toObject(Recipe.class);
                        recipe.setId(document.getId());
                        allRecipes.add(recipe);
                    }

                    // Perform fuzzy search on background thread
                    return FuzzySearchUtil.fuzzySearch(allRecipes, query);
                });
    }

    public Task<List<Recipe>> getRecipesByNutrition(NutritionFilter filter) {
        Log.d(TAG, String.format("Applying nutrition filter with ranges:" +
                        "\nCarbs: %.1f-%.1f" +
                        "\nProtein: %.1f-%.1f" +
                        "\nFats: %.1f-%.1f" +
                        "\nMatch All: %b",
                filter.getCarbRange().getMin(), filter.getCarbRange().getMax(),
                filter.getProteinRange().getMin(), filter.getProteinRange().getMax(),
                filter.getFatRange().getMin(), filter.getFatRange().getMax(),
                filter.isMatchAll()
        ));

        return db.collection("recipes")
                .get()
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "Failed to fetch recipes", task.getException());
                        throw task.getException();
                    }

                    List<Recipe> filteredRecipes = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Recipe recipe = doc.toObject(Recipe.class);
                        recipe.setId(doc.getId());
                        if (recipe.getNutritionFacts() == null) {
                            Log.w(TAG, "Recipe " + recipe.getId() + " has no nutrition facts");
                            continue;
                        }
                        try {
                            if (isWithinNutritionRange(recipe, filter)) {
                                filteredRecipes.add(recipe);
                                Log.d(TAG, "Recipe " + recipe.getName() + " matches nutrition criteria");
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error processing recipe " + recipe.getId(), e);
                        }
                    }
                    return filteredRecipes;
                });
    }

    private boolean isWithinNutritionRange(Recipe recipe, NutritionFilter filter) {
        NutritionFacts facts = recipe.getNutritionFacts();

        if (facts == null) {
            Log.w(TAG, "Recipe " + recipe.getName() + " has null nutrition facts");
            return false;
        }

        boolean matchesCarbs = facts.getCarbohydrates() >= filter.getCarbRange().getMin() &&
                facts.getCarbohydrates() <= filter.getCarbRange().getMax();

        boolean matchesProtein = facts.getProtein() >= filter.getProteinRange().getMin() &&
                facts.getProtein() <= filter.getProteinRange().getMax();

        boolean matchesFats = facts.getFats() >= filter.getFatRange().getMin() &&
                facts.getFats() <= filter.getFatRange().getMax();

        Log.d(TAG, String.format("Match results for %s - Carbs: %b, Protein: %b, Fats: %b",
                recipe.getName(), matchesCarbs, matchesProtein, matchesFats));

        boolean matches;
        if (filter.isMatchAll()) {
            // AND logic - must match all criteria
            matches = matchesCarbs && matchesProtein && matchesFats;
        } else {
            // OR logic - must match at least one criteria
            matches = matchesCarbs || matchesProtein || matchesFats;
        }

        Log.d(TAG, String.format("Final match result for %s: %b (using %s logic)",
                recipe.getName(), matches, filter.isMatchAll() ? "AND" : "OR"));

        return matches;
    }
}
