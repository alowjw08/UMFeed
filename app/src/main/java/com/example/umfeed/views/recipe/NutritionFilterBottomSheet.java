package com.example.umfeed.views.recipe;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.umfeed.databinding.NutritionFilterSheetBinding;
import com.example.umfeed.models.recipe.NutritionFilter;
import com.example.umfeed.models.recipe.Range;
import com.example.umfeed.viewmodels.recipe.RecipeViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;

public class NutritionFilterBottomSheet extends BottomSheetDialogFragment {
    private NutritionFilterSheetBinding binding;
    private RecipeViewModel viewModel;
    private SharedPreferences preferences;
    private boolean isInitialSetup = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = NutritionFilterSheetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);
        preferences = requireContext().getSharedPreferences("nutrition_filters", Context.MODE_PRIVATE);

        isInitialSetup = true;
        setupSliders();
        isInitialSetup = false;
        setupApplyButton();
        loadSavedValues();
    }

    private void setupSliders() {
        // Set initial values
        binding.carbohydrateSlider.setValues(Arrays.asList(0f, 100f));
        binding.proteinSlider.setValues(Arrays.asList(0f, 50f));
        binding.fatsSlider.setValues(Arrays.asList(0f, 30f));

        // Add logging to verify initialization
        Log.d("NutritionFilter", "Initial slider values set:" +
                "\nCarbs: " + binding.carbohydrateSlider.getValues() +
                "\nProtein: " + binding.proteinSlider.getValues() +
                "\nFats: " + binding.fatsSlider.getValues());

        binding.carbohydrateSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (!isInitialSetup && fromUser) {  // Only react to user changes
                List<Float> values = slider.getValues();
                binding.carbRange.setText(String.format("%dg - %dg",
                        Math.round(values.get(0)), Math.round(values.get(1))));
                Log.d("NutritionFilter", "Carb slider changed: " + values);
            }
        });

        binding.proteinSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (!isInitialSetup && fromUser) {  // Only react to user changes
                List<Float> values = slider.getValues();
                binding.proteinRange.setText(String.format("%dg - %dg",
                        Math.round(values.get(0)), Math.round(values.get(1))));
                Log.d("NutritionFilter", "Protein slider changed: " + values);
            }
        });

        binding.fatsSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (!isInitialSetup && fromUser) {  // Only react to user changes
                List<Float> values = slider.getValues();
                binding.fatsRange.setText(String.format("%dg - %dg",
                        Math.round(values.get(0)), Math.round(values.get(1))));
                Log.d("NutritionFilter", "Fats slider changed: " + values);
            }
        });
    }

    private void setupApplyButton() {
        binding.applyButton.setOnClickListener(v -> {
            try {
                // Add logging to verify slider values
                List<Float> carbValues = binding.carbohydrateSlider.getValues();
                List<Float> proteinValues = binding.proteinSlider.getValues();
                List<Float> fatValues = binding.fatsSlider.getValues();

                Log.d("NutritionFilter", String.format(
                        "Slider Values - Carbs: %s, Protein: %s, Fats: %s",
                        carbValues.toString(),
                        proteinValues.toString(),
                        fatValues.toString()
                ));

                // Create ranges with explicit logging
                Range carbRange = new Range(carbValues.get(0), carbValues.get(1));
                Range proteinRange = new Range(proteinValues.get(0), proteinValues.get(1));
                Range fatRange = new Range(fatValues.get(0), fatValues.get(1));

                Log.d("NutritionFilter", String.format(
                        "Created Ranges - Carbs: %.1f-%.1f, Protein: %.1f-%.1f, Fats: %.1f-%.1f",
                        carbRange.getMin(), carbRange.getMax(),
                        proteinRange.getMin(), proteinRange.getMax(),
                        fatRange.getMin(), fatRange.getMax()
                ));

                NutritionFilter filter = new NutritionFilter(
                        carbRange,
                        proteinRange,
                        fatRange,
                        binding.matchingCriteriaSwitch.isChecked()
                );

                viewModel.clearRecipes();
                viewModel.getRecipesByNutrition(filter);
                dismiss();

            } catch (Exception e) {
                Log.e("NutritionFilter", "Error creating filter", e);
                showError("Error applying filter: " + e.getMessage());
            }
        });
    }

    private void showError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }
    private void loadSavedValues() {
        float carbMin = preferences.getFloat("carb_min", 40);
        float carbMax = preferences.getFloat("carb_max", 200);
        binding.carbohydrateSlider.setValues(carbMin, carbMax);

        float proteinMin = preferences.getFloat("protein_min", 5);
        float proteinMax = preferences.getFloat("protein_max", 50);
        binding.proteinSlider.setValues(proteinMin, proteinMax);

        float fatMin = preferences.getFloat("fat_min", 5);
        float fatMax = preferences.getFloat("fat_max", 15);
        binding.fatsSlider.setValues(fatMin, fatMax);
    }
}
