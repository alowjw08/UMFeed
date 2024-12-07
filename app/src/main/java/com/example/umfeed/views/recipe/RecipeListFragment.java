package com.example.umfeed.views.recipe;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.appcompat.widget.SearchView;

import com.example.umfeed.adapters.RecipeAdapter;
import com.example.umfeed.databinding.FragmentRecipeListBinding;
import com.example.umfeed.viewmodels.recipe.RecipeViewModel;
import com.example.umfeed.viewmodels.recipe.SavedRecipeViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;


public class RecipeListFragment extends Fragment {
    private RecipeViewModel viewModel;
    private RecipeAdapter adapter;
    private FragmentRecipeListBinding binding;
    private static final String TAG = "RecipeListFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecipeListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupSearchView();
        setupFilterChips();
        setupFilterButton();
        observeViewModel();
    }

    private void setupRecyclerView() {
        SavedRecipeViewModel savedRecipeViewModel = new ViewModelProvider(this).get(SavedRecipeViewModel.class);
        Log.d("RecipeListFragment", "Setting up RecyclerView");

        adapter = new RecipeAdapter(recipe -> {
            Log.d("RecipeListFragment", "Recipe clicked in fragment: " + recipe.getName());

            if (recipe.getId() == null) {
                Log.e("RecipeListFragment", "Recipe ID is null!");
                Snackbar.make(binding.getRoot(), "Error: Cannot view recipe details",
                        Snackbar.LENGTH_SHORT).show();
                return;
            }

            try {
                NavDirections action = RecipeListFragmentDirections
                        .actionRecipeListToDetail(recipe.getId());
                Navigation.findNavController(requireView()).navigate(action);
                Log.d("RecipeListFragment", "Navigation action executed");
            } catch (Exception e) {
                Log.e("RecipeListFragment", "Navigation failed", e);
                Snackbar.make(binding.getRoot(), "Error viewing recipe details",
                        Snackbar.LENGTH_SHORT).show();
            }
        });

        binding.recipesRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));
        binding.recipesRecyclerView.setAdapter(adapter);
        binding.recipesRecyclerView.setVisibility(View.VISIBLE);

        Log.d("RecipeListFragment", "RecyclerView setup completed");
    }

    private void setupSearchView() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Add clear button listener
        binding.searchView.setOnCloseListener(() -> {
            // Clear the search query
            binding.searchView.setQuery("", false);

            // Clear any selected filter chips
            binding.filterChipGroup.clearCheck();

            // Optional: Collapse the SearchView
            binding.searchView.onActionViewCollapsed();

            clearFilters();

            return true; // Return true to indicate we handled the close
        });
        // Also handle the clear button (X) click
        binding.searchView.setOnQueryTextFocusChangeListener((view, hasFocus) -> {
            // When focus is lost and query is empty, reload all recipes
            if (!hasFocus && binding.searchView.getQuery().length() == 0) {
                viewModel.loadRecipes();
            }
        });
    }

    private void performSearch(String query) {
        // Show loading state
        binding.progressBar.setVisibility(View.VISIBLE);

        // Hide keyboard after search
        InputMethodManager imm = (InputMethodManager) requireContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.searchView.getWindowToken(), 0);

        // Perform search
        viewModel.searchRecipes(query);
    }

    private void setupFilterButton() {
        binding.filterFab.setOnClickListener(v -> {
            NutritionFilterBottomSheet filterSheet = new NutritionFilterBottomSheet();
            filterSheet.show(getChildFragmentManager(), "nutrition_filter");
        });
    }

    private void setupFilterChips() {
        String[] categories = {"QuickMeal", "HighProtein", "BudgetFriendly", "Vegan", "LowFats", "LowCarbs"};
        binding.filterChipGroup.removeAllViews();

        // Add filter chips dynamically
        for (String category : categories) {
            Chip chip = new Chip(requireContext());
            chip.setText(category);
            chip.setCheckable(true);
            chip.setId(View.generateViewId());
            binding.filterChipGroup.addView(chip);

            // Add individual click listener for debugging
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    Log.d("RecipeListFragment", "Chip selected: " + category);
                }
            });
        }

        binding.filterChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == View.NO_ID) {
                // No chip selected - show all recipes
                Log.d("RecipeListFragment", "No category selected - loading all recipes");
                viewModel.loadRecipes();
            } else {
                Chip chip = group.findViewById(checkedId);
                if (chip != null) {
                    String category = chip.getText().toString();
                    Log.d("RecipeListFragment", "Filtering by category: " + category);
                    viewModel.filterRecipesByCategory(category);
                }
            }
        });

        //Allow deselection
        binding.filterChipGroup.setSingleSelection(true);
        binding.filterChipGroup.setSelectionRequired(false);
    }
    private void clearFilters() {
        binding.filterChipGroup.clearCheck();
        viewModel.loadRecipes();
    }

    private void observeViewModel() {
        Log.d(TAG, "Setting up observers");

        // Main recipes observer
        viewModel.getRecipes().observe(getViewLifecycleOwner(), recipes -> {
            Log.d(TAG, "Recipe observer triggered");
            binding.progressBar.setVisibility(View.GONE);

            if (recipes == null) {
                Log.d(TAG, "Received null recipes list - waiting for filter");
                return;
            }

            Log.d(TAG, "Received recipes list size: " + recipes.size());

            if (recipes.isEmpty()) {
                Log.d(TAG, "Showing empty state for empty list");
                showEmptyState();
            } else {
                Log.d(TAG, "Updating adapter with recipes");
                binding.recipesRecyclerView.setVisibility(View.VISIBLE);
                binding.emptyStateView.setVisibility(View.GONE);
                adapter.submitList(recipes);
            }
        });

        // Error observer
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            Log.d(TAG, "Error observer triggered: " + error);
            if (error != null) {
                binding.progressBar.setVisibility(View.GONE);
                Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_LONG).show();
            }
        });

        // Loading observer
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            Log.d(TAG, "Loading observer triggered: " + isLoading);
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }
    private void showEmptyState() {
        binding.recipesRecyclerView.setVisibility(View.GONE);
        binding.emptyStateView.setVisibility(View.VISIBLE);
        binding.emptyStateView.setText("No recipes match the selected nutrition criteria.\n" +
                "Try adjusting the ranges or switch to 'Match Any' mode.");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Fragment resumed");
    }
}