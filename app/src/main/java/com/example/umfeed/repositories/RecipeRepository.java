package com.example.umfeed.repositories;

import android.util.Log;

import com.example.umfeed.models.recipe.Recipe;
import com.example.umfeed.models.user.SavedRecipe;
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

    public RecipeRepository() {
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        recipesRef = db.collection("recipes");
    }

    public Task<QuerySnapshot> getAllRecipes() {
        return db.collection("recipes").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Recipe> recipeList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Recipe recipe = document.toObject(Recipe.class);
                        // Make sure to set the ID from the document
                        recipe.setId(document.getId());
                        recipeList.add(recipe);
                    }
                    // Log for debugging
                    Log.d("RecipeRepository", "Loaded " + recipeList.size() + " recipes");
                })
                .addOnFailureListener(e -> {
                    Log.e("RecipeRepository", "Error loading recipes", e);
                });
    }
    public Task<QuerySnapshot> getRecipesByCategory(String category) {
        return db.collection("recipes").whereArrayContains("categories", category).get();
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

    public Task<QuerySnapshot> searchRecipes(String query) {
        // Create array of search terms for partial matching
        String[] searchTerms = query.split("\\s+");

        // Create query to search in name and description fields
        Query searchQuery = recipesRef;

        // First, search for matches in recipe name
        searchQuery = searchQuery.orderBy("name")
                .startAt(query)
                .endAt(query + '\uf8ff');

        return searchQuery.get()
                .continueWithTask(task -> {
                    // If no results in name, search in description
                    if (task.isSuccessful() && task.getResult().isEmpty()) {
                        return recipesRef.orderBy("description")
                                .startAt(query)
                                .endAt(query + '\uf8ff')
                                .get();
                    }
                    return task;
                });
    }
}
