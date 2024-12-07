package com.example.umfeed.views.recipe;

// views/recipe/RecipeDetailFragment.java

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.umfeed.R;
import com.example.umfeed.databinding.FragmentRecipeDetailBinding;
import com.example.umfeed.models.recipe.NutritionFacts;
import com.example.umfeed.models.recipe.Recipe;
import com.example.umfeed.viewmodels.recipe.RecipeViewModel;
import com.example.umfeed.viewmodels.recipe.SavedRecipeViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

public class RecipeDetailFragment extends Fragment {
    private RecipeViewModel viewModel;
    private SavedRecipeViewModel savedRecipeViewModel;
    private FragmentRecipeDetailBinding binding;
    private String recipeId;
    private static final String TAG = "RecipeDetailFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
        savedRecipeViewModel = new ViewModelProvider(this).get(SavedRecipeViewModel.class);
        recipeId = RecipeDetailFragmentArgs.fromBundle(requireArguments()).getRecipeId();
        if (getArguments() != null) {
            String recipeId = getArguments().getString("recipeId");
            Log.d("RecipeDetailFragment", "Received recipe ID: " + recipeId);
        } else {
            Log.e("RecipeDetailFragment", "No recipe ID received");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecipeDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
        observeViewModel();
        setupObservers();
        viewModel.loadRecipeDetails(recipeId);

        if (recipeId != null) {
            Log.d(TAG, "Checking initial save state for recipe: " + recipeId);
            savedRecipeViewModel.checkIsSaved(recipeId);
        }
    }

    private void setupViews() {
        savedRecipeViewModel.checkIsSaved(recipeId);

        binding.saveButton.setOnClickListener(v -> {
            if (recipeId == null) {
                Log.e(TAG, "Cannot save recipe: recipeId is null");
                Snackbar.make(binding.getRoot(), "Cannot save recipe", Snackbar.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "Save button clicked for recipe: " + recipeId);
            savedRecipeViewModel.toggleSaveRecipe(recipeId);
        });
        binding.likeButton.setOnClickListener(v -> {
            viewModel.likeRecipe(recipeId);
        });
    }

    private void setupObservers() {
        // Observe saved state
        savedRecipeViewModel.getIsSaved().observe(getViewLifecycleOwner(), isSaved -> {
            Log.d(TAG, "Save state changed: " + isSaved);
            updateSaveButtonIcon(isSaved);
        });

        savedRecipeViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            Log.d(TAG, "Loading state changed: " + isLoading);
            binding.saveButton.setEnabled(!isLoading);

            // Only show loading if we have a saved state to return to
            if (isLoading) {
                binding.saveProgressBar.setVisibility(View.VISIBLE);
            } else {
                binding.saveProgressBar.setVisibility(View.GONE);
            }
        });

        savedRecipeViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Log.e(TAG, "Error updating save state: " + error);
                Snackbar.make(binding.getRoot(), "Failed to update saved status: " + error,
                        Snackbar.LENGTH_LONG).show();
                binding.saveButton.setEnabled(true);
                binding.saveProgressBar.setVisibility(View.GONE);
            }
        });
    }
    private void updateSaveButtonIcon(boolean isSaved) {
        binding.saveButton.setIcon(ContextCompat.getDrawable(requireContext(),
                isSaved ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_border));

        // Show feedback message
        String message = isSaved ? "Recipe saved" : "Recipe removed from saved";
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
    }

    private void observeViewModel() {
        viewModel.getRecipeDetails().observe(getViewLifecycleOwner(), this::updateUI);
    }

    private void updateUI(Recipe recipe) {
        if (recipe == null) return;

        binding.recipeName.setText(recipe.getName());

        binding.recipeDescription.setText(recipe.getDescription());
        binding.caloriesText.setText(getString(R.string.calories_format, recipe.getCalories()));

        Glide.with(this)
                .load(recipe.getImageUrl())
                .placeholder(R.drawable.placeholder_recipe)
                .error(R.drawable.error_recipe)
                .into(binding.recipeImage);

        StringBuilder ingredientsText = new StringBuilder();
        for (String ingredient : recipe.getIngredients()) {
            ingredientsText.append("• ").append(ingredient).append("\n");
        }
        binding.ingredientsList.setText(ingredientsText.toString().trim());

        StringBuilder stepsText = new StringBuilder();
        for (int i = 0; i < recipe.getSteps().size(); i++) {
            stepsText.append(i + 1).append(". ")
                    .append(recipe.getSteps().get(i))
                    .append("\n\n");
        }
        binding.stepsList.setText(stepsText.toString().trim());

        binding.allergensChipGroup.removeAllViews();
        for (String allergen : recipe.getAllergens()) {
            Chip chip = new Chip(requireContext());
            chip.setText(allergen);
            binding.allergensChipGroup.addView(chip);
        }
    }
}