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

public class RecipeDetailFragment extends Fragment {
    private RecipeViewModel viewModel;
    private SavedRecipeViewModel savedRecipeViewModel;
    private FragmentRecipeDetailBinding binding;
    private String recipeId;

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
        viewModel.loadRecipeDetails(recipeId);
    }

    private void setupViews() {
        binding.saveButton.setOnClickListener(v -> {
            savedRecipeViewModel.saveRecipe(recipeId);
        });

        binding.likeButton.setOnClickListener(v -> {
            viewModel.likeRecipe(recipeId);
        });
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