package com.example.umfeed.utils;

import com.example.umfeed.models.recipe.Recipe;

import java.util.List;
import java.util.stream.Collectors;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class FuzzySearchUtil {
    private static final int SCORE_THRESHOLD = 65;

    public static List<Recipe> fuzzySearch(List<Recipe> recipes, String query) {
        if (query == null || query.trim().isEmpty()) {
            return recipes;
        }

        return recipes.stream()
                .map(recipe -> {
                    // Calculate fuzzy match scores for both name and description
                    int nameScore = FuzzySearch.weightedRatio(query.toLowerCase(),
                            recipe.getName().toLowerCase());
                    int descScore = FuzzySearch.partialRatio(query.toLowerCase(),
                            recipe.getDescription().toLowerCase());

                    // Take the better score between name and description
                    recipe.setSearchScore(Math.max(nameScore, descScore));
                    return recipe;
                })
                .filter(recipe -> recipe.getSearchScore() >= SCORE_THRESHOLD)
                .sorted((r1, r2) -> Integer.compare(r2.getSearchScore(), r1.getSearchScore()))
                .collect(Collectors.toList());
    }
}
